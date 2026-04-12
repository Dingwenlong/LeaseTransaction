package com.campus.lease.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "发送消息请求")
public class MessageSendRequest {
    @Schema(description = "接收者用户 ID", example = "2")
    private Long receiverId;

    @Schema(description = "消息类型，1 文本、2 图片、3 系统消息", example = "1")
    private Integer type;

    @Schema(description = "消息内容", example = "你好，这个相机什么时候可以取？")
    private String content;

    @Schema(description = "消息图片，多个地址用英文逗号分隔", example = "/uploads/2026/03/18/chat.png")
    private String images;

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
