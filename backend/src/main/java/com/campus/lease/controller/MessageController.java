package com.campus.lease.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.lease.common.result.Result;
import com.campus.lease.dto.MessageSendRequest;
import com.campus.lease.service.MessageService;
import com.campus.lease.support.AuthContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final AuthContext authContext;

    @GetMapping("/list")
    public Result<Page<Map<String, Object>>> getMessages(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) Integer type
    ) {
        Long userId = authContext.getCurrentUserIdOrDefault(2L);
        return Result.success(messageService.getMessagePage(userId, page, size, type));
    }

    @PostMapping("/send")
    public Result<Map<String, Object>> sendMessage(@RequestBody MessageSendRequest request) {
        Long userId = authContext.getCurrentUserIdOrDefault(2L);
        return Result.success(messageService.sendMessage(userId, request));
    }

    @PostMapping("/read/{id}")
    public Result<Void> markRead(@PathVariable Long id) {
        Long userId = authContext.getCurrentUserIdOrDefault(2L);
        messageService.markRead(userId, id);
        return Result.success();
    }

    @GetMapping("/unread-count")
    public Result<Map<String, Object>> unreadCount() {
        Long userId = authContext.getCurrentUserIdOrDefault(2L);
        Map<String, Object> result = new HashMap<>();
        result.put("count", messageService.countUnread(userId));
        return Result.success(result);
    }
}
