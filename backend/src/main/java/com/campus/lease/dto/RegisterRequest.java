package com.campus.lease.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户注册请求")
public class RegisterRequest {
    @Schema(description = "用户名（学号或手机号）", example = "student001")
    private String username;

    @Schema(description = "密码", example = "123456")
    private String password;

    @Schema(description = "确认密码", example = "123456")
    private String confirmPassword;

    @Schema(description = "用户昵称", example = "张三")
    private String nickname;

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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
