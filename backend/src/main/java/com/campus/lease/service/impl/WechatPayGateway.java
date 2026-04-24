package com.campus.lease.service.impl;

import com.campus.lease.dto.PaymentGatewayNotifyResult;
import com.campus.lease.dto.PaymentGatewayPrepayRequest;
import com.campus.lease.dto.PaymentNotifyHeaders;
import com.campus.lease.service.PaymentGateway;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.payments.model.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
@Profile("prod")
public class WechatPayGateway implements PaymentGateway {

    private final String appId;
    private final String mchId;
    private final JsapiServiceExtension service;
    private final NotificationParser notificationParser;

    public WechatPayGateway(
            @Value("${wechat.pay.app-id}") String appId,
            @Value("${wechat.pay.mch-id}") String mchId,
            @Value("${wechat.pay.mch-serial-no}") String merchantSerialNumber,
            @Value("${wechat.pay.private-key-path}") String privateKeyPath,
            @Value("${wechat.pay.api-v3-key}") String apiV3Key
    ) {
        this.appId = appId;
        this.mchId = mchId;
        RSAAutoCertificateConfig config = new RSAAutoCertificateConfig.Builder()
                .merchantId(mchId)
                .privateKeyFromPath(privateKeyPath)
                .merchantSerialNumber(merchantSerialNumber)
                .apiV3Key(apiV3Key)
                .build();
        this.service = new JsapiServiceExtension.Builder().config(config).build();
        this.notificationParser = new NotificationParser(config);
    }

    @Override
    public Map<String, Object> createPrepay(PaymentGatewayPrepayRequest gatewayRequest) {
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(gatewayRequest.amount().movePointRight(2).intValueExact());
        Payer payer = new Payer();
        payer.setOpenid(gatewayRequest.payerOpenid());

        request.setAmount(amount);
        request.setPayer(payer);
        request.setAppid(appId);
        request.setMchid(mchId);
        request.setDescription(gatewayRequest.description());
        request.setNotifyUrl(gatewayRequest.notifyUrl());
        request.setOutTradeNo(gatewayRequest.outTradeNo());

        PrepayWithRequestPaymentResponse response = service.prepayWithRequestPayment(request);
        Map<String, Object> result = new HashMap<>();
        result.put("timeStamp", response.getTimeStamp());
        result.put("timestamp", response.getTimeStamp());
        result.put("nonceStr", response.getNonceStr());
        result.put("package", response.getPackageVal());
        result.put("signType", response.getSignType());
        result.put("paySign", response.getPaySign());
        return result;
    }

    @Override
    public PaymentGatewayNotifyResult parsePaymentNotify(PaymentNotifyHeaders headers, String body) {
        RequestParam requestParam = new RequestParam.Builder()
                .serialNumber(headers.getSerial())
                .nonce(headers.getNonce())
                .signature(headers.getSignature())
                .timestamp(headers.getTimestamp())
                .signType(headers.getSignatureType())
                .body(body)
                .build();
        Transaction transaction = notificationParser.parse(requestParam, Transaction.class);
        BigDecimal amount = BigDecimal.valueOf(transaction.getAmount().getTotal()).movePointLeft(2);
        return new PaymentGatewayNotifyResult(
                transaction.getOutTradeNo(),
                transaction.getTransactionId(),
                amount,
                Transaction.TradeStateEnum.SUCCESS.equals(transaction.getTradeState())
        );
    }
}
