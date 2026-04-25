package com.campus.lease.controller;

import com.campus.lease.common.result.Result;
import com.campus.lease.dto.CampusVerifyRequest;
import com.campus.lease.dto.LoginRequest;
import com.campus.lease.dto.LoginResponse;
import com.campus.lease.dto.RegisterRequest;
import com.campus.lease.dto.UserInfo;
import com.campus.lease.dto.UserCreditAdjustRequest;
import com.campus.lease.dto.UserProfileUpdateRequest;
import com.campus.lease.dto.UserStatusUpdateRequest;
import com.campus.lease.service.UserService;
import com.campus.lease.support.AdminAccessGuard;
import com.campus.lease.support.AuthContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(summary = "用户注册", description = "通过用户名和密码注册新账号")
    @PostMapping("/register")
    public Result<LoginResponse> register(@RequestBody RegisterRequest request) {
        log.info("用户注册请求，username: {}", request.getUsername());
        LoginResponse response = userService.register(request);
        return Result.success(response);
    }

    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/info")
    public Result<UserInfo> getUserInfo() {
        Long userId = authContext.requireCurrentUserId();
        return Result.success(userService.getUserInfo(userId));
    }

    @Operation(summary = "更新用户资料", description = "更新当前登录用户的昵称、头像、院系和校区信息")
    @PutMapping("/profile")
    public Result<UserInfo> updateProfile(@RequestBody UserProfileUpdateRequest request) {
        Long userId = authContext.requireCurrentUserId();
        return Result.success(userService.updateProfile(userId, request));
    }

    @Operation(summary = "提交校园认证", description = "提交学号、院系和校区信息，完成校园身份核验")
    @PostMapping("/verify")
    public Result<UserInfo> verifyCampus(@RequestBody CampusVerifyRequest request) {
        Long userId = authContext.requireCurrentUserId();
        return Result.success(userService.verifyCampus(userId, request));
    }

    @Operation(summary = "获取公开用户主页", description = "根据用户 ID 获取公开展示的信誉主页信息")
    @GetMapping("/profile/{id}")
    public Result<Map<String, Object>> getUserProfile(
            @Parameter(description = "用户 ID", example = "1")
            @PathVariable Long id
    ) {
        return Result.success(userService.getPublicProfile(id));
    }

    @Operation(summary = "分页查询用户列表", description = "后台管理分页查询用户，可按关键字、账号状态和认证状态筛选")
    @GetMapping("/list")
    public Result<Map<String, Object>> getUserList(
            @Parameter(description = "页码，从 1 开始", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "关键字，匹配昵称、学号等信息", example = "张三")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "用户状态，0 为禁用，1 为正常", example = "1")
            @RequestParam(required = false) Integer status,
            @Parameter(description = "认证状态，0 为未认证，1 为已认证", example = "1")
            @RequestParam(required = false) Integer verified
    ) {
        adminAccessGuard.requireAdminId();
        return Result.success(userService.getUserPage(page, size, keyword, status, verified));
    }

    @Operation(summary = "修改用户状态", description = "后台管理修改指定用户的账号状态")
    @PostMapping("/status/{id}")
    public Result<Void> updateUserStatus(
            @Parameter(description = "用户 ID", example = "1")
            @PathVariable Long id,
            @RequestBody UserStatusUpdateRequest request
    ) {
        adminAccessGuard.requireAdminId();
        userService.updateUserStatus(id, request.getStatus());
        return Result.success();
    }

    @Operation(summary = "调整用户信用分", description = "后台管理根据违约或投诉成立等场景人工调整用户信用分")
    @PostMapping("/credit/{id}")
    public Result<Map<String, Object>> adjustUserCredit(
            @Parameter(description = "用户 ID", example = "1")
            @PathVariable Long id,
            @RequestBody UserCreditAdjustRequest request
    ) {
        adminAccessGuard.requireAdminId();
        return Result.success(userService.adjustUserCredit(id, request));
    }
}
