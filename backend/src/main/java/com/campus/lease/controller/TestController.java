package com.campus.lease.controller;

import com.campus.lease.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "测试接口", description = "服务连通性与环境验证接口")
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Operation(summary = "服务健康测试", description = "快速验证后端服务是否已成功启动并能够正常响应请求")
    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("校园租赁交易系统启动成功！");
    }
}
