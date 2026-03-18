package com.campus.lease.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "后台登录请求")
public class AdminLoginRequest {

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "后台登录用户名", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "后台登录密码", example = "Admin@123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
