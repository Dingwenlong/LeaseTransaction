package com.campus.lease.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户登录请求")
public class LoginRequest {
    @Schema(description = "微信登录 code，联调或模拟登录时可为空", example = "wx-login-code")
    private String code;

    @Schema(description = "模拟登录用户名", example = "student001")
    private String username;

    @Schema(description = "模拟登录密码", example = "123456")
    private String password;

    @Schema(description = "用户昵称", example = "张三")
    private String nickname;

    @Schema(description = "头像地址", example = "https://example.com/avatar.png")
    private String avatarUrl;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
