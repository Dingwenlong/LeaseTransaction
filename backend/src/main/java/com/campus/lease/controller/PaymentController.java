package com.campus.lease.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.lease.common.result.Result;
import com.campus.lease.dto.PaymentCreateRequest;
import com.campus.lease.dto.PaymentNotifyHeaders;
import com.campus.lease.dto.PaymentRefundRequest;
import com.campus.lease.service.PaymentService;
import com.campus.lease.support.AuthContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "支付管理", description = "支付创建、回调、查询、退款和资金概览接口")
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final AuthContext authContext;

    @Operation(summary = "创建支付单", description = "根据订单生成微信小程序支付参数")
    @PostMapping("/create")
    public Result<Map<String, Object>> createPayment(@RequestBody PaymentCreateRequest request) {
        Long userId = authContext.requireCurrentUserId();
        return Result.success(paymentService.createPayment(userId, request));
    }

    @Operation(summary = "接收支付回调", description = "接收微信支付平台异步通知，验签、校验金额并幂等更新订单状态")
    @PostMapping("/notify")
    public ResponseEntity<Map<String, String>> paymentNotify(
            @RequestHeader(value = "Wechatpay-Signature", required = false) String signature,
            @RequestHeader(value = "Wechatpay-Serial", required = false) String serial,
            @RequestHeader(value = "Wechatpay-Nonce", required = false) String nonce,
            @RequestHeader(value = "Wechatpay-Timestamp", required = false) String timestamp,
            @RequestHeader(value = "Wechatpay-Signature-Type", required = false) String signatureType,
            @RequestBody String notifyData
    ) {
        PaymentNotifyHeaders headers = new PaymentNotifyHeaders();
        headers.setSignature(signature);
        headers.setSerial(serial);
        headers.setNonce(nonce);
        headers.setTimestamp(timestamp);
        headers.setSignatureType(signatureType);
        try {
            paymentService.handlePaymentNotify(headers, notifyData);
            return ResponseEntity.ok(Map.of("code", "SUCCESS", "message", "成功"));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("code", "FAIL", "message", exception.getMessage() == null ? "处理失败" : exception.getMessage()));
        }
    }

    @Operation(summary = "查询支付状态", description = "根据订单 ID 查询当前登录用户最近一条支付流水")
    @GetMapping("/query/{orderId}")
    public Result<Map<String, Object>> queryPayment(
            @Parameter(description = "订单 ID", example = "1")
            @PathVariable Long orderId
    ) {
        Long userId = authContext.requireCurrentUserId();
        return Result.success(paymentService.queryPayment(userId, orderId));
    }

    @Operation(summary = "申请退款", description = "对指定订单发起退款处理，并写入退款流水记录")
    @PostMapping("/refund")
    public Result<Map<String, Object>> refund(@RequestBody PaymentRefundRequest request) {
        Long userId = authContext.requireCurrentUserId();
        return Result.success(paymentService.refund(userId, request));
    }

    @Operation(summary = "获取支付汇总", description = "返回当前登录用户的支付和退款汇总信息")
    @GetMapping("/summary")
    public Result<Map<String, Object>> getSummary() {
        Long userId = authContext.requireCurrentUserId();
        return Result.success(paymentService.getSummary(userId));
    }

    @Operation(summary = "分页查询支付记录", description = "返回当前登录用户的支付流水分页列表")
    @GetMapping("/records")
    public Result<Page<Map<String, Object>>> getRecords(
            @Parameter(description = "页码，从 1 开始", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Long userId = authContext.requireCurrentUserId();
        return Result.success(paymentService.getRecords(userId, page, size));
    }
}
