package com.campus.lease.controller;

import com.campus.lease.common.result.Result;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @PostMapping("/create")
    public Result<Map<String, Object>> createPayment(@RequestBody Map<String, Object> request) {
        Long orderId = Long.valueOf(request.get("orderId").toString());
        
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("timestamp", System.currentTimeMillis());
        result.put("nonceStr", generateNonceStr());
        result.put("package", "prepay_id=wx1234567890");
        result.put("signType", "MD5");
        result.put("paySign", "mock_pay_sign");
        
        return Result.success(result);
    }

    @PostMapping("/notify")
    public String paymentNotify(@RequestBody String notifyData) {
        System.out.println("收到微信支付回调: " + notifyData);
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }

    @GetMapping("/query/{orderId}")
    public Result<Map<String, Object>> queryPayment(@PathVariable Long orderId) {
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("status", "SUCCESS");
        result.put("transactionId", "4200001234567890");
        
        return Result.success(result);
    }

    @PostMapping("/refund")
    public Result<Map<String, Object>> refund(@RequestBody Map<String, Object> request) {
        Long orderId = Long.valueOf(request.get("orderId").toString());
        String reason = request.get("reason").toString();
        
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("refundId", "RF" + System.currentTimeMillis());
        result.put("status", "PROCESSING");
        
        return Result.success(result);
    }

    private String generateNonceStr() {
        return Long.toHexString(System.currentTimeMillis());
    }
}
