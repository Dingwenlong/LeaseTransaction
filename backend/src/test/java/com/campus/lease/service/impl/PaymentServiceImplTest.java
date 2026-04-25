package com.campus.lease.service.impl;

import com.campus.lease.common.constant.BusinessConstants;
import com.campus.lease.common.exception.BusinessException;
import com.campus.lease.dto.PaymentCreateRequest;
import com.campus.lease.dto.PaymentGatewayNotifyResult;
import com.campus.lease.dto.PaymentNotifyHeaders;
import com.campus.lease.dto.PaymentRefundRequest;
import com.campus.lease.entity.Order;
import com.campus.lease.entity.PaymentRecord;
import com.campus.lease.entity.User;
import com.campus.lease.mapper.PaymentRecordMapper;
import com.campus.lease.service.OrderService;
import com.campus.lease.service.PaymentGateway;
import com.campus.lease.service.UserService;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceImplTest {

    @Test
    void createPaymentRejectsOrderOwnedByAnotherBuyer() {
        OrderService orderService = mock(OrderService.class);
        UserService userService = mock(UserService.class);
        PaymentRecordMapper paymentRecordMapper = mock(PaymentRecordMapper.class);
        PaymentGateway paymentGateway = mock(PaymentGateway.class);
        PaymentServiceImpl service = new PaymentServiceImpl(orderService, userService, paymentRecordMapper, paymentGateway);

        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setOrderId(1L);
        when(orderService.getById(1L)).thenReturn(order());

        assertThrows(BusinessException.class, () -> service.createPayment(99L, request));
        verify(paymentRecordMapper, never()).insert(any(PaymentRecord.class));
        verify(paymentGateway, never()).createPrepay(any());
    }

    @Test
    void createPaymentCreatesProcessingRecordAndReturnsPaymentNo() {
        OrderService orderService = mock(OrderService.class);
        UserService userService = mock(UserService.class);
        PaymentRecordMapper paymentRecordMapper = mock(PaymentRecordMapper.class);
        PaymentGateway paymentGateway = mock(PaymentGateway.class);
        PaymentServiceImpl service = new PaymentServiceImpl(orderService, userService, paymentRecordMapper, paymentGateway);

        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setOrderId(1L);
        User user = new User();
        user.setOpenid("openid-001");
        when(orderService.getById(1L)).thenReturn(order());
        when(paymentRecordMapper.selectOne(any())).thenReturn(null);
        when(userService.getById(2L)).thenReturn(user);
        when(paymentGateway.createPrepay(any())).thenReturn(new HashMap<>(Map.of(
                "timeStamp", "1",
                "nonceStr", "n",
                "package", "prepay_id=local",
                "signType", "RSA",
                "paySign", "LOCAL_PAY_SIGN"
        )));

        Map<String, Object> result = service.createPayment(2L, request);

        ArgumentCaptor<PaymentRecord> recordCaptor = ArgumentCaptor.forClass(PaymentRecord.class);
        verify(paymentRecordMapper).insert(recordCaptor.capture());
        PaymentRecord record = recordCaptor.getValue();
        assertEquals(BusinessConstants.PaymentStatus.PROCESSING, record.getStatus());
        assertEquals(new BigDecimal("20.00"), record.getAmount());
        assertEquals(record.getPaymentNo(), result.get("paymentNo"));
    }

    @Test
    void handlePaymentNotifyRejectsAmountMismatch() {
        OrderService orderService = mock(OrderService.class);
        UserService userService = mock(UserService.class);
        PaymentRecordMapper paymentRecordMapper = mock(PaymentRecordMapper.class);
        PaymentGateway paymentGateway = mock(PaymentGateway.class);
        PaymentServiceImpl service = new PaymentServiceImpl(orderService, userService, paymentRecordMapper, paymentGateway);

        PaymentRecord record = paymentRecord(BusinessConstants.PaymentStatus.PROCESSING, new BigDecimal("20.00"));
        when(paymentGateway.parsePaymentNotify(any(), any())).thenReturn(new PaymentGatewayNotifyResult("PAY001", "TX001", new BigDecimal("19.99"), true));
        when(paymentRecordMapper.selectOne(any())).thenReturn(record);
        when(orderService.getById(1L)).thenReturn(order());

        assertThrows(BusinessException.class, () -> service.handlePaymentNotify(new PaymentNotifyHeaders(), "{}"));
        verify(paymentRecordMapper).updateById(argThat((PaymentRecord updated) ->
                updated.getStatus() == BusinessConstants.PaymentStatus.FAILED
                        && "支付回调金额不一致".equals(updated.getFailureReason())));
        verify(orderService, never()).updateOrderStatus(any(), any(), any());
    }

    @Test
    void handlePaymentNotifyIsIdempotentAfterSuccess() {
        OrderService orderService = mock(OrderService.class);
        UserService userService = mock(UserService.class);
        PaymentRecordMapper paymentRecordMapper = mock(PaymentRecordMapper.class);
        PaymentGateway paymentGateway = mock(PaymentGateway.class);
        PaymentServiceImpl service = new PaymentServiceImpl(orderService, userService, paymentRecordMapper, paymentGateway);

        PaymentRecord record = paymentRecord(BusinessConstants.PaymentStatus.SUCCESS, new BigDecimal("20.00"));
        when(paymentGateway.parsePaymentNotify(any(), any())).thenReturn(new PaymentGatewayNotifyResult("PAY001", "TX001", new BigDecimal("20.00"), true));
        when(paymentRecordMapper.selectOne(any())).thenReturn(record);
        when(orderService.getById(1L)).thenReturn(order());

        assertDoesNotThrow(() -> service.handlePaymentNotify(new PaymentNotifyHeaders(), "{}"));
        verify(paymentRecordMapper, never()).updateById(any(PaymentRecord.class));
        verify(orderService, never()).updateOrderStatus(any(), any(), any());
    }

    @Test
    void confirmLocalPaymentCompletesOrderAndIsIdempotent() {
        OrderService orderService = mock(OrderService.class);
        UserService userService = mock(UserService.class);
        PaymentRecordMapper paymentRecordMapper = mock(PaymentRecordMapper.class);
        PaymentGateway paymentGateway = mock(PaymentGateway.class);
        PaymentServiceImpl service = new PaymentServiceImpl(orderService, userService, paymentRecordMapper, paymentGateway);

        PaymentRecord record = paymentRecord(BusinessConstants.PaymentStatus.PROCESSING, new BigDecimal("20.00"));
        when(paymentRecordMapper.selectOne(any())).thenReturn(record);
        when(orderService.getById(1L)).thenReturn(order());

        Map<String, Object> result = service.confirmLocalPayment(2L, "PAY001");

        assertEquals("SUCCESS", result.get("paymentStatus"));
        assertEquals(BusinessConstants.OrderStatus.PAID, result.get("orderStatus"));
        verify(paymentRecordMapper).updateById(argThat((PaymentRecord updated) ->
                updated.getStatus() == BusinessConstants.PaymentStatus.SUCCESS
                        && "LOCAL-PAY001".equals(updated.getTransactionId())));
        verify(orderService).updateOrderStatus(eq(0L), eq(1L), any());

        assertDoesNotThrow(() -> service.confirmLocalPayment(2L, "PAY001"));
    }

    @Test
    void refundRejectsPendingPaymentOrder() {
        OrderService orderService = mock(OrderService.class);
        UserService userService = mock(UserService.class);
        PaymentRecordMapper paymentRecordMapper = mock(PaymentRecordMapper.class);
        PaymentGateway paymentGateway = mock(PaymentGateway.class);
        PaymentServiceImpl service = new PaymentServiceImpl(orderService, userService, paymentRecordMapper, paymentGateway);
        PaymentRefundRequest request = new PaymentRefundRequest();
        request.setOrderId(1L);
        request.setReason("测试退款");

        when(orderService.getById(1L)).thenReturn(order());

        assertThrows(BusinessException.class, () -> service.refund(2L, request));
        verify(paymentRecordMapper, never()).insert(any(PaymentRecord.class));
    }

    private PaymentRecord paymentRecord(Integer status, BigDecimal amount) {
        PaymentRecord record = new PaymentRecord();
        record.setPaymentNo("PAY001");
        record.setOrderId(1L);
        record.setUserId(2L);
        record.setType(BusinessConstants.PaymentType.PAYMENT);
        record.setAmount(amount);
        record.setStatus(status);
        return record;
    }

    private Order order() {
        Order order = new Order();
        order.setId(1L);
        order.setBuyerId(2L);
        order.setSellerId(3L);
        order.setStatus(BusinessConstants.OrderStatus.PENDING_PAYMENT);
        order.setTotalAmount(new BigDecimal("20.00"));
        return order;
    }
}
