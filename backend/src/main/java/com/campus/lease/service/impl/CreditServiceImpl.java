package com.campus.lease.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.lease.common.constant.BusinessConstants;
import com.campus.lease.common.exception.BusinessException;
import com.campus.lease.entity.CreditRecord;
import com.campus.lease.entity.User;
import com.campus.lease.mapper.CreditRecordMapper;
import com.campus.lease.mapper.UserMapper;
import com.campus.lease.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {

    private final UserMapper userMapper;
    private final CreditRecordMapper creditRecordMapper;

    @Override
    @Transactional
    public Map<String, Object> applyRule(Long userId, String rule, Long relatedOrderId, String note) {
        if (userId == null) {
            throw new BusinessException("缺少信用处理用户");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        RuleDefinition definition = resolveDefinition(rule);
        if (shouldSkip(userId, definition.baseReason(), relatedOrderId, definition.dedupeScope())) {
            return buildSnapshot(user);
        }

        int beforeScore = normalizeScore(user.getCreditScore());
        int afterScore = Math.min(
                BusinessConstants.Credit.MAX_SCORE,
                Math.max(BusinessConstants.Credit.MIN_SCORE, beforeScore + definition.scoreDelta())
        );

        user.setCreditScore(afterScore);
        userMapper.updateById(user);

        CreditRecord record = new CreditRecord();
        record.setUserId(userId);
        record.setType(definition.scoreDelta() >= 0 ? 1 : 2);
        record.setScoreChange(Math.abs(definition.scoreDelta()));
        record.setBeforeScore(beforeScore);
        record.setAfterScore(afterScore);
        record.setReason(buildReason(definition.baseReason(), note));
        record.setRelatedOrderId(relatedOrderId);
        creditRecordMapper.insert(record);

        user.setCreditScore(afterScore);
        return buildSnapshot(user);
    }

    @Override
    public String resolveCreditLevel(Integer score) {
        int actualScore = normalizeScore(score);
        if (actualScore >= 90) {
            return "优秀";
        }
        if (actualScore >= 70) {
            return "良好";
        }
        if (actualScore >= 50) {
            return "一般";
        }
        return "较差";
    }

    private int normalizeScore(Integer score) {
        return score == null ? BusinessConstants.Credit.DEFAULT_SCORE : score;
    }

    private boolean shouldSkip(Long userId, String baseReason, Long relatedOrderId, DedupeScope dedupeScope) {
        LambdaQueryWrapper<CreditRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CreditRecord::getUserId, userId)
                .likeRight(CreditRecord::getReason, baseReason);

        if (dedupeScope == DedupeScope.ONCE_PER_USER) {
            return creditRecordMapper.selectCount(wrapper) > 0;
        }
        if (dedupeScope == DedupeScope.ONCE_PER_ORDER && relatedOrderId != null) {
            wrapper.eq(CreditRecord::getRelatedOrderId, relatedOrderId);
            return creditRecordMapper.selectCount(wrapper) > 0;
        }
        return false;
    }

    private String buildReason(String baseReason, String note) {
        return StringUtils.isBlank(note) ? baseReason : baseReason + " - " + note.trim();
    }

    private Map<String, Object> buildSnapshot(User user) {
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("creditScore", normalizeScore(user.getCreditScore()));
        result.put("creditLevel", resolveCreditLevel(user.getCreditScore()));
        return result;
    }

    private RuleDefinition resolveDefinition(String rule) {
        if (BusinessConstants.Credit.REAL_NAME.equals(rule)) {
            return new RuleDefinition(10, "实名认证通过", DedupeScope.ONCE_PER_USER);
        }
        if (BusinessConstants.Credit.SUCCESSFUL_TRANSACTION.equals(rule)) {
            return new RuleDefinition(5, "成功完成交易", DedupeScope.ONCE_PER_ORDER);
        }
        if (BusinessConstants.Credit.GOOD_REVIEW.equals(rule)) {
            return new RuleDefinition(3, "收到好评", DedupeScope.ONCE_PER_ORDER);
        }
        if (BusinessConstants.Credit.BAD_REVIEW.equals(rule)) {
            return new RuleDefinition(-5, "收到差评", DedupeScope.ONCE_PER_ORDER);
        }
        if (BusinessConstants.Credit.BREACH.equals(rule)) {
            return new RuleDefinition(-10, "违约处理", DedupeScope.ONCE_PER_ORDER);
        }
        if (BusinessConstants.Credit.COMPLAINT_CONFIRMED.equals(rule)) {
            return new RuleDefinition(-15, "投诉成立", DedupeScope.ONCE_PER_ORDER);
        }
        throw new BusinessException("不支持的信用规则类型");
    }

    private enum DedupeScope {
        NONE,
        ONCE_PER_USER,
        ONCE_PER_ORDER
    }

    private record RuleDefinition(int scoreDelta, String baseReason, DedupeScope dedupeScope) {
    }
}
