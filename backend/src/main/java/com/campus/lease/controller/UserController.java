package com.campus.lease.controller;

import com.campus.lease.common.result.Result;
import com.campus.lease.dto.CampusVerifyRequest;
import com.campus.lease.dto.LoginRequest;
import com.campus.lease.dto.LoginResponse;
import com.campus.lease.dto.UserInfo;
import com.campus.lease.dto.UserProfileUpdateRequest;
import com.campus.lease.dto.UserStatusUpdateRequest;
import com.campus.lease.service.UserService;
import com.campus.lease.support.AdminAccessGuard;
import com.campus.lease.support.AuthContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Tag(name = "用户接口", description = "用户相关接口")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthContext authContext;
    private final AdminAccessGuard adminAccessGuard;

    @Operation(summary = "用户登录", description = "通过微信code进行用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("用户登录请求，code: {}, username: {}", request.getCode(), request.getUsername());
        LoginResponse response = userService.login(request);
        return Result.success(response);
    }

    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/info")
    public Result<UserInfo> getUserInfo() {
        Long userId = authContext.getCurrentUserIdOrDefault(1L);
        return Result.success(userService.getUserInfo(userId));
    }

    @PutMapping("/profile")
    public Result<UserInfo> updateProfile(@RequestBody UserProfileUpdateRequest request) {
        Long userId = authContext.getCurrentUserIdOrDefault(1L);
        return Result.success(userService.updateProfile(userId, request));
    }

    @PostMapping("/verify")
    public Result<UserInfo> verifyCampus(@RequestBody CampusVerifyRequest request) {
        Long userId = authContext.getCurrentUserIdOrDefault(1L);
        return Result.success(userService.verifyCampus(userId, request));
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer verified
    ) {
        adminAccessGuard.requireAdminId();
        return Result.success(userService.getUserPage(page, size, keyword, status, verified));
    }

    @PostMapping("/status/{id}")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestBody UserStatusUpdateRequest request) {
        adminAccessGuard.requireAdminId();
        userService.updateUserStatus(id, request.getStatus());
        return Result.success();
    }
}
