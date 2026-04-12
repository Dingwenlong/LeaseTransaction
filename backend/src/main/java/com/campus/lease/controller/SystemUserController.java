package com.campus.lease.controller;

import com.campus.lease.common.result.Result;
import com.campus.lease.dto.AdminUserInfo;
import com.campus.lease.dto.SystemUserCreateRequest;
import com.campus.lease.dto.SystemUserPasswordResetRequest;
import com.campus.lease.dto.SystemUserStatusUpdateRequest;
import com.campus.lease.dto.SystemUserUpdateRequest;
import com.campus.lease.service.SystemUserService;
import com.campus.lease.support.AdminAccessGuard;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "后台系统用户", description = "后台系统账号的分页查询、创建、编辑、启停和密码重置接口")
@RestController
@RequestMapping("/api/admin/system-users")
@RequiredArgsConstructor
public class SystemUserController {

    private final SystemUserService systemUserService;
    private final AdminAccessGuard adminAccessGuard;

    @Operation(summary = "分页查询系统用户", description = "仅超级管理员可访问，可按关键字和状态筛选后台系统账号")
    @GetMapping("/list")
    public Result<Map<String, Object>> getSystemUserList(
            @Parameter(description = "页码，从 1 开始", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "关键字，匹配用户名或显示名称", example = "admin")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "账号状态，0 为禁用，1 为启用", example = "1")
            @RequestParam(required = false) Integer status
    ) {
        adminAccessGuard.requireSuperAdminId();
        return Result.success(systemUserService.getSystemUserPage(page, size, keyword, status));
    }

    @Operation(summary = "创建系统用户", description = "仅超级管理员可创建后台账号，角色支持 SUPER_ADMIN 和 OPERATOR")
    @PostMapping
    public Result<AdminUserInfo> createSystemUser(@Valid @RequestBody SystemUserCreateRequest request) {
        Long adminId = adminAccessGuard.requireSuperAdminId();
        return Result.success(systemUserService.createSystemUser(request, adminId));
    }

    @Operation(summary = "更新系统用户", description = "仅超级管理员可更新后台账号的显示名称、角色或状态")
    @PutMapping("/{id}")
    public Result<AdminUserInfo> updateSystemUser(
            @Parameter(description = "系统用户 ID", example = "1")
            @PathVariable Long id,
            @RequestBody SystemUserUpdateRequest request
    ) {
        Long adminId = adminAccessGuard.requireSuperAdminId();
        return Result.success(systemUserService.updateSystemUser(id, request, adminId));
    }

    @Operation(summary = "修改系统用户状态", description = "仅超级管理员可启用或禁用指定后台账号")
    @PostMapping("/{id}/status")
    public Result<Void> updateSystemUserStatus(
            @Parameter(description = "系统用户 ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody SystemUserStatusUpdateRequest request
    ) {
        Long adminId = adminAccessGuard.requireSuperAdminId();
        systemUserService.updateSystemUserStatus(id, request, adminId);
        return Result.success();
    }

    @Operation(summary = "重置系统用户密码", description = "仅超级管理员可为指定后台账号设置新密码")
    @PostMapping("/{id}/reset-password")
    public Result<Void> resetPassword(
            @Parameter(description = "系统用户 ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody SystemUserPasswordResetRequest request
    ) {
        Long adminId = adminAccessGuard.requireSuperAdminId();
        systemUserService.resetPassword(id, request, adminId);
        return Result.success();
    }
}
