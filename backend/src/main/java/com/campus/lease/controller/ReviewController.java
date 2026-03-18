package com.campus.lease.controller;

import com.campus.lease.common.result.Result;
import com.campus.lease.dto.ReviewSubmitRequest;
import com.campus.lease.service.ReviewService;
import com.campus.lease.support.AuthContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final AuthContext authContext;

    @PostMapping("/submit")
    public Result<Map<String, Object>> submitReview(@RequestBody ReviewSubmitRequest request) {
        Long userId = authContext.getCurrentUserIdOrDefault(2L);
        return Result.success(reviewService.submitReview(userId, request));
    }

    @GetMapping("/order/{orderId}")
    public Result<List<Map<String, Object>>> getOrderReviews(@PathVariable Long orderId) {
        return Result.success(reviewService.getOrderReviews(orderId));
    }
}
