package com.campus.lease.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.lease.common.constant.BusinessConstants;
import com.campus.lease.common.exception.BusinessException;
import com.campus.lease.common.result.Result;
import com.campus.lease.dto.OrderStatusUpdateRequest;
import com.campus.lease.dto.PaymentCreateRequest;
import com.campus.lease.dto.PaymentRefundRequest;
import com.campus.lease.entity.Order;
import com.campus.lease.entity.PaymentRecord;
import com.campus.lease.mapper.PaymentRecordMapper;
import com.campus.lease.service.OrderService;
import com.campus.lease.support.AuthContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Tag(name = "支付管理", description = "支付创建、回调、查询、退款和资金概览接口")
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final OrderService orderService;
    private final PaymentRecordMapper paymentRecordMapper;
    private final AuthContext authContext;

    @Operation(summary = "创建支付单", description = "根据订单生成模拟支付参数，返回前端拉起支付所需的数据")
    @PostMapping("/create")
    public Result<Map<String, Object>> createPayment(@RequestBody PaymentCreateRequest request) {
        Long orderId = request.getOrderId();
        Order order = orderService.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        PaymentRecord record = new PaymentRecord();
        record.setPaymentNo("PREPAY" + System.currentTimeMillis());
        record.setOrderId(orderId);
        record.setUserId(order.getBuyerId());
        record.setType(BusinessConstants.PaymentType.PAYMENT);
        record.setAmount(order.getTotalAmount());
        record.setPaymentMethod(1);
        record.setStatus(2);
        record.setTransactionId("PRE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        paymentRecordMapper.insert(record);

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("timestamp", String.valueOf(System.currentTimeMillis()));
        result.put("nonceStr", generateNonceStr());
        result.put("package", "prepay_id=wx1234567890");
        result.put("signType", "MD5");
        result.put("paySign", "mock_pay_sign");
        result.put("demoMode", true);
        return Result.success(result);
    }

    @Operation(summary = "接收支付回调", description = "接收微信支付平台的异步通知，当前实现返回模拟成功响应")
    @PostMapping("/notify")
    public String paymentNotify(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "微信支付回调原始报文", required = true)
            @RequestBody String notifyData
    ) {
        System.out.println("收到微信支付回调: " + notifyData);
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }

    @Operation(summary = "查询支付状态", description = "根据订单 ID 查询最近一条支付流水的处理状态")
    @GetMapping("/query/{orderId}")
    public Result<Map<String, Object>> queryPayment(
            @Parameter(description = "订单 ID", example = "1")
            @PathVariable Long orderId
    ) {
        LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentRecord::getOrderId, orderId).orderByDesc(PaymentRecord::getCreateTime).last("limit 1");
        PaymentRecord record = paymentRecordMapper.selectOne(wrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("status", record == null ? "PENDING" : record.getStatus() == 1 ? "SUCCESS" : "PROCESSING");
        result.put("transactionId", record == null ? "" : record.getTransactionId());
        return Result.success(result);
    }

    @Operation(summary = "申请退款", description = "对指定订单发起退款处理，并写入退款流水记录")
    @PostMapping("/refund")
    public Result<Map<String, Object>> refund(@RequestBody PaymentRefundRequest request) {
        Long orderId = request.getOrderId();
        String reason = request.getReason();

        OrderStatusUpdateRequest updateRequest = new OrderStatusUpdateRequest();
        updateRequest.setStatus(BusinessConstants.OrderStatus.REFUNDING);
        updateRequest.setRemark(reason);
        orderService.updateOrderStatus(0L, orderId, updateRequest);

        Order order = orderService.getById(orderId);
        PaymentRecord record = new PaymentRecord();
        record.setPaymentNo("RF" + System.currentTimeMillis());
        record.setOrderId(orderId);
        record.setUserId(order == null ? 0L : order.getBuyerId());
        record.setType(BusinessConstants.PaymentType.REFUND);
        record.setAmount(order == null ? BigDecimal.ZERO : order.getDeposit());
        record.setPaymentMethod(1);
        record.setStatus(2);
        record.setTransactionId("RF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        paymentRecordMapper.insert(record);

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("refundId", record.getPaymentNo());
        result.put("status", "PROCESSING");
        return Result.success(result);
    }

    @Operation(summary = "获取支付汇总", description = "返回当前登录用户的支付、押金和退款汇总信息")
    @GetMapping("/summary")
    public Result<Map<String, Object>> getSummary() {
        Long userId = authContext.getCurrentUserIdOrDefault(2L);
        LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentRecord::getUserId, userId).orderByDesc(PaymentRecord::getCreateTime);
        var records = paymentRecordMapper.selectList(wrapper);

        BigDecimal paidAmount = records.stream()
                .filter(record -> record.getType() != null && record.getType() == BusinessConstants.PaymentType.PAYMENT)
                .filter(record -> record.getStatus() != null && record.getStatus() == 1)
                .map(PaymentRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal frozenDeposit = records.stream()
                .filter(record -> record.getType() != null && record.getType() == BusinessConstants.PaymentType.DEPOSIT)
                .filter(record -> record.getStatus() != null && record.getStatus() == 1)
                .map(PaymentRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .subtract(records.stream()
                        .filter(record -> record.getType() != null && record.getType() == BusinessConstants.PaymentType.REFUND)
                        .filter(record -> record.getStatus() != null && record.getStatus() == 1)
                        .map(PaymentRecord::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
        BigDecimal refundingAmount = records.stream()
                .filter(record -> record.getType() != null && record.getType() == BusinessConstants.PaymentType.REFUND)
                .filter(record -> record.getStatus() != null && record.getStatus() == 2)
                .map(PaymentRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal refundedAmount = records.stream()
                .filter(record -> record.getType() != null && record.getType() == BusinessConstants.PaymentType.REFUND)
                .filter(record -> record.getStatus() != null && record.getStatus() == 1)
                .map(PaymentRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> result = new HashMap<>();
        result.put("paidAmount", paidAmount);
        result.put("depositFrozen", frozenDeposit.max(BigDecimal.ZERO));
        result.put("refundingAmount", refundingAmount);
        result.put("refundedAmount", refundedAmount);
        result.put("recordCount", records.size());
        return Result.success(result);
    }

    @Operation(summary = "分页查询支付记录", description = "返回当前登录用户的支付流水分页列表")
    @GetMapping("/records")
    public Result<Page<Map<String, Object>>> getRecords(
            @Parameter(description = "页码，从 1 开始", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Long userId = authContext.getCurrentUserIdOrDefault(2L);
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
            item.put("statusText", record.getStatus() != null && record.getStatus() == 1 ? "成功" : record.getStatus() != null && record.getStatus() == 2 ? "处理中" : "失败");
            item.put("transactionId", record.getTransactionId());
            item.put("createdAt", record.getCreateTime());
            return item;
        }).toList());
        return Result.success(result);
    }

    private String generateNonceStr() {
        return Long.toHexString(System.currentTimeMillis());
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
