package com.campus.lease.service;

import java.util.Map;

public interface CreditService {
    Map<String, Object> applyRule(Long userId, String rule, Long relatedOrderId, String note);
    String resolveCreditLevel(Integer score);
}
