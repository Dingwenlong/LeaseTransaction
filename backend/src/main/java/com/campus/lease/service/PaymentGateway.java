package com.campus.lease.service;

import com.campus.lease.dto.PaymentGatewayNotifyResult;
import com.campus.lease.dto.PaymentGatewayPrepayRequest;
import com.campus.lease.dto.PaymentNotifyHeaders;

import java.util.Map;

public interface PaymentGateway {
    Map<String, Object> createPrepay(PaymentGatewayPrepayRequest request);

    PaymentGatewayNotifyResult parsePaymentNotify(PaymentNotifyHeaders headers, String body);
}
