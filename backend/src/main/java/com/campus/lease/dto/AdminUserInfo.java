package com.campus.lease.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "后台管理员信息")
public class AdminUserInfo {

    @Schema(description = "管理员 ID", example = "1")
    private Long id;

    @Schema(description = "登录用户名", example = "admin")
    private String username;

    @Schema(description = "显示名称", example = "系统管理员")
    private String displayName;

    @Schema(description = "系统角色，SUPER_ADMIN 或 OPERATOR", example = "SUPER_ADMIN")
    private String role;

    @Schema(description = "账号状态，0 为禁用，1 为启用", example = "1")
    private Integer status;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
