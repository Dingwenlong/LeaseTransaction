package com.campus.lease.dto;

import java.math.BigDecimal;

public record PaymentGatewayNotifyResult(
        String outTradeNo,
        String transactionId,
        BigDecimal amount,
        boolean success
) {
}
