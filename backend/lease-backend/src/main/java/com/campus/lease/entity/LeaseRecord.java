package com.campus.lease.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("lease_record")
public class LeaseRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long itemId;

    private Long userId;

    private Long lesseeId;

    private LocalDateTime leaseStart;

    private LocalDateTime leaseEnd;

    private LocalDateTime actualReturn;

    private Integer isOverdue;

    private BigDecimal overdueFee;

    private String damageDescription;

    private BigDecimal damageCompensation;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
