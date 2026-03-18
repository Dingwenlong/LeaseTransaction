package com.campus.lease.controller;

import com.campus.lease.common.result.Result;
import com.campus.lease.dto.AdminUserInfo;
import com.campus.lease.dto.SystemUserCreateRequest;
import com.campus.lease.dto.SystemUserPasswordResetRequest;
import com.campus.lease.dto.SystemUserStatusUpdateRequest;
import com.campus.lease.dto.SystemUserUpdateRequest;
import com.campus.lease.service.SystemUserService;
import com.campus.lease.support.AdminAccessGuard;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/system-users")
@RequiredArgsConstructor
public class SystemUserController {

    private final SystemUserService systemUserService;
    private final AdminAccessGuard adminAccessGuard;

    @GetMapping("/list")
    public Result<Map<String, Object>> getSystemUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status
    ) {
        adminAccessGuard.requireSuperAdminId();
        return Result.success(systemUserService.getSystemUserPage(page, size, keyword, status));
    }

    @PostMapping
    public Result<AdminUserInfo> createSystemUser(@Valid @RequestBody SystemUserCreateRequest request) {
        Long adminId = adminAccessGuard.requireSuperAdminId();
        return Result.success(systemUserService.createSystemUser(request, adminId));
    }

    @PutMapping("/{id}")
    public Result<AdminUserInfo> updateSystemUser(@PathVariable Long id, @RequestBody SystemUserUpdateRequest request) {
        Long adminId = adminAccessGuard.requireSuperAdminId();
        return Result.success(systemUserService.updateSystemUser(id, request, adminId));
    }

    @PostMapping("/{id}/status")
    public Result<Void> updateSystemUserStatus(@PathVariable Long id, @Valid @RequestBody SystemUserStatusUpdateRequest request) {
        Long adminId = adminAccessGuard.requireSuperAdminId();
        systemUserService.updateSystemUserStatus(id, request, adminId);
        return Result.success();
    }

    @PostMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id, @Valid @RequestBody SystemUserPasswordResetRequest request) {
        Long adminId = adminAccessGuard.requireSuperAdminId();
        systemUserService.resetPassword(id, request, adminId);
        return Result.success();
    }
}
