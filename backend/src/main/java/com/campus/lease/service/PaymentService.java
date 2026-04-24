package com.campus.lease.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.lease.dto.PaymentCreateRequest;
import com.campus.lease.dto.PaymentNotifyHeaders;
import com.campus.lease.dto.PaymentRefundRequest;

import java.util.Map;

public interface PaymentService {
    Map<String, Object> createPayment(Long userId, PaymentCreateRequest request);

    void handlePaymentNotify(PaymentNotifyHeaders headers, String notifyData);

    Map<String, Object> confirmLocalPayment(Long userId, String paymentNo);

    Map<String, Object> queryPayment(Long userId, Long orderId);

    Map<String, Object> refund(Long userId, PaymentRefundRequest request);

    Map<String, Object> getSummary(Long userId);

    Page<Map<String, Object>> getRecords(Long userId, Integer page, Integer size);
}
