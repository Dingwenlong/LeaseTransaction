package com.campus.lease.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.lease.common.constant.BusinessConstants;
import com.campus.lease.common.exception.BusinessException;
import com.campus.lease.dto.CreateOrderRequest;
import com.campus.lease.dto.OrderStatusUpdateRequest;
import com.campus.lease.entity.Item;
import com.campus.lease.entity.LeaseRecord;
import com.campus.lease.entity.Order;
import com.campus.lease.entity.PaymentRecord;
import com.campus.lease.entity.User;
import com.campus.lease.mapper.LeaseRecordMapper;
import com.campus.lease.mapper.OrderMapper;
import com.campus.lease.mapper.PaymentRecordMapper;
import com.campus.lease.service.CreditService;
import com.campus.lease.service.ItemService;
import com.campus.lease.service.MessageService;
import com.campus.lease.service.OrderService;
import com.campus.lease.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final ItemService itemService;
    private final UserService userService;
    private final MessageService messageService;
    private final LeaseRecordMapper leaseRecordMapper;
    private final PaymentRecordMapper paymentRecordMapper;
    private final CreditService creditService;

    @Override
    @Transactional
    public Map<String, Object> createOrder(Long userId, CreateOrderRequest request) {
        Item item = itemService.getItemDetail(request.getItemId());
        if (item == null) {
            throw new BusinessException("物品不存在");
        }
        if (item.getUserId() != null && item.getUserId().equals(userId)) {
            throw new BusinessException("不能购买或租赁自己发布的物品");
        }
        if (item.getStatus() == null || item.getStatus() != BusinessConstants.ItemStatus.ACTIVE) {
            throw new BusinessException("当前物品暂不可下单");
        }

        int orderType = request.getType() == null ? item.getType() : request.getType();
        if (orderType != BusinessConstants.OrderType.LEASE && orderType != BusinessConstants.OrderType.SALE) {
            throw new BusinessException("订单类型不合法");
        }

        if (orderType == BusinessConstants.OrderType.SALE) {
            ensureSaleItemAvailable(item.getId());
        }

        LocalDateTime startDate = request.getStartDate();
        LocalDateTime endDate = request.getEndDate();
        Integer rentalDays = request.getRentalDays();
        BigDecimal rentalAmount = orderType == BusinessConstants.OrderType.LEASE ? calculateLeaseAmount(item, startDate, endDate, rentalDays) : item.getPrice();
        if (orderType == BusinessConstants.OrderType.LEASE) {
            startDate = resolveStartDate(startDate);
            rentalDays = resolveRentalDays(startDate, endDate, rentalDays);
            endDate = startDate.plusDays(rentalDays);
            validateLeasePeriod(item.getId(), startDate, endDate);
            rentalAmount = safeAmount(item.getPrice()).multiply(BigDecimal.valueOf(rentalDays));
        } else {
            startDate = null;
            endDate = null;
            rentalDays = null;
        }

        BigDecimal deposit = orderType == BusinessConstants.OrderType.LEASE ? safeAmount(item.getDeposit()) : BigDecimal.ZERO;
        BigDecimal totalAmount = rentalAmount.add(deposit).setScale(2, RoundingMode.HALF_UP);

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setItemId(item.getId());
        order.setSellerId(item.getUserId());
        order.setBuyerId(userId);
        order.setType(orderType);
        order.setStatus(BusinessConstants.OrderStatus.PENDING_PAYMENT);
        order.setTotalAmount(totalAmount);
        order.setDeposit(deposit);
        order.setRentalPrice(rentalAmount);
        order.setStartDate(startDate);
        order.setEndDate(endDate);
        order.setRentalDays(rentalDays);
        order.setDeliveryMethod(StringUtils.defaultIfBlank(request.getDeliveryMethod(), "校内面交"));
        order.setRemark(StringUtils.defaultString(request.getRemark()));
        save(order);

        if (orderType == BusinessConstants.OrderType.LEASE) {
            ensureLeaseRecord(order);
        }

        notifyOrderCreated(order);
        log.info("订单创建成功，orderNo: {}", order.getOrderNo());
        return convertToOrderMap(order);
    }

    @Override
    public Page<Map<String, Object>> getOrderList(Long userId, Integer pageNum, Integer pageSize, Integer status, Integer type, String keyword, boolean adminView) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (!adminView) {
            wrapper.and(query -> query.eq(Order::getBuyerId, userId).or().eq(Order::getSellerId, userId));
        }
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        if (type != null) {
            wrapper.eq(Order::getType, type);
        }
        wrapper.orderByDesc(Order::getCreateTime);

        List<Order> allOrders = list(wrapper);
        List<Map<String, Object>> mappedOrders = allOrders.stream()
                .map(this::convertToOrderMap)
                .filter(orderMap -> matchesKeyword(orderMap, keyword))
                .toList();

        long total = mappedOrders.size();
        int fromIndex = Math.max(0, (pageNum - 1) * pageSize);
        int toIndex = Math.min(mappedOrders.size(), fromIndex + pageSize);
        List<Map<String, Object>> records = fromIndex >= toIndex
                ? new ArrayList<>()
                : mappedOrders.subList(fromIndex, toIndex);

        Page<Map<String, Object>> result = new Page<>(pageNum, pageSize, total);
        result.setRecords(records);
        return result;
    }

    @Override
    public Map<String, Object> getOrderDetail(Long orderId, Long requesterUserId, boolean adminView) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!adminView && requesterUserId != null
                && !requesterUserId.equals(order.getBuyerId())
                && !requesterUserId.equals(order.getSellerId())) {
            throw new BusinessException("无权查看该订单");
        }
        return convertToOrderMap(order);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long operatorUserId, Long orderId, OrderStatusUpdateRequest request) {
        if (request.getStatus() == null) {
            throw new BusinessException("缺少订单状态");
        }

        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        Integer previousStatus = order.getStatus();
        int targetStatus = request.getStatus();
        assertOperatorCanUpdateOrder(order, operatorUserId);
        if (StringUtils.isNotBlank(request.getRemark())) {
            order.setRemark(request.getRemark());
        }

        order.setStatus(targetStatus);
        updateById(order);
        syncLeaseRecord(order);
        syncItemStatus(order, targetStatus);

        if (targetStatus == BusinessConstants.OrderStatus.COMPLETED) {
            creditService.applyRule(order.getBuyerId(), BusinessConstants.Credit.SUCCESSFUL_TRANSACTION, order.getId(), null);
            creditService.applyRule(order.getSellerId(), BusinessConstants.Credit.SUCCESSFUL_TRANSACTION, order.getId(), null);
        }
        if (targetStatus == BusinessConstants.OrderStatus.CANCELLED) {
            if (operatorUserId != null
                    && operatorUserId > 0
                    && previousStatus != null
                    && (previousStatus == BusinessConstants.OrderStatus.PAID
                    || previousStatus == BusinessConstants.OrderStatus.IN_PROGRESS
                    || previousStatus == BusinessConstants.OrderStatus.PENDING_RETURN)) {
                creditService.applyRule(operatorUserId, BusinessConstants.Credit.BREACH, order.getId(), "订单取消造成违约");
            }
            messageService.sendSystemMessage(order.getBuyerId(), "订单已取消", "订单 " + order.getOrderNo() + " 已取消。");
            messageService.sendSystemMessage(order.getSellerId(), "订单已取消", "订单 " + order.getOrderNo() + " 已被取消。");
            return;
        }

        String statusText = getOrderStatusText(targetStatus);
        messageService.sendSystemMessage(order.getBuyerId(), "订单状态更新", "订单 " + order.getOrderNo() + " 当前状态：" + statusText);
        messageService.sendSystemMessage(order.getSellerId(), "订单状态更新", "订单 " + order.getOrderNo() + " 当前状态：" + statusText);
        log.info("订单状态更新成功，orderId: {}, status: {}, operatorUserId: {}", orderId, targetStatus, operatorUserId);
    }

    private void ensureSaleItemAvailable(Long itemId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getItemId, itemId)
                .eq(Order::getType, BusinessConstants.OrderType.SALE)
                .in(Order::getStatus,
                        BusinessConstants.OrderStatus.PENDING_PAYMENT,
                        BusinessConstants.OrderStatus.PAID,
                        BusinessConstants.OrderStatus.IN_PROGRESS,
                        BusinessConstants.OrderStatus.COMPLETED);
        if (count(wrapper) > 0) {
            throw new BusinessException("该物品已有有效交易订单");
        }
    }

    private LocalDateTime resolveStartDate(LocalDateTime startDate) {
        return startDate == null ? LocalDateTime.now().plusHours(2).truncatedTo(ChronoUnit.HOURS) : startDate;
    }

    private Integer resolveRentalDays(LocalDateTime startDate, LocalDateTime endDate, Integer rentalDays) {
        if (rentalDays != null && rentalDays > 0) {
            return rentalDays;
        }
        if (endDate == null) {
            return 1;
        }
        long days = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
        return (int) Math.max(days, 1);
    }

    private BigDecimal calculateLeaseAmount(Item item, LocalDateTime startDate, LocalDateTime endDate, Integer rentalDays) {
        if (item.getType() == null || item.getType() != BusinessConstants.OrderType.LEASE) {
            return safeAmount(item.getPrice());
        }
        int days = resolveRentalDays(resolveStartDate(startDate), endDate, rentalDays);
        return safeAmount(item.getPrice()).multiply(BigDecimal.valueOf(days)).setScale(2, RoundingMode.HALF_UP);
    }

    private void validateLeasePeriod(Long itemId, LocalDateTime startDate, LocalDateTime endDate) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getItemId, itemId)
                .eq(Order::getType, BusinessConstants.OrderType.LEASE)
                .in(Order::getStatus,
                        BusinessConstants.OrderStatus.PENDING_PAYMENT,
                        BusinessConstants.OrderStatus.PAID,
                        BusinessConstants.OrderStatus.IN_PROGRESS,
                        BusinessConstants.OrderStatus.PENDING_RETURN);
        List<Order> orders = list(wrapper);
        for (Order order : orders) {
            if (order.getStartDate() == null || order.getEndDate() == null) {
                continue;
            }
            boolean overlap = !endDate.isBefore(order.getStartDate()) && !startDate.isAfter(order.getEndDate());
            if (overlap) {
                throw new BusinessException("该时间段已被预约，请重新选择租赁时间");
            }
        }
    }

    private void notifyOrderCreated(Order order) {
        messageService.sendSystemMessage(order.getBuyerId(), "订单创建成功", "订单 " + order.getOrderNo() + " 已创建，请尽快完成支付。");
        messageService.sendSystemMessage(order.getSellerId(), "收到新的订单申请", "订单 " + order.getOrderNo() + " 正等待买家支付。");
    }

    private void ensureLeaseRecord(Order order) {
        LambdaQueryWrapper<LeaseRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LeaseRecord::getOrderId, order.getId());
        LeaseRecord record = leaseRecordMapper.selectOne(wrapper);
        if (record == null) {
            record = new LeaseRecord();
            record.setOrderId(order.getId());
            record.setItemId(order.getItemId());
            record.setUserId(order.getSellerId());
            record.setLesseeId(order.getBuyerId());
            record.setLeaseStart(order.getStartDate());
            record.setLeaseEnd(order.getEndDate());
            record.setIsOverdue(0);
            record.setOverdueFee(BigDecimal.ZERO);
            record.setDamageCompensation(BigDecimal.ZERO);
            record.setStatus(1);
            leaseRecordMapper.insert(record);
        }
    }

    private void syncLeaseRecord(Order order) {
        if (order.getType() == null || order.getType() != BusinessConstants.OrderType.LEASE) {
            return;
        }

        LambdaQueryWrapper<LeaseRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LeaseRecord::getOrderId, order.getId());
        LeaseRecord record = leaseRecordMapper.selectOne(wrapper);
        if (record == null) {
            ensureLeaseRecord(order);
            record = leaseRecordMapper.selectOne(wrapper);
        }
        if (record == null) {
            return;
        }

        if (order.getStatus() != null && order.getStatus() == BusinessConstants.OrderStatus.COMPLETED) {
            record.setActualReturn(LocalDateTime.now());
            record.setStatus(2);
        } else {
            record.setStatus(1);
        }
        leaseRecordMapper.updateById(record);
    }

    private void syncItemStatus(Order order, int targetStatus) {
        Item item = itemService.getById(order.getItemId());
        if (item == null) {
            return;
        }

        if (targetStatus == BusinessConstants.OrderStatus.CANCELLED || targetStatus == BusinessConstants.OrderStatus.REFUNDING) {
            item.setStatus(hasOtherActiveOrder(order.getItemId(), order.getId())
                    ? (order.getType() == BusinessConstants.OrderType.LEASE ? BusinessConstants.ItemStatus.LEASING : BusinessConstants.ItemStatus.SOLD)
                    : BusinessConstants.ItemStatus.ACTIVE);
        } else if (targetStatus == BusinessConstants.OrderStatus.PAID
                || targetStatus == BusinessConstants.OrderStatus.IN_PROGRESS
                || targetStatus == BusinessConstants.OrderStatus.PENDING_RETURN) {
            item.setStatus(order.getType() == BusinessConstants.OrderType.LEASE
                    ? BusinessConstants.ItemStatus.LEASING
                    : BusinessConstants.ItemStatus.SOLD);
        } else if (targetStatus == BusinessConstants.OrderStatus.COMPLETED) {
            item.setStatus(order.getType() == BusinessConstants.OrderType.LEASE
                    ? (hasOtherActiveOrder(order.getItemId(), order.getId()) ? BusinessConstants.ItemStatus.LEASING : BusinessConstants.ItemStatus.ACTIVE)
                    : BusinessConstants.ItemStatus.SOLD);
        }

        itemService.updateById(item);
    }

    private boolean hasOtherActiveOrder(Long itemId, Long currentOrderId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getItemId, itemId)
                .ne(Order::getId, currentOrderId)
                .in(Order::getStatus,
                        BusinessConstants.OrderStatus.PENDING_PAYMENT,
                        BusinessConstants.OrderStatus.PAID,
                        BusinessConstants.OrderStatus.IN_PROGRESS,
                        BusinessConstants.OrderStatus.PENDING_RETURN);
        return count(wrapper) > 0;
    }

    private void assertOperatorCanUpdateOrder(Order order, Long operatorUserId) {
        if (operatorUserId == null || operatorUserId == 0L) {
            return;
        }
        if (!operatorUserId.equals(order.getBuyerId()) && !operatorUserId.equals(order.getSellerId())) {
            throw new BusinessException("无权操作该订单");
        }
    }

    private boolean matchesKeyword(Map<String, Object> orderMap, String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return true;
        }
        String normalizedKeyword = keyword.toLowerCase();
        return contains(orderMap.get("orderNo"), normalizedKeyword)
                || contains(orderMap.get("itemTitle"), normalizedKeyword)
                || contains(orderMap.get("buyerName"), normalizedKeyword)
                || contains(orderMap.get("sellerName"), normalizedKeyword);
    }

    private boolean contains(Object value, String keyword) {
        return value != null && value.toString().toLowerCase().contains(keyword);
    }

    private Map<String, Object> convertToOrderMap(Order order) {
        Item item = order.getItemId() == null ? null : itemService.getById(order.getItemId());
        User buyer = order.getBuyerId() == null ? null : userService.getById(order.getBuyerId());
        User seller = order.getSellerId() == null ? null : userService.getById(order.getSellerId());

        Map<String, Object> map = new HashMap<>();
        map.put("id", order.getId());
        map.put("orderNo", order.getOrderNo());
        map.put("itemId", order.getItemId());
        map.put("itemTitle", item == null ? "物品已删除" : item.getTitle());
        map.put("itemImage", resolveItemImage(item));
        map.put("buyerId", order.getBuyerId());
        map.put("buyerName", buyer == null ? "校园用户" : StringUtils.defaultIfBlank(buyer.getNickname(), buyer.getStudentId()));
        map.put("sellerId", order.getSellerId());
        map.put("sellerName", seller == null ? "校园用户" : StringUtils.defaultIfBlank(seller.getNickname(), seller.getStudentId()));
        map.put("type", order.getType());
        map.put("typeText", order.getType() != null && order.getType() == BusinessConstants.OrderType.LEASE ? "租赁" : "交易");
        map.put("status", order.getStatus());
        map.put("statusText", getOrderStatusText(order.getStatus()));
        map.put("amount", order.getType() != null && order.getType() == BusinessConstants.OrderType.LEASE ? order.getRentalPrice() : order.getTotalAmount());
        map.put("deposit", order.getDeposit());
        map.put("totalAmount", order.getTotalAmount());
        map.put("rentalPrice", order.getRentalPrice());
        map.put("rentalDays", order.getRentalDays());
        map.put("startDate", order.getStartDate());
        map.put("endDate", order.getEndDate());
        map.put("deliveryMethod", order.getDeliveryMethod());
        map.put("remark", order.getRemark());
        map.put("createdAt", order.getCreateTime());
        map.put("updatedAt", order.getUpdateTime());
        putPaymentSummary(map, order.getId());
        return map;
    }

    private void putPaymentSummary(Map<String, Object> map, Long orderId) {
        LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentRecord::getOrderId, orderId)
                .eq(PaymentRecord::getType, BusinessConstants.PaymentType.PAYMENT)
                .orderByDesc(PaymentRecord::getCreateTime)
                .last("limit 1");
        PaymentRecord record = paymentRecordMapper.selectOne(wrapper);
        map.put("paymentNo", record == null ? "" : record.getPaymentNo());
        map.put("paymentStatus", record == null ? "" : getPaymentStatusText(record.getStatus()));
        map.put("transactionId", record == null ? "" : StringUtils.defaultString(record.getTransactionId()));
    }

    private String resolveItemImage(Item item) {
        if (item == null || StringUtils.isBlank(item.getImages())) {
            return "";
        }
        return item.getImages().split(",")[0].trim();
    }

    private String getOrderStatusText(Integer status) {
        if (status == null) {
            return "未知状态";
        }
        return switch (status) {
            case BusinessConstants.OrderStatus.PENDING_PAYMENT -> "待付款";
            case BusinessConstants.OrderStatus.PAID -> "待交付";
            case BusinessConstants.OrderStatus.IN_PROGRESS -> "进行中";
            case BusinessConstants.OrderStatus.PENDING_RETURN -> "待归还验收";
            case BusinessConstants.OrderStatus.COMPLETED -> "已完成";
            case BusinessConstants.OrderStatus.CANCELLED -> "已取消";
            case BusinessConstants.OrderStatus.DISPUTE -> "纠纷中";
            case BusinessConstants.OrderStatus.REFUNDING -> "退款中";
            default -> "未知状态";
        };
    }

    private String getPaymentStatusText(Integer status) {
        return switch (status == null ? -1 : status) {
            case BusinessConstants.PaymentStatus.SUCCESS -> "成功";
            case BusinessConstants.PaymentStatus.PROCESSING -> "处理中";
            case BusinessConstants.PaymentStatus.FAILED -> "失败";
            default -> "";
        };
    }

    private BigDecimal safeAmount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value.setScale(2, RoundingMode.HALF_UP);
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
