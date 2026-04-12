package com.campus.lease.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户资料更新请求")
public class UserProfileUpdateRequest {
    @Schema(description = "昵称", example = "校园相机哥")
    private String nickname;

    @Schema(description = "头像地址", example = "https://example.com/avatar.png")
    private String avatar;

    @Schema(description = "院系", example = "计算机学院")
    private String department;

    @Schema(description = "校区", example = "东校区")
    private String campus;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }
}
