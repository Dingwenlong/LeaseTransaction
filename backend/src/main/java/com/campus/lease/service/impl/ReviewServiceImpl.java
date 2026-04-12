package com.campus.lease.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.lease.common.exception.BusinessException;
import com.campus.lease.dto.ReviewSubmitRequest;
import com.campus.lease.entity.Item;
import com.campus.lease.entity.Order;
import com.campus.lease.entity.Review;
import com.campus.lease.entity.User;
import com.campus.lease.mapper.ReviewMapper;
import com.campus.lease.service.ItemService;
import com.campus.lease.service.MessageService;
import com.campus.lease.service.OrderService;
import com.campus.lease.service.ReviewService;
import com.campus.lease.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    private final OrderService orderService;
    private final UserService userService;
    private final ItemService itemService;
    private final MessageService messageService;

    @Override
    @Transactional
    public Map<String, Object> submitReview(Long userId, ReviewSubmitRequest request) {
        if (request.getOrderId() == null) {
            throw new BusinessException("缺少订单信息");
        }
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new BusinessException("评分必须在 1 到 5 之间");
        }
        if (StringUtils.isBlank(request.getContent())) {
            throw new BusinessException("请填写评价内容");
        }

        Order order = orderService.getById(request.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() == null || order.getStatus() != 5) {
            throw new BusinessException("仅已完成订单支持评价");
        }
        if (!userId.equals(order.getBuyerId()) && !userId.equals(order.getSellerId())) {
            throw new BusinessException("无权评价该订单");
        }

        LambdaQueryWrapper<Review> existsWrapper = new LambdaQueryWrapper<>();
        existsWrapper.eq(Review::getOrderId, request.getOrderId())
                .eq(Review::getReviewerId, userId);
        if (count(existsWrapper) > 0) {
            throw new BusinessException("当前订单已评价，无需重复提交");
        }

        Long revieweeId = userId.equals(order.getBuyerId()) ? order.getSellerId() : order.getBuyerId();
        Review review = new Review();
        review.setOrderId(order.getId());
        review.setItemId(order.getItemId());
        review.setReviewerId(userId);
        review.setRevieweeId(revieweeId);
        review.setRating(request.getRating());
        review.setContent(request.getContent().trim());
        review.setImages(StringUtils.defaultString(request.getImages()));
        review.setIsAnonymous(request.getIsAnonymous() == null ? 0 : request.getIsAnonymous());
        save(review);

        messageService.sendSystemMessage(
                revieweeId,
                "收到新的评价",
                "订单 " + order.getOrderNo() + " 已收到一条新的 " + request.getRating() + " 星评价。"
        );

        return convertToReviewMap(review);
    }

    @Override
    public List<Map<String, Object>> getOrderReviews(Long orderId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getOrderId, orderId).orderByDesc(Review::getCreateTime);
        return list(wrapper).stream().map(this::convertToReviewMap).toList();
    }

    private Map<String, Object> convertToReviewMap(Review review) {
        User reviewer = review.getReviewerId() == null ? null : userService.getById(review.getReviewerId());
        User reviewee = review.getRevieweeId() == null ? null : userService.getById(review.getRevieweeId());
        Item item = review.getItemId() == null ? null : itemService.getById(review.getItemId());

        Map<String, Object> result = new HashMap<>();
        result.put("id", review.getId());
        result.put("orderId", review.getOrderId());
        result.put("itemId", review.getItemId());
        result.put("itemTitle", item == null ? "物品已下架" : item.getTitle());
        result.put("reviewerId", review.getReviewerId());
        result.put("revieweeId", review.getRevieweeId());
        result.put("reviewerName", resolveName(reviewer, review.getIsAnonymous()));
        result.put("revieweeName", resolveName(reviewee, 0));
        result.put("rating", review.getRating());
        result.put("content", review.getContent());
        result.put("images", review.getImages());
        result.put("isAnonymous", review.getIsAnonymous() == null ? 0 : review.getIsAnonymous());
        result.put("createdAt", review.getCreateTime());
        return result;
    }

    private String resolveName(User user, Integer anonymousFlag) {
        if (anonymousFlag != null && anonymousFlag == 1) {
            return "匿名用户";
        }
        if (user == null) {
            return "校园用户";
        }
        return StringUtils.defaultIfBlank(user.getNickname(), user.getStudentId());
    }
}
