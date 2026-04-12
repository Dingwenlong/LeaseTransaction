package com.campus.lease.controller;

import com.campus.lease.common.result.Result;
import com.campus.lease.dto.AdminLoginRequest;
import com.campus.lease.dto.AdminLoginResponse;
import com.campus.lease.dto.AdminUserInfo;
import com.campus.lease.service.SystemUserService;
import com.campus.lease.support.AdminAccessGuard;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Tag(name = "后台认证", description = "后台登录与当前管理员信息接口")
@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final SystemUserService systemUserService;
    private final AdminAccessGuard adminAccessGuard;

    @Operation(summary = "后台账号登录", description = "使用后台系统用户名和密码登录，成功后返回后台访问令牌和当前管理员信息")
    @PostMapping("/login")
    public Result<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        return Result.success(systemUserService.login(request));
    }

    @Operation(summary = "获取当前后台账号信息", description = "根据请求中的后台访问令牌返回当前登录管理员的资料")
    @GetMapping("/me")
    public Result<AdminUserInfo> getCurrentAdmin() {
        Long adminId = adminAccessGuard.requireAdminId();
        return Result.success(systemUserService.getAdminInfo(adminId));
    }
}
