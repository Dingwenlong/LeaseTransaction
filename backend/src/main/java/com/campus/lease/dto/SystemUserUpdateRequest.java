package com.campus.lease.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "系统用户更新请求")
public class SystemUserUpdateRequest {

    @Schema(description = "显示名称", example = "运营主管")
    private String displayName;

    @Schema(description = "角色，SUPER_ADMIN 或 OPERATOR", example = "OPERATOR")
    private String role;

    @Schema(description = "账号状态，0 为禁用，1 为启用", example = "1")
    private Integer status;
}
