package com.campus.lease.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.lease.common.result.Result;
import com.campus.lease.dto.CreateOrderRequest;
import com.campus.lease.dto.OrderStatusUpdateRequest;
import com.campus.lease.service.OrderService;
import com.campus.lease.support.AdminAccessGuard;
import com.campus.lease.support.AuthContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Tag(name = "订单管理", description = "订单创建、分页查询、详情查看和状态流转接口")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AuthContext authContext;
    private final AdminAccessGuard adminAccessGuard;

    @Operation(summary = "创建订单", description = "用户基于物品创建租赁或出售订单")
    @PostMapping("/create")
    public Result<Map<String, Object>> createOrder(@RequestBody CreateOrderRequest request) {
        log.info("订单创建请求，itemId: {}", request.getItemId());
        Long userId = authContext.requireCurrentUserId();
        return Result.success(orderService.createOrder(userId, request));
    }

    @Operation(summary = "分页查询订单列表", description = "查询当前用户订单；adminView=true 时切换为后台订单视图并要求管理员身份")
    @GetMapping("/list")
    public Result<Page<Map<String, Object>>> getOrderList(
            @Parameter(description = "页码，从 1 开始", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "订单状态", example = "3")
            @RequestParam(required = false) Integer status,
            @Parameter(description = "订单类型，1 为租赁，2 为出售", example = "1")
            @RequestParam(required = false) Integer type,
            @Parameter(description = "关键字，可匹配订单号或物品标题等", example = "20260318")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "是否启用后台视图", example = "false")
            @RequestParam(defaultValue = "false") Boolean adminView
    ) {
        if (Boolean.TRUE.equals(adminView)) {
            adminAccessGuard.requireAdminId();
            return Result.success(orderService.getOrderList(null, page, size, status, type, keyword, true));
        }
        Long userId = authContext.requireCurrentUserId();
        return Result.success(orderService.getOrderList(userId, page, size, status, type, keyword, false));
    }

    @Operation(summary = "获取订单详情", description = "根据订单 ID 查询订单详情")
    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> getOrderDetail(
            @Parameter(description = "订单 ID", example = "1")
            @PathVariable Long id
    ) {
        Long adminId = authContext.getCurrentAdminIdOrNull();
        if (adminId != null) {
            return Result.success(orderService.getOrderDetail(id, null, true));
        }
        Long userId = authContext.requireCurrentUserId();
        return Result.success(orderService.getOrderDetail(id, userId, false));
    }

    @Operation(summary = "更新订单状态", description = "用户或后台根据业务流转修改订单状态，并可附带备注说明")
    @PostMapping("/status/{id}")
    public Result<Void> updateOrderStatus(
            @Parameter(description = "订单 ID", example = "1")
            @PathVariable Long id,
            @RequestBody OrderStatusUpdateRequest request
    ) {
        Long adminId = authContext.getCurrentAdminIdOrNull();
        Long operatorUserId = adminId != null ? 0L : authContext.requireCurrentUserId();
        orderService.updateOrderStatus(operatorUserId, id, request);
        return Result.success();
    }
}
