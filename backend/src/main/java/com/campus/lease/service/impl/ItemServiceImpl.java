package com.campus.lease.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.lease.common.constant.BusinessConstants;
import com.campus.lease.common.exception.BusinessException;
import com.campus.lease.dto.ItemAuditRequest;
import com.campus.lease.dto.ItemPublishRequest;
import com.campus.lease.entity.Item;
import com.campus.lease.entity.User;
import com.campus.lease.mapper.ItemMapper;
import com.campus.lease.service.ItemService;
import com.campus.lease.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements ItemService {

    private final UserService userService;

    @Override
    public Item publishItem(Long userId, ItemPublishRequest request) {
        Item item = new Item();
        item.setUserId(userId);
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setImages(request.getImages());
        item.setCategory(request.getCategory());
        item.setType(request.getType());
        item.setPrice(request.getPrice());
        item.setDeposit(request.getDeposit());
        item.setCampus(request.getCampus());
        item.setStatus(BusinessConstants.ItemStatus.PENDING_REVIEW);
        item.setViewCount(0);
        item.setFavoriteCount(0);
        save(item);
        log.info("物品发布成功，itemId: {}", item.getId());
        return item;
    }

    @Override
    public Page<Map<String, Object>> getItemList(Integer pageNum, Integer pageSize, String category, Integer type, String campus, String keyword, Integer status) {
        LambdaQueryWrapper<Item> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(category)) {
            wrapper.eq(Item::getCategory, category);
        }
        if (type != null) {
            wrapper.eq(Item::getType, type);
        }
        if (StringUtils.isNotBlank(campus)) {
            wrapper.eq(Item::getCampus, campus);
        }
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Item::getTitle, keyword).or().like(Item::getDescription, keyword));
        }
        if (status != null) {
            wrapper.eq(Item::getStatus, status);
        }

        List<Item> matchedItems = new ArrayList<>(list(wrapper));
        Map<Long, User> ownerCache = buildOwnerCache(matchedItems);
        matchedItems.sort(buildExposureComparator(ownerCache));

        long total = matchedItems.size();
        int fromIndex = Math.max(0, (pageNum - 1) * pageSize);
        int toIndex = Math.min(matchedItems.size(), fromIndex + pageSize);
        List<Map<String, Object>> records = fromIndex >= toIndex
                ? new ArrayList<>()
                : matchedItems.subList(fromIndex, toIndex).stream()
                .map(item -> convertToItemMap(item, ownerCache.get(item.getUserId())))
                .toList();

        Page<Map<String, Object>> result = new Page<>(pageNum, pageSize, total);
        result.setRecords(records);
        return result;
    }

    @Override
    public Item getItemDetail(Long itemId) {
        return getById(itemId);
    }

    @Override
    public Map<String, Object> getItemDetailView(Long itemId) {
        Item item = getById(itemId);
        if (item == null) {
            throw new BusinessException("物品不存在");
        }
        return convertToItemMap(item, item.getUserId() == null ? null : userService.getById(item.getUserId()));
    }

    @Override
    public void updateViewCount(Long itemId) {
        Item item = getById(itemId);
        if (item != null) {
            item.setViewCount((item.getViewCount() == null ? 0 : item.getViewCount()) + 1);
            updateById(item);
        }
    }

    @Override
    public Page<Map<String, Object>> getMyItems(Long userId, Integer pageNum, Integer pageSize) {
        Page<Item> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Item> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Item::getUserId, userId).orderByDesc(Item::getCreateTime);
        Page<Item> entityPage = page(page, wrapper);
        Page<Map<String, Object>> result = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        result.setRecords(entityPage.getRecords().stream()
                .map(item -> convertToItemMap(item, item.getUserId() == null ? null : userService.getById(item.getUserId())))
                .toList());
        return result;
    }

    @Override
    public void auditItem(Long itemId, ItemAuditRequest request) {
        Item item = getById(itemId);
        if (item == null) {
            throw new BusinessException("物品不存在");
        }

        int targetStatus = request.getStatus() == null
                ? BusinessConstants.ItemStatus.ACTIVE
                : request.getStatus();
        item.setStatus(targetStatus);
        if (StringUtils.isNotBlank(request.getReason())) {
            item.setDescription(item.getDescription() + "\n审核备注：" + request.getReason());
        }
        updateById(item);
    }

    @Override
    public void updateItemStatus(Long itemId, Long userId, Integer status) {
        Item item = getById(itemId);
        if (item == null) {
            throw new BusinessException("物品不存在");
        }
        if (userId != null && userId > 0 && item.getUserId() != null && !item.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该物品");
        }
        if (status == null) {
            throw new BusinessException("缺少目标状态");
        }
        if (status != BusinessConstants.ItemStatus.ACTIVE && status != BusinessConstants.ItemStatus.OFFLINE) {
            throw new BusinessException("仅支持上架或下架操作");
        }
        if (item.getStatus() != null && item.getStatus() == BusinessConstants.ItemStatus.SOLD) {
            throw new BusinessException("已售出物品不能修改状态");
        }
        if (item.getStatus() != null && item.getStatus() == BusinessConstants.ItemStatus.LEASING) {
            throw new BusinessException("履约中的物品暂不能修改状态");
        }

        item.setStatus(status);
        updateById(item);
    }

    @Override
    public List<Map<String, Object>> getNearbyItems(String campus, Integer limit) {
        LambdaQueryWrapper<Item> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Item::getStatus, BusinessConstants.ItemStatus.ACTIVE);
        if (StringUtils.isNotBlank(campus)) {
            wrapper.eq(Item::getCampus, campus);
        }
        List<Item> items = new ArrayList<>(list(wrapper));
        Map<Long, User> ownerCache = buildOwnerCache(items);
        items.sort(buildExposureComparator(ownerCache));
        return items.stream()
                .limit(Math.max(1, limit))
                .map(item -> convertToItemMap(item, ownerCache.get(item.getUserId())))
                .toList();
    }

    private Map<String, Object> convertToItemMap(Item item, User owner) {
        List<String> imageList = parseImages(item.getImages());
        Map<String, Object> map = new HashMap<>();
        map.put("id", item.getId());
        map.put("title", item.getTitle());
        map.put("description", item.getDescription());
        map.put("category", item.getCategory());
        map.put("type", item.getType());
        map.put("typeText", item.getType() != null && item.getType() == BusinessConstants.OrderType.LEASE ? "租赁" : "出售");
        map.put("price", item.getPrice());
        map.put("deposit", item.getDeposit());
        map.put("campus", item.getCampus());
        map.put("status", item.getStatus());
        map.put("statusText", getStatusText(item.getStatus()));
        map.put("ownerId", item.getUserId());
        map.put("ownerName", owner == null ? "校园用户" : StringUtils.defaultIfBlank(owner.getNickname(), owner.getStudentId()));
        map.put("ownerVerified", owner != null && owner.getIsVerified() != null ? owner.getIsVerified() : 0);
        map.put("ownerCreditScore", owner != null && owner.getCreditScore() != null ? owner.getCreditScore() : BusinessConstants.Credit.DEFAULT_SCORE);
        map.put("ownerCreditLevel", resolveCreditLevel(owner == null ? null : owner.getCreditScore()));
        map.put("viewCount", item.getViewCount() == null ? 0 : item.getViewCount());
        map.put("favoriteCount", item.getFavoriteCount() == null ? 0 : item.getFavoriteCount());
        map.put("images", imageList);
        map.put("coverImage", imageList.isEmpty() ? "" : imageList.get(0));
        map.put("createdAt", item.getCreateTime());
        map.put("updatedAt", item.getUpdateTime());
        map.put("reviewHint", getReviewHint(item.getStatus()));
        return map;
    }

    private Map<Long, User> buildOwnerCache(List<Item> items) {
        Map<Long, User> cache = new HashMap<>();
        for (Item item : items) {
            if (item.getUserId() == null || cache.containsKey(item.getUserId())) {
                continue;
            }
            cache.put(item.getUserId(), userService.getById(item.getUserId()));
        }
        return cache;
    }

    private Comparator<Item> buildExposureComparator(Map<Long, User> ownerCache) {
        return Comparator
                .comparing((Item item) -> resolveOwnerCreditScore(item, ownerCache), Comparator.reverseOrder())
                .thenComparing((Item item) -> resolveOwnerVerified(item, ownerCache), Comparator.reverseOrder())
                .thenComparing(item -> item.getViewCount() == null ? 0 : item.getViewCount(), Comparator.reverseOrder())
                .thenComparing(item -> item.getFavoriteCount() == null ? 0 : item.getFavoriteCount(), Comparator.reverseOrder())
                .thenComparing(Item::getCreateTime, Comparator.nullsLast(Comparator.reverseOrder()));
    }

    private int resolveOwnerCreditScore(Item item, Map<Long, User> ownerCache) {
        User owner = item.getUserId() == null ? null : ownerCache.get(item.getUserId());
        return owner == null || owner.getCreditScore() == null
                ? BusinessConstants.Credit.DEFAULT_SCORE
                : owner.getCreditScore();
    }

    private int resolveOwnerVerified(Item item, Map<Long, User> ownerCache) {
        User owner = item.getUserId() == null ? null : ownerCache.get(item.getUserId());
        return owner == null || owner.getIsVerified() == null ? 0 : owner.getIsVerified();
    }

    private String resolveCreditLevel(Integer score) {
        int actualScore = score == null ? BusinessConstants.Credit.DEFAULT_SCORE : score;
        if (actualScore >= 90) {
            return "优秀";
        }
        if (actualScore >= 70) {
            return "良好";
        }
        if (actualScore >= 50) {
            return "一般";
        }
        return "较差";
    }

    private List<String> parseImages(String images) {
        if (StringUtils.isBlank(images)) {
            return new ArrayList<>();
        }
        return Arrays.stream(images.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .toList();
    }

    private String getStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case BusinessConstants.ItemStatus.PENDING_REVIEW -> "待审核";
            case BusinessConstants.ItemStatus.ACTIVE -> "已上架";
            case BusinessConstants.ItemStatus.LEASING -> "租赁中";
            case BusinessConstants.ItemStatus.SOLD -> "已售出";
            case BusinessConstants.ItemStatus.OFFLINE -> "已下架";
            case BusinessConstants.ItemStatus.REJECTED -> "已驳回";
            default -> "未知";
        };
    }

    private String getReviewHint(Integer status) {
        if (status == null) {
            return "请完善信息后提交审核";
        }
        return switch (status) {
            case BusinessConstants.ItemStatus.PENDING_REVIEW -> "检查图片、成色和价格说明后尽快处理。";
            case BusinessConstants.ItemStatus.ACTIVE -> "已对外展示，可继续观察曝光和收藏表现。";
            case BusinessConstants.ItemStatus.LEASING -> "物品正在履约中，注意归还时间与异常提醒。";
            case BusinessConstants.ItemStatus.SOLD -> "已完成交易，可作为成交案例保留。";
            case BusinessConstants.ItemStatus.OFFLINE -> "当前处于下架状态，可按需重新上架。";
            case BusinessConstants.ItemStatus.REJECTED -> "建议补充描述或图片后重新提交。";
            default -> "等待进一步处理。";
        };
    }
}
