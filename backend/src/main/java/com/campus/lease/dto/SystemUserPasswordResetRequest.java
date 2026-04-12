package com.campus.lease.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "系统用户密码重置请求")
public class SystemUserPasswordResetRequest {

    @NotBlank(message = "新密码不能为空")
    @Schema(description = "新密码", example = "NewPassword@123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
