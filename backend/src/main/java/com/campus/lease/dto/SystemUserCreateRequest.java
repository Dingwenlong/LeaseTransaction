package com.campus.lease.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "创建系统用户请求")
public class SystemUserCreateRequest {

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "登录用户名", example = "operator01", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "显示名称不能为空")
    @Schema(description = "显示名称", example = "运营专员", requiredMode = Schema.RequiredMode.REQUIRED)
    private String displayName;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "登录密码", example = "Operator@123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @NotBlank(message = "角色不能为空")
    @Schema(description = "角色，SUPER_ADMIN 或 OPERATOR", example = "OPERATOR", requiredMode = Schema.RequiredMode.REQUIRED)
    private String role;
}
