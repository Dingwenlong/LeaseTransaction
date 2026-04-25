package com.campus.lease.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.lease.common.result.Result;
import com.campus.lease.entity.SystemConfig;
import com.campus.lease.mapper.SystemConfigMapper;
import com.campus.lease.support.AdminAccessGuard;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "系统配置", description = "后台系统配置读取和保存接口")
@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigController {

    private static final String CONFIG_KEY = "system";

    private final AdminAccessGuard adminAccessGuard;
    private final SystemConfigMapper systemConfigMapper;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        if (loadConfigEntity() != null) {
            return;
        }
        saveConfig(defaultConfig());
    }

    @Operation(summary = "获取系统配置", description = "后台读取系统配置，包含轮播图、公告、分类、校区和风控规则等内容")
    @GetMapping("/system")
    public Result<Map<String, Object>> getSystemConfig() {
        adminAccessGuard.requireAdminId();
        return Result.success(loadConfig());
    }

    @Operation(summary = "保存系统配置", description = "后台保存系统配置对象，支持覆盖已有配置项")
    @PostMapping("/system")
    public Result<Map<String, Object>> saveSystemConfig(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "系统配置对象，可包含 banners、announcements、categories、campuses、riskRules 等键",
                    required = true
            )
            @RequestBody Map<String, Object> request
    ) {
        adminAccessGuard.requireAdminId();
        Map<String, Object> config = loadConfig();
        config.putAll(request);
        saveConfig(config);
        return Result.success(config);
    }

    private Map<String, Object> loadConfig() {
        SystemConfig entity = loadConfigEntity();
        if (entity == null || entity.getConfigValue() == null) {
            return defaultConfig();
        }
        try {
            return objectMapper.readValue(entity.getConfigValue(), new TypeReference<>() {
            });
        } catch (Exception exception) {
            return defaultConfig();
        }
    }

    private SystemConfig loadConfigEntity() {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, CONFIG_KEY).last("limit 1");
        return systemConfigMapper.selectOne(wrapper);
    }

    private void saveConfig(Map<String, Object> config) {
        try {
            SystemConfig entity = loadConfigEntity();
            if (entity == null) {
                entity = new SystemConfig();
                entity.setConfigKey(CONFIG_KEY);
                entity.setConfigValue(objectMapper.writeValueAsString(config));
                systemConfigMapper.insert(entity);
            } else {
                entity.setConfigValue(objectMapper.writeValueAsString(config));
                systemConfigMapper.updateById(entity);
            }
        } catch (Exception exception) {
            throw new IllegalStateException("系统配置保存失败", exception);
        }
    }

    private Map<String, Object> defaultConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("banners", new ArrayList<>(List.of(
                Map.of("title", "新学期开租季", "subtitle", "数码与露营装备热租中", "active", true),
                Map.of("title", "信用免押专区", "subtitle", "高信用用户可减免押金", "active", true)
        )));
        config.put("announcements", new ArrayList<>(List.of(
                Map.of("title", "实名认证提醒", "content", "完成校园身份核验后可解锁更多交易能力。"),
                Map.of("title", "安全交易提示", "content", "建议校内当面验货，平台保留纠纷申诉入口。")
        )));
        config.put("categories", new ArrayList<>(List.of("电子产品", "书籍资料", "运动器材", "生活用品", "服饰配件", "毕业季专区")));
        config.put("campuses", new ArrayList<>(List.of("东校区", "西校区", "南校区", "北校区")));
        config.put("riskRules", new ArrayList<>(List.of(
                Map.of("name", "高价值物品二次审核", "enabled", true),
                Map.of("name", "低信用用户提高押金", "enabled", true),
                Map.of("name", "超过 7 天租赁提醒", "enabled", true)
        )));
        return config;
    }
}
