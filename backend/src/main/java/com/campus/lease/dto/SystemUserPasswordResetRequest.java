package com.campus.lease.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SystemUserPasswordResetRequest {

    @NotBlank(message = "新密码不能为空")
    private String password;
}
