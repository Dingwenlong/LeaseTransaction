package com.campus.lease.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.lease.dto.CreateOrderRequest;
import com.campus.lease.entity.Order;

public interface OrderService extends IService<Order> {
    Order createOrder(Long userId, CreateOrderRequest request);
    Page<Order> getOrderList(Long userId, Integer pageNum, Integer pageSize, Integer status);
    Order getOrderDetail(Long orderId);
    void updateOrderStatus(Long orderId, Integer status);
}
