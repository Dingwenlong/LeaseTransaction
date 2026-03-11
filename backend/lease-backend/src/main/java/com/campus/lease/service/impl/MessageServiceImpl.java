package com.campus.lease.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.lease.common.constant.BusinessConstants;
import com.campus.lease.common.exception.BusinessException;
import com.campus.lease.dto.MessageSendRequest;
import com.campus.lease.entity.Message;
import com.campus.lease.entity.User;
import com.campus.lease.mapper.MessageMapper;
import com.campus.lease.service.MessageService;
import com.campus.lease.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public Page<Map<String, Object>> getMessagePage(Long userId, Integer pageNum, Integer pageSize, Integer type) {
        Page<Message> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(query -> query.eq(Message::getReceiverId, userId).or().eq(Message::getSenderId, userId));
        if (type != null) {
            wrapper.eq(Message::getType, type);
        }
        wrapper.orderByDesc(Message::getCreateTime);
        Page<Message> entityPage = page(page, wrapper);
        Page<Map<String, Object>> result = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        result.setRecords(entityPage.getRecords().stream().map(message -> convertToMessageMap(message, userId)).toList());
        return result;
    }

    @Override
    public Map<String, Object> sendMessage(Long userId, MessageSendRequest request) {
        if (request.getReceiverId() == null) {
            throw new BusinessException("请选择接收人");
        }
        if (StringUtils.isBlank(request.getContent()) && StringUtils.isBlank(request.getImages())) {
            throw new BusinessException("消息内容不能为空");
        }

        Message message = new Message();
        message.setSenderId(userId);
        message.setReceiverId(request.getReceiverId());
        message.setType(request.getType() == null ? BusinessConstants.MessageType.TEXT : request.getType());
        message.setContent(StringUtils.defaultString(request.getContent()));
        message.setImages(StringUtils.defaultString(request.getImages()));
        message.setIsRead(0);
        save(message);

        Map<String, Object> payload = convertToMessageMap(message, userId);
        messagingTemplate.convertAndSend("/topic/user." + request.getReceiverId(), payload);
        return payload;
    }

    @Override
    public void markRead(Long userId, Long messageId) {
        Message message = getById(messageId);
        if (message == null) {
            throw new BusinessException("消息不存在");
        }
        if (!userId.equals(message.getReceiverId())) {
            throw new BusinessException("无权操作该消息");
        }
        message.setIsRead(1);
        updateById(message);
    }

    @Override
    public long countUnread(Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getReceiverId, userId).eq(Message::getIsRead, 0);
        return count(wrapper);
    }

    @Override
    public void sendSystemMessage(Long receiverId, String title, String content) {
        Message message = new Message();
        message.setSenderId(0L);
        message.setReceiverId(receiverId);
        message.setType(BusinessConstants.MessageType.SYSTEM);
        message.setContent(title + "\n" + content);
        message.setIsRead(0);
        save(message);

        messagingTemplate.convertAndSend("/topic/user." + receiverId, convertToMessageMap(message, receiverId));
    }

    private Map<String, Object> convertToMessageMap(Message message, Long currentUserId) {
        User sender = message.getSenderId() == null || message.getSenderId() == 0 ? null : userService.getById(message.getSenderId());
        String rawContent = StringUtils.defaultString(message.getContent());
        String title = rawContent;
        String desc = rawContent;
        if (message.getType() != null && message.getType() == BusinessConstants.MessageType.SYSTEM && rawContent.contains("\n")) {
            String[] parts = rawContent.split("\n", 2);
            title = parts[0];
            desc = parts.length > 1 ? parts[1] : parts[0];
        }

        Map<String, Object> map = new HashMap<>();
        map.put("id", message.getId());
        map.put("senderId", message.getSenderId());
        map.put("receiverId", message.getReceiverId());
        map.put("title", message.getType() != null && message.getType() == BusinessConstants.MessageType.SYSTEM
                ? title
                : sender == null ? "系统消息" : StringUtils.defaultIfBlank(sender.getNickname(), sender.getStudentId()));
        map.put("desc", desc);
        map.put("content", rawContent);
        map.put("type", message.getType());
        map.put("time", message.getCreateTime());
        map.put("read", message.getIsRead() != null && message.getIsRead() == 1);
        map.put("self", currentUserId.equals(message.getSenderId()));
        map.put("icon", getIcon(message.getType()));
        return map;
    }

    private String getIcon(Integer type) {
        if (type == null) {
            return "💬";
        }
        return switch (type) {
            case BusinessConstants.MessageType.IMAGE -> "🖼";
            case BusinessConstants.MessageType.SYSTEM -> "🔔";
            default -> "💬";
        };
    }
}
