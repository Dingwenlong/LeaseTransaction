package com.campus.lease.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.lease.dto.ItemPublishRequest;
import com.campus.lease.entity.Item;
import com.campus.lease.mapper.ItemMapper;
import com.campus.lease.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements ItemService {

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
        item.setStatus(1);
        item.setViewCount(0);
        item.setFavoriteCount(0);
        save(item);
        log.info("物品发布成功，itemId: {}", item.getId());
        return item;
    }

    @Override
    public Page<Item> getItemList(Integer pageNum, Integer pageSize, String category, Integer type, String campus, String keyword) {
        Page<Item> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Item> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Item::getStatus, 1);

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

        wrapper.orderByDesc(Item::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public Item getItemDetail(Long itemId) {
        return getById(itemId);
    }

    @Override
    public void updateViewCount(Long itemId) {
        Item item = getById(itemId);
        if (item != null) {
            item.setViewCount(item.getViewCount() + 1);
            updateById(item);
        }
    }
}
