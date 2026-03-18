package com.campus.lease.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "退款申请请求")
public class PaymentRefundRequest {

    @Schema(description = "订单 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderId;

    @Schema(description = "退款原因", example = "用户取消订单", requiredMode = Schema.RequiredMode.REQUIRED)
    private String reason;
}
