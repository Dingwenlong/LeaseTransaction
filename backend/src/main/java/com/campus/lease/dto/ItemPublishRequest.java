package com.campus.lease.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "物品发布请求")
public class ItemPublishRequest {
    @Schema(description = "物品标题", example = "佳能单反相机")
    private String title;

    @Schema(description = "物品描述", example = "九成新，配充电器和相机包")
    private String description;

    @Schema(description = "图片地址，多个使用英文逗号分隔", example = "/uploads/2026/03/18/a.jpg,/uploads/2026/03/18/b.jpg")
    private String images;

    @Schema(description = "物品分类", example = "电子产品")
    private String category;

    @Schema(description = "物品类型，1 为租赁，2 为出售", example = "1")
    private Integer type;

    @Schema(description = "价格", example = "20.00")
    private BigDecimal price;

    @Schema(description = "押金", example = "200.00")
    private BigDecimal deposit;

    @Schema(description = "所在校区", example = "东校区")
    private String campus;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }
}
