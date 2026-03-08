package com.campus.lease.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.lease.dto.CreateOrderRequest;
import com.campus.lease.entity.Item;
import com.campus.lease.entity.Order;
import com.campus.lease.mapper.OrderMapper;
import com.campus.lease.service.ItemService;
import com.campus.lease.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final ItemService itemService;

    @Override
    @Transactional
    public Order createOrder(Long userId, CreateOrderRequest request) {
        Item item = itemService.getItemDetail(request.getItemId());
        if (item == null) {
            throw new RuntimeException("物品不存在");
        }

        String orderNo = generateOrderNo();

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setItemId(request.getItemId());
        order.setSellerId(item.getUserId());
        order.setBuyerId(userId);
        order.setType(request.getType());
        order.setStatus(1);
        order.setTotalAmount(item.getPrice());
        order.setDeposit(item.getDeposit());
        order.setRentalPrice(item.getPrice());
        order.setStartDate(request.getStartDate());
        order.setEndDate(request.getEndDate());
        order.setRentalDays(request.getRentalDays());
        order.setDeliveryMethod(request.getDeliveryMethod());
        order.setRemark(request.getRemark());

        save(order);
        log.info("订单创建成功，orderNo: {}", orderNo);
        return order;
    }

    @Override
    public Page<Order> getOrderList(Long userId, Integer pageNum, Integer pageSize, Integer status) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Order::getBuyerId, userId).or().eq(Order::getSellerId, userId));

        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }

        wrapper.orderByDesc(Order::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public Order getOrderDetail(Long orderId) {
        return getById(orderId);
    }

    @Override
    public void updateOrderStatus(Long orderId, Integer status) {
        Order order = getById(orderId);
        if (order != null) {
            order.setStatus(status);
            updateById(order);
            log.info("订单状态更新成功，orderId: {}, status: {}", orderId, status);
        }
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD" + timestamp + uuid;
    }
}
