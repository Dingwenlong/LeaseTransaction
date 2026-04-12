package com.campus.lease.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "物品审核或状态更新请求")
public class ItemAuditRequest {
    @Schema(description = "物品状态，具体取值由业务常量定义", example = "1")
    private Integer status;

    @Schema(description = "审核原因或状态变更备注", example = "图片信息完整，审核通过")
    private String reason;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
