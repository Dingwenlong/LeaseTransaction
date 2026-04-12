package com.campus.lease.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户状态更新请求")
public class UserStatusUpdateRequest {
    @Schema(description = "用户状态，0 为禁用，1 为正常", example = "1")
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
