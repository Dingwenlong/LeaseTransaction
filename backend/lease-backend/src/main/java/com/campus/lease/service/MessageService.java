package com.campus.lease.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.lease.dto.MessageSendRequest;
import com.campus.lease.entity.Message;

import java.util.Map;

public interface MessageService extends IService<Message> {
    Page<Map<String, Object>> getMessagePage(Long userId, Integer pageNum, Integer pageSize, Integer type);
    Map<String, Object> sendMessage(Long userId, MessageSendRequest request);
    void markRead(Long userId, Long messageId);
    long countUnread(Long userId);
    void sendSystemMessage(Long receiverId, String title, String content);
}
