package com.campus.lease.dto;

import lombok.Data;

@Data
public class PaymentNotifyHeaders {
    private String signature;
    private String serial;
    private String nonce;
    private String timestamp;
    private String signatureType;
}
