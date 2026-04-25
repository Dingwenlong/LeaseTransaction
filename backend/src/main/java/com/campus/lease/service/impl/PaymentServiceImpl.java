package com.campus.lease.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.lease.common.constant.BusinessConstants;
import com.campus.lease.common.exception.BusinessException;
import com.campus.lease.dto.OrderStatusUpdateRequest;
import com.campus.lease.dto.PaymentCreateRequest;
import com.campus.lease.dto.PaymentGatewayNotifyResult;
import com.campus.lease.dto.PaymentGatewayPrepayRequest;
import com.campus.lease.dto.PaymentNotifyHeaders;
import com.campus.lease.dto.PaymentRefundRequest;
import com.campus.lease.entity.Order;
import com.campus.lease.entity.PaymentRecord;
import com.campus.lease.entity.User;
import com.campus.lease.mapper.PaymentRecordMapper;
import com.campus.lease.service.OrderService;
import com.campus.lease.service.PaymentGateway;
import com.campus.lease.service.PaymentService;
import com.campus.lease.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderService orderService;
    private final UserService userService;
    private final PaymentRecordMapper paymentRecordMapper;
    private final PaymentGateway paymentGateway;

    @Value("${wechat.pay.app-id:local-app}")
    private String appId;

    @Value("${wechat.pay.mch-id:local-mch}")
    private String mchId;

    @Value("${wechat.pay.notify-url:http://127.0.0.1:8081/api/payment/notify}")
    private String notifyUrl;

    @Override
    @Transactional
    public Map<String, Object> createPayment(Long userId, PaymentCreateRequest request) {
        Order order = requireBuyerOrder(userId, request.getOrderId());
        if (order.getStatus() == null || order.getStatus() != BusinessConstants.OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException("当前订单状态不支持支付");
        }

        PaymentRecord record = findLatestRecord(order.getId(), BusinessConstants.PaymentType.PAYMENT);
        if (record == null || record.getStatus() == null || record.getStatus() != BusinessConstants.PaymentStatus.PROCESSING) {
            record = new PaymentRecord();
            record.setPaymentNo("PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
            record.setOrderId(order.getId());
            record.setUserId(userId);
            record.setType(BusinessConstants.PaymentType.PAYMENT);
            record.setAmount(safeAmount(order.getTotalAmount()));
            record.setPaymentMethod(1);
            record.setStatus(BusinessConstants.PaymentStatus.PROCESSING);
            paymentRecordMapper.insert(record);
        }

        User user = userService.getById(userId);
        Map<String, Object> result = paymentGateway.createPrepay(new PaymentGatewayPrepayRequest(
                appId,
                mchId,
                "校园租赁交易订单 " + order.getOrderNo(),
                record.getPaymentNo(),
                notifyUrl,
                user == null ? "" : StringUtils.defaultString(user.getOpenid()),
                record.getAmount()
        ));
        result.put("paymentNo", record.getPaymentNo());
        result.put("orderId", order.getId());
        return result;
    }

    @Override
    @Transactional
    public void handlePaymentNotify(PaymentNotifyHeaders headers, String notifyData) {
        PaymentGatewayNotifyResult notifyResult = paymentGateway.parsePaymentNotify(headers, notifyData);
        PaymentRecord record = requirePaymentRecord(notifyResult.outTradeNo());
        Order order = orderService.getById(record.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (record.getStatus() != null && record.getStatus() == BusinessConstants.PaymentStatus.SUCCESS) {
            return;
        }
        if (safeAmount(record.getAmount()).compareTo(safeAmount(notifyResult.amount())) != 0) {
            record.setStatus(BusinessConstants.PaymentStatus.FAILED);
            record.setFailureReason("支付回调金额不一致");
            paymentRecordMapper.updateById(record);
            throw new BusinessException("支付回调金额不一致");
        }
        if (!notifyResult.success()) {
            record.setStatus(BusinessConstants.PaymentStatus.FAILED);
            record.setFailureReason("微信支付回调状态失败");
            paymentRecordMapper.updateById(record);
            return;
        }

        completeSuccessfulPayment(record, order, notifyResult.transactionId());
    }

    @Override
    @Transactional
    public Map<String, Object> confirmLocalPayment(Long userId, String paymentNo) {
        PaymentRecord record = requirePaymentRecord(paymentNo);
        if (!userId.equals(record.getUserId())) {
            throw new BusinessException("无权操作该支付流水");
        }
        if (record.getType() == null || record.getType() != BusinessConstants.PaymentType.PAYMENT) {
            throw new BusinessException("该流水不是支付单");
        }

        Order order = orderService.getById(record.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!userId.equals(order.getBuyerId())) {
            throw new BusinessException("无权操作该订单支付");
        }
        if (record.getStatus() != null && record.getStatus() == BusinessConstants.PaymentStatus.SUCCESS) {
            return buildPaymentResult(order, record);
        }
        if (record.getStatus() == null || record.getStatus() != BusinessConstants.PaymentStatus.PROCESSING) {
            throw new BusinessException("当前支付流水状态不支持确认");
        }
        if (order.getStatus() == null || order.getStatus() != BusinessConstants.OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException("当前订单状态不支持本地支付确认");
        }
        if (safeAmount(record.getAmount()).compareTo(safeAmount(order.getTotalAmount())) != 0) {
            record.setStatus(BusinessConstants.PaymentStatus.FAILED);
            record.setFailureReason("本地支付金额与订单金额不一致");
            paymentRecordMapper.updateById(record);
            throw new BusinessException("本地支付金额与订单金额不一致");
        }

        completeSuccessfulPayment(record, order, "LOCAL-" + paymentNo);
        return buildPaymentResult(order, record);
    }

    private void completeSuccessfulPayment(PaymentRecord record, Order order, String transactionId) {
        record.setStatus(BusinessConstants.PaymentStatus.SUCCESS);
        record.setTransactionId(transactionId);
        paymentRecordMapper.updateById(record);

        if (order.getStatus() != null && order.getStatus() == BusinessConstants.OrderStatus.PENDING_PAYMENT) {
            OrderStatusUpdateRequest updateRequest = new OrderStatusUpdateRequest();
            updateRequest.setStatus(BusinessConstants.OrderStatus.PAID);
            updateRequest.setRemark("支付成功，流水号：" + transactionId);
            orderService.updateOrderStatus(0L, order.getId(), updateRequest);
            order.setStatus(BusinessConstants.OrderStatus.PAID);
        }
    }

    @Override
    public Map<String, Object> queryPayment(Long userId, Long orderId) {
        requireBuyerOrder(userId, orderId);
        PaymentRecord record = findLatestRecord(orderId, BusinessConstants.PaymentType.PAYMENT);
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("paymentNo", record == null ? "" : record.getPaymentNo());
        result.put("status", record == null ? "PENDING" : getPaymentStatusCode(record.getStatus()));
        result.put("transactionId", record == null ? "" : record.getTransactionId());
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> refund(Long userId, PaymentRefundRequest request) {
        Order order = requireBuyerOrder(userId, request.getOrderId());
        if (order.getStatus() == null || order.getStatus() < BusinessConstants.OrderStatus.PAID
                || order.getStatus() == BusinessConstants.OrderStatus.CANCELLED) {
            throw new BusinessException("当前订单状态不支持退款");
        }

        PaymentRecord existing = findLatestRecord(order.getId(), BusinessConstants.PaymentType.REFUND);
        if (existing != null && existing.getStatus() != null
                && existing.getStatus() == BusinessConstants.PaymentStatus.PROCESSING) {
            return buildRefundResult(order.getId(), existing);
        }

        OrderStatusUpdateRequest updateRequest = new OrderStatusUpdateRequest();
        updateRequest.setStatus(BusinessConstants.OrderStatus.REFUNDING);
        updateRequest.setRemark(request.getReason());
        orderService.updateOrderStatus(userId, order.getId(), updateRequest);

        PaymentRecord record = new PaymentRecord();
        record.setPaymentNo("RF" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        record.setOrderId(order.getId());
        record.setUserId(userId);
        record.setType(BusinessConstants.PaymentType.REFUND);
        record.setAmount(resolveRefundAmount(order));
        record.setPaymentMethod(1);
        record.setStatus(BusinessConstants.PaymentStatus.PROCESSING);
        record.setFailureReason(StringUtils.defaultString(request.getReason()));
        paymentRecordMapper.insert(record);
        return buildRefundResult(order.getId(), record);
    }

    @Override
    public Map<String, Object> getSummary(Long userId) {
        LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentRecord::getUserId, userId).orderByDesc(PaymentRecord::getCreateTime);
        var records = paymentRecordMapper.selectList(wrapper);

        BigDecimal paidAmount = records.stream()
                .filter(record -> record.getType() != null && record.getType() == BusinessConstants.PaymentType.PAYMENT)
                .filter(record -> record.getStatus() != null && record.getStatus() == BusinessConstants.PaymentStatus.SUCCESS)
                .map(PaymentRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal refundingAmount = records.stream()
                .filter(record -> record.getType() != null && record.getType() == BusinessConstants.PaymentType.REFUND)
                .filter(record -> record.getStatus() != null && record.getStatus() == BusinessConstants.PaymentStatus.PROCESSING)
                .map(PaymentRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal refundedAmount = records.stream()
                .filter(record -> record.getType() != null && record.getType() == BusinessConstants.PaymentType.REFUND)
                .filter(record -> record.getStatus() != null && record.getStatus() == BusinessConstants.PaymentStatus.SUCCESS)
                .map(PaymentRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> result = new HashMap<>();
        result.put("paidAmount", paidAmount);
        result.put("depositFrozen", BigDecimal.ZERO);
        result.put("refundingAmount", refundingAmount);
        result.put("refundedAmount", refundedAmount);
        result.put("recordCount", records.size());
        return result;
    }

    @Override
    public Page<Map<String, Object>> getRecords(Long userId, Integer page, Integer size) {
        Page<PaymentRecord> paymentPage = new Page<>(page, size);
        LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentRecord::getUserId, userId).orderByDesc(PaymentRecord::getCreateTime);
        Page<PaymentRecord> entityPage = paymentRecordMapper.selectPage(paymentPage, wrapper);

        Page<Map<String, Object>> result = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        result.setRecords(entityPage.getRecords().stream().map(record -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", record.getId());
            item.put("orderId", record.getOrderId());
            item.put("paymentNo", record.getPaymentNo());
            item.put("amount", record.getAmount());
            item.put("type", record.getType());
            item.put("typeText", getPaymentTypeText(record.getType()));
            item.put("status", record.getStatus());
            item.put("statusText", getPaymentStatusText(record.getStatus()));
            item.put("transactionId", record.getTransactionId());
            item.put("createdAt", record.getCreateTime());
            return item;
        }).toList());
        return result;
    }

    private Order requireBuyerOrder(Long userId, Long orderId) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!userId.equals(order.getBuyerId())) {
            throw new BusinessException("无权操作该订单支付");
        }
        return order;
    }

    private PaymentRecord requirePaymentRecord(String paymentNo) {
        LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentRecord::getPaymentNo, paymentNo).last("limit 1");
        PaymentRecord record = paymentRecordMapper.selectOne(wrapper);
        if (record == null) {
            throw new BusinessException("支付流水不存在");
        }
        return record;
    }

    private PaymentRecord findLatestRecord(Long orderId, Integer type) {
        LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentRecord::getOrderId, orderId)
                .eq(PaymentRecord::getType, type)
                .orderByDesc(PaymentRecord::getCreateTime)
                .last("limit 1");
        return paymentRecordMapper.selectOne(wrapper);
    }

    private Map<String, Object> buildRefundResult(Long orderId, PaymentRecord record) {
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("refundId", record.getPaymentNo());
        result.put("status", getPaymentStatusCode(record.getStatus()));
        return result;
    }

    private Map<String, Object> buildPaymentResult(Order order, PaymentRecord record) {
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getId());
        result.put("orderStatus", order.getStatus());
        result.put("paymentNo", record.getPaymentNo());
        result.put("paymentStatus", getPaymentStatusCode(record.getStatus()));
        result.put("transactionId", StringUtils.defaultString(record.getTransactionId()));
        return result;
    }

    private BigDecimal resolveRefundAmount(Order order) {
        BigDecimal deposit = safeAmount(order.getDeposit());
        if (order.getType() != null && order.getType() == BusinessConstants.OrderType.LEASE && deposit.compareTo(BigDecimal.ZERO) > 0) {
            return deposit;
        }
        return safeAmount(order.getTotalAmount());
    }

    private BigDecimal safeAmount(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(2, RoundingMode.HALF_UP);
    }

    private String getPaymentStatusCode(Integer status) {
        if (status == null) {
            return "UNKNOWN";
        }
        return switch (status) {
            case BusinessConstants.PaymentStatus.SUCCESS -> "SUCCESS";
            case BusinessConstants.PaymentStatus.PROCESSING -> "PROCESSING";
            case BusinessConstants.PaymentStatus.FAILED -> "FAILED";
            default -> "UNKNOWN";
        };
    }

    private String getPaymentStatusText(Integer status) {
        return switch (status == null ? -1 : status) {
            case BusinessConstants.PaymentStatus.SUCCESS -> "成功";
            case BusinessConstants.PaymentStatus.PROCESSING -> "处理中";
            case BusinessConstants.PaymentStatus.FAILED -> "失败";
            default -> "未知";
        };
    }

    private String getPaymentTypeText(Integer type) {
        if (type == null) {
            return "未知";
        }
        return switch (type) {
            case BusinessConstants.PaymentType.PAYMENT -> "支付";
            case BusinessConstants.PaymentType.REFUND -> "退款";
            case BusinessConstants.PaymentType.DEPOSIT -> "押金冻结";
            default -> "未知";
        };
    }
}
