package com.campus.lease.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户信息")
public class UserInfo {
    @Schema(description = "用户 ID", example = "1")
    private Long id;

    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Schema(description = "头像地址", example = "https://example.com/avatar.png")
    private String avatar;

    @Schema(description = "学号", example = "2023001001")
    private String studentId;

    @Schema(description = "院系", example = "计算机学院")
    private String department;

    @Schema(description = "校区", example = "东校区")
    private String campus;

    @Schema(description = "信用分", example = "100")
    private Integer creditScore;

    @Schema(description = "是否已认证，0 否 1 是", example = "1")
    private Integer isVerified;

    @Schema(description = "账号状态，0 禁用 1 正常", example = "1")
    private Integer status;

    public UserInfo() {
    }

    public UserInfo(Long id, String nickname, String avatar, String studentId, String department, String campus, Integer creditScore, Integer isVerified, Integer status) {
        this.id = id;
        this.nickname = nickname;
        this.avatar = avatar;
        this.studentId = studentId;
        this.department = department;
        this.campus = campus;
        this.creditScore = creditScore;
        this.isVerified = isVerified;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
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

    public Integer getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
    }

    public Integer getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Integer isVerified) {
        this.isVerified = isVerified;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
