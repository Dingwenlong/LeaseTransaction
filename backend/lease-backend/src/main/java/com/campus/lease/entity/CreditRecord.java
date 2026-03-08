package com.campus.lease.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("credit_record")
public class CreditRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer type;

    private Integer scoreChange;

    private Integer beforeScore;

    private Integer afterScore;

    private String reason;

    private Long relatedOrderId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getScoreChange() {
        return scoreChange;
    }

    public void setScoreChange(Integer scoreChange) {
        this.scoreChange = scoreChange;
    }

    public Integer getBeforeScore() {
        return beforeScore;
    }

    public void setBeforeScore(Integer beforeScore) {
        this.beforeScore = beforeScore;
    }

    public Integer getAfterScore() {
        return afterScore;
    }

    public void setAfterScore(Integer afterScore) {
        this.afterScore = afterScore;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getRelatedOrderId() {
        return relatedOrderId;
    }

    public void setRelatedOrderId(Long relatedOrderId) {
        this.relatedOrderId = relatedOrderId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
