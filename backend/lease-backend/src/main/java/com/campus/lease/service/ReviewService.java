package com.campus.lease.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.lease.dto.ReviewSubmitRequest;
import com.campus.lease.entity.Review;

import java.util.List;
import java.util.Map;

public interface ReviewService extends IService<Review> {
    Map<String, Object> submitReview(Long userId, ReviewSubmitRequest request);
    List<Map<String, Object>> getOrderReviews(Long orderId);
}
