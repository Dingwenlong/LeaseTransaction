package com.campus.lease.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "提交评价请求")
public class ReviewSubmitRequest {
    @Schema(description = "订单 ID", example = "1")
    private Long orderId;

    @Schema(description = "评分，1 到 5 分", example = "5")
    private Integer rating;

    @Schema(description = "评价内容", example = "物品与描述一致，交易顺利")
    private String content;

    @Schema(description = "评价图片，多个地址用英文逗号分隔", example = "/uploads/2026/03/18/review.png")
    private String images;

    @Schema(description = "是否匿名，0 否 1 是", example = "0")
    private Integer isAnonymous;
}
