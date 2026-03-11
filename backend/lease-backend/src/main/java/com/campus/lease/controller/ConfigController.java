package com.campus.lease.controller;

import com.campus.lease.common.result.Result;
import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private final Map<String, Object> config = new HashMap<>();

    @PostConstruct
    public void init() {
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
    }

    @GetMapping("/system")
    public Result<Map<String, Object>> getSystemConfig() {
        return Result.success(config);
    }

    @PostMapping("/system")
    public Result<Map<String, Object>> saveSystemConfig(@RequestBody Map<String, Object> request) {
        config.putAll(request);
        return Result.success(config);
    }
}
