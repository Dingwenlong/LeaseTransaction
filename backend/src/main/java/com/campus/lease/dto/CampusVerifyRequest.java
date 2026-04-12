package com.campus.lease.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "校园认证请求")
public class CampusVerifyRequest {
    @Schema(description = "学号", example = "2023001001")
    private String studentId;

    @Schema(description = "院系", example = "计算机学院")
    private String department;

    @Schema(description = "校区", example = "东校区")
    private String campus;

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
}
