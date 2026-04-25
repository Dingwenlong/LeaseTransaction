package com.campus.lease.dto;

import java.math.BigDecimal;

public record PaymentGatewayPrepayRequest(
        String appId,
        String mchId,
        String description,
        String outTradeNo,
        String notifyUrl,
        String payerOpenid,
        BigDecimal amount
) {
}
