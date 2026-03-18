package com.campus.lease.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.lease.common.exception.UnauthorizedException;
import com.campus.lease.common.result.Result;
import com.campus.lease.dto.CreateOrderRequest;
import com.campus.lease.dto.OrderStatusUpdateRequest;
import com.campus.lease.service.OrderService;
import com.campus.lease.support.AdminAccessGuard;
import com.campus.lease.support.AuthContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AuthContext authContext;
    private final AdminAccessGuard adminAccessGuard;

    @PostMapping("/create")
    public Result<Map<String, Object>> createOrder(@RequestBody CreateOrderRequest request) {
        log.info("订单创建请求，itemId: {}", request.getItemId());
        Long userId = authContext.getCurrentUserIdOrDefault(2L);
        return Result.success(orderService.createOrder(userId, request));
    }

    @GetMapping("/list")
    public Result<Page<Map<String, Object>>> getOrderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "false") Boolean adminView
    ) {
        Long userId = authContext.getCurrentUserIdOrDefault(2L);
        if (Boolean.TRUE.equals(adminView)) {
            adminAccessGuard.requireAdminId();
        }
        return Result.success(orderService.getOrderList(userId, page, size, status, type, keyword, adminView));
    }

    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> getOrderDetail(@PathVariable Long id) {
        return Result.success(orderService.getOrderDetail(id));
    }

    @PostMapping("/status/{id}")
    public Result<Void> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody OrderStatusUpdateRequest request
    ) {
        Long adminId = authContext.getCurrentAdminId();
        Long operatorUserId = adminId != null ? 0L : authContext.getCurrentUserId();
        if (operatorUserId == null) {
            throw new UnauthorizedException("请先登录后再操作订单");
        }
        orderService.updateOrderStatus(operatorUserId, id, request);
        return Result.success();
    }
}
