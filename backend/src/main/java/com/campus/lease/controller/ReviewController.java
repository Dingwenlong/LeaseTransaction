package com.campus.lease.controller;

import com.campus.lease.common.result.Result;
import com.campus.lease.dto.ReviewSubmitRequest;
import com.campus.lease.service.ReviewService;
import com.campus.lease.support.AuthContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "评价管理", description = "订单评价提交与评价查询接口")
@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final AuthContext authContext;

    @Operation(summary = "提交评价", description = "用户对订单完成后的交易对象提交评分和评价内容")
    @PostMapping("/submit")
    public Result<Map<String, Object>> submitReview(@RequestBody ReviewSubmitRequest request) {
        Long userId = authContext.getCurrentUserIdOrDefault(2L);
        return Result.success(reviewService.submitReview(userId, request));
    }

    @Operation(summary = "查询订单评价", description = "根据订单 ID 查询该订单下的全部评价记录")
    @GetMapping("/order/{orderId}")
    public Result<List<Map<String, Object>>> getOrderReviews(
            @Parameter(description = "订单 ID", example = "1")
            @PathVariable Long orderId
    ) {
        return Result.success(reviewService.getOrderReviews(orderId));
    }
}
