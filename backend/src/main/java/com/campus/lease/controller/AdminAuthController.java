package com.campus.lease.controller;

import com.campus.lease.common.result.Result;
import com.campus.lease.dto.AdminLoginRequest;
import com.campus.lease.dto.AdminLoginResponse;
import com.campus.lease.dto.AdminUserInfo;
import com.campus.lease.service.SystemUserService;
import com.campus.lease.support.AdminAccessGuard;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final SystemUserService systemUserService;
    private final AdminAccessGuard adminAccessGuard;

    @PostMapping("/login")
    public Result<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        return Result.success(systemUserService.login(request));
    }

    @GetMapping("/me")
    public Result<AdminUserInfo> getCurrentAdmin() {
        Long adminId = adminAccessGuard.requireAdminId();
        return Result.success(systemUserService.getAdminInfo(adminId));
    }
}
