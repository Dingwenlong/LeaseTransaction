package com.campus.lease.controller;

import com.campus.lease.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "测试接口", description = "系统测试相关接口")
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Operation(summary = "测试接口", description = "验证系统是否正常启动")
    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("校园租赁交易系统启动成功！");
    }
}
