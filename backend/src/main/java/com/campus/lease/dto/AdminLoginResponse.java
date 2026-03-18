package com.campus.lease.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "后台登录响应")
public class AdminLoginResponse {

    @Schema(description = "后台访问令牌")
    private String token;

    @Schema(description = "当前登录管理员信息")
    private AdminUserInfo userInfo;
}
