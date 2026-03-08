package com.campus.lease.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment_record")
public class PaymentRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String paymentNo;

    private Long orderId;

    private Long userId;

    private Integer type;

    private BigDecimal amount;

    private Integer paymentMethod;

    private String transactionId;

    private Integer status;

    private String failureReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
