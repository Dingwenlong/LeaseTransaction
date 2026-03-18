package com.campus.lease.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户登录响应")
public class LoginResponse {
    @Schema(description = "用户访问令牌")
    private String token;

    @Schema(description = "当前登录用户信息")
    private UserInfo userInfo;

    public LoginResponse() {
    }

    public LoginResponse(String token, UserInfo userInfo) {
        this.token = token;
        this.userInfo = userInfo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
