package com.campus.lease.controller;

import com.campus.lease.common.result.Result;
import com.campus.lease.dto.LoginRequest;
import com.campus.lease.dto.LoginResponse;
import com.campus.lease.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "用户接口", description = "用户相关接口")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户登录", description = "通过微信code进行用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("用户登录请求，code: {}", request.getCode());
        LoginResponse response = userService.login(request.getCode());
        return Result.success(response);
    }

    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/info")
    public Result<String> getUserInfo() {
        return Result.success("获取用户信息");
    }
}
