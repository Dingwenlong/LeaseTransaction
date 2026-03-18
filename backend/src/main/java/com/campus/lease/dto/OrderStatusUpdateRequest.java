package com.campus.lease.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "订单状态更新请求")
public class OrderStatusUpdateRequest {
    @Schema(description = "订单状态，具体取值由业务常量定义", example = "3")
    private Integer status;

    @Schema(description = "状态变更备注", example = "买家已确认收货")
    private String remark;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
