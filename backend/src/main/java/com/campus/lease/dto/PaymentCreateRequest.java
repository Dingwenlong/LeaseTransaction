package com.campus.lease.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "创建支付请求")
public class PaymentCreateRequest {

    @Schema(description = "订单 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderId;
}
