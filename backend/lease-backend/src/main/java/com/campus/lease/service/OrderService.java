package com.campus.lease.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.lease.dto.CreateOrderRequest;
import com.campus.lease.dto.OrderStatusUpdateRequest;
import com.campus.lease.entity.Order;

import java.util.Map;

public interface OrderService extends IService<Order> {
    Map<String, Object> createOrder(Long userId, CreateOrderRequest request);
    Page<Map<String, Object>> getOrderList(Long userId, Integer pageNum, Integer pageSize, Integer status, Integer type, String keyword, boolean adminView);
    Map<String, Object> getOrderDetail(Long orderId);
    void updateOrderStatus(Long operatorUserId, Long orderId, OrderStatusUpdateRequest request);
}
