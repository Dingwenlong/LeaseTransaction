package com.campus.lease.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "创建订单请求")
public class CreateOrderRequest {
    @Schema(description = "物品 ID", example = "1")
    private Long itemId;

    @Schema(description = "订单类型，1 为租赁，2 为出售", example = "1")
    private Integer type;

    @Schema(description = "租赁开始时间，出售订单可不传", example = "2026-03-20T10:00:00")
    private LocalDateTime startDate;

    @Schema(description = "租赁结束时间，出售订单可不传", example = "2026-03-25T10:00:00")
    private LocalDateTime endDate;

    @Schema(description = "租赁天数", example = "5")
    private Integer rentalDays;

    @Schema(description = "交付方式", example = "线下自提")
    private String deliveryMethod;

    @Schema(description = "订单备注", example = "周五下午在东门交接")
    private String remark;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getRentalDays() {
        return rentalDays;
    }

    public void setRentalDays(Integer rentalDays) {
        this.rentalDays = rentalDays;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
