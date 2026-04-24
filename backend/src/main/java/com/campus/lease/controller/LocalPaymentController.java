package com.campus.lease.controller;

import com.campus.lease.common.result.Result;
import com.campus.lease.service.PaymentService;
import com.campus.lease.support.AuthContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "本地支付模拟", description = "非生产环境用于小程序演示的支付确认接口")
@Profile("!prod")
@RestController
@RequestMapping("/api/payment/local")
@RequiredArgsConstructor
public class LocalPaymentController {

    private final PaymentService paymentService;
    private final AuthContext authContext;

    @Operation(summary = "本地确认支付", description = "仅非生产环境可用，复用支付状态机完成本地演示支付")
    @PostMapping("/confirm/{paymentNo}")
    public Result<Map<String, Object>> confirm(@PathVariable String paymentNo) {
        Long userId = authContext.requireCurrentUserId();
        return Result.success(paymentService.confirmLocalPayment(userId, paymentNo));
    }
}
