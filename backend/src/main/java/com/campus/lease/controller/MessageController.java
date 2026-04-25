package com.campus.lease.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.lease.common.result.Result;
import com.campus.lease.dto.MessageSendRequest;
import com.campus.lease.service.MessageService;
import com.campus.lease.support.AuthContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "消息中心", description = "站内消息列表、发送、已读和未读统计接口")
@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final AuthContext authContext;

    @Operation(summary = "分页查询消息列表", description = "获取当前登录用户的消息列表，可按消息类型筛选")
    @GetMapping("/list")
    public Result<Page<Map<String, Object>>> getMessages(
            @Parameter(description = "页码，从 1 开始", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量", example = "20")
            @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "消息类型，1 文本、2 图片、3 系统消息", example = "1")
            @RequestParam(required = false) Integer type
    ) {
        Long userId = authContext.requireCurrentUserId();
        return Result.success(messageService.getMessagePage(userId, page, size, type));
    }

    @Operation(summary = "发送消息", description = "向指定接收者发送站内消息")
    @PostMapping("/send")
    public Result<Map<String, Object>> sendMessage(@RequestBody MessageSendRequest request) {
        Long userId = authContext.requireCurrentUserId();
        return Result.success(messageService.sendMessage(userId, request));
    }

    @Operation(summary = "标记消息已读", description = "将指定消息标记为已读状态")
    @PostMapping("/read/{id}")
    public Result<Void> markRead(
            @Parameter(description = "消息 ID", example = "1")
            @PathVariable Long id
    ) {
        Long userId = authContext.requireCurrentUserId();
        messageService.markRead(userId, id);
        return Result.success();
    }

    @Operation(summary = "获取未读消息数量", description = "返回当前登录用户的未读消息总数")
    @GetMapping("/unread-count")
    public Result<Map<String, Object>> unreadCount() {
        Long userId = authContext.requireCurrentUserId();
        Map<String, Object> result = new HashMap<>();
        result.put("count", messageService.countUnread(userId));
        return Result.success(result);
    }
}
