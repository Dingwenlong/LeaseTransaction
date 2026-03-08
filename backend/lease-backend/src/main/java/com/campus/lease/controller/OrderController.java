package com.campus.lease.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.lease.common.result.Result;
import com.campus.lease.dto.CreateOrderRequest;
import com.campus.lease.entity.Order;
import com.campus.lease.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public Result<Order> createOrder(@RequestBody CreateOrderRequest request) {
        log.info("订单创建请求，itemId: {}", request.getItemId());
        Long userId = 1L;
        Order order = orderService.createOrder(userId, request);
        return Result.success(order);
    }

    @GetMapping("/list")
    public Result<Page<Order>> getOrderList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status
    ) {
        Long userId = 1L;
        Page<Order> page = orderService.getOrderList(userId, pageNum, pageSize, status);
        return Result.success(page);
    }

    @GetMapping("/detail/{id}")
    public Result<Order> getOrderDetail(@PathVariable Long id) {
        Order order = orderService.getOrderDetail(id);
        return Result.success(order);
    }

    @PostMapping("/status/{id}")
    public Result<Void> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Integer status
    ) {
        orderService.updateOrderStatus(id, status);
        return Result.success();
    }
}
