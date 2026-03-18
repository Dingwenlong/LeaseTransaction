package com.campus.lease.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUserInfo {

    private Long id;

    private String username;

    private String displayName;

    private String role;

    private Integer status;

    private LocalDateTime lastLoginTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
