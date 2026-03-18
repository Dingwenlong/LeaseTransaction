package com.campus.lease.dto;

public class UserInfo {
    private Long id;
    private String nickname;
    private String avatar;
    private String studentId;
    private String department;
    private String campus;
    private Integer creditScore;
    private Integer isVerified;
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
