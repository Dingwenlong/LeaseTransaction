package com.campus.lease.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户信用分调整请求")
public class UserCreditAdjustRequest {
    @Schema(description = "信用动作类型，可选 BREACH 或 COMPLAINT_CONFIRMED", example = "COMPLAINT_CONFIRMED")
    private String action;

    @Schema(description = "关联订单 ID，可为空", example = "12")
    private Long relatedOrderId;

    @Schema(description = "补充说明", example = "平台核实后判定投诉成立")
    private String note;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getRelatedOrderId() {
        return relatedOrderId;
    }

    public void setRelatedOrderId(Long relatedOrderId) {
        this.relatedOrderId = relatedOrderId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
