package com.campus.lease.dto;

import lombok.Data;

@Data
public class SystemUserUpdateRequest {

    private String displayName;

    private String role;

    private Integer status;
}
