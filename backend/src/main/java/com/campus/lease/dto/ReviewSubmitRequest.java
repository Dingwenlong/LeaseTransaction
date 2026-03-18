package com.campus.lease.dto;

import lombok.Data;

@Data
public class ReviewSubmitRequest {
    private Long orderId;
    private Integer rating;
    private String content;
    private String images;
    private Integer isAnonymous;
}
