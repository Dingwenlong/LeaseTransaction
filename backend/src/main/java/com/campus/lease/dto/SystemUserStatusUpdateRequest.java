package com.campus.lease.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "系统用户状态更新请求")
public class SystemUserStatusUpdateRequest {

    @NotNull(message = "状态不能为空")
    @Schema(description = "账号状态，0 为禁用，1 为启用", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;
}
