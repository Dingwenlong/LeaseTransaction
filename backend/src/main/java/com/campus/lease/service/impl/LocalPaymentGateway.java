package com.campus.lease.service.impl;

import com.campus.lease.dto.PaymentGatewayNotifyResult;
import com.campus.lease.dto.PaymentGatewayPrepayRequest;
import com.campus.lease.dto.PaymentNotifyHeaders;
import com.campus.lease.service.PaymentGateway;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
@Profile("!prod")
@RequiredArgsConstructor
public class LocalPaymentGateway implements PaymentGateway {

    private final ObjectMapper objectMapper;

    @Override
    public Map<String, Object> createPrepay(PaymentGatewayPrepayRequest request) {
        Map<String, Object> result = new HashMap<>();
        result.put("timeStamp", String.valueOf(Instant.now().getEpochSecond()));
        result.put("timestamp", result.get("timeStamp"));
        result.put("nonceStr", Long.toHexString(System.nanoTime()));
        result.put("package", "prepay_id=local_" + request.outTradeNo());
        result.put("signType", "RSA");
        result.put("paySign", "LOCAL_PAY_SIGN_" + request.outTradeNo());
        return result;
    }

    @Override
    public PaymentGatewayNotifyResult parsePaymentNotify(PaymentNotifyHeaders headers, String body) {
        try {
            JsonNode root = objectMapper.readTree(body);
            String outTradeNo = root.path("out_trade_no").asText(root.path("outTradeNo").asText());
            String transactionId = root.path("transaction_id").asText(root.path("transactionId").asText("LOCAL-" + outTradeNo));
            BigDecimal amount = new BigDecimal(root.path("amount").path("total").asText(root.path("amount").asText("0")))
                    .movePointLeft(2);
            boolean success = !"FAIL".equalsIgnoreCase(root.path("trade_state").asText("SUCCESS"));
            return new PaymentGatewayNotifyResult(outTradeNo, transactionId, amount, success);
        } catch (Exception exception) {
            throw new IllegalArgumentException("支付回调报文解析失败", exception);
        }
    }
}
