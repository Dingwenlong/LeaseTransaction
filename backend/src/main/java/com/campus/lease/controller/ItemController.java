package com.campus.lease.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.lease.common.result.Result;
import com.campus.lease.common.constant.BusinessConstants;
import com.campus.lease.dto.ItemAuditRequest;
import com.campus.lease.dto.ItemPublishRequest;
import com.campus.lease.entity.Item;
import com.campus.lease.service.ItemService;
import com.campus.lease.support.AdminAccessGuard;
import com.campus.lease.support.AuthContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final AuthContext authContext;
    private final AdminAccessGuard adminAccessGuard;

    @PostMapping("/publish")
    public Result<Item> publishItem(@RequestBody ItemPublishRequest request) {
        log.info("物品发布请求，title: {}", request.getTitle());
        Long userId = authContext.getCurrentUserIdOrDefault(2L);
        Item item = itemService.publishItem(userId, request);
        return Result.success(item);
    }

    @GetMapping("/list")
    public Result<Page<Map<String, Object>>> getItemList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String campus,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "false") Boolean adminView
    ) {
        if (Boolean.TRUE.equals(adminView)) {
            adminAccessGuard.requireAdminId();
        }
        Integer actualStatus = adminView ? status : (status == null ? BusinessConstants.ItemStatus.ACTIVE : status);
        Page<Map<String, Object>> result = itemService.getItemList(page, size, category, type, campus, keyword, actualStatus);
        return Result.success(result);
    }

    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> getItemDetail(@PathVariable Long id) {
        itemService.updateViewCount(id);
        return Result.success(itemService.getItemDetailView(id));
    }

    @GetMapping("/my")
    public Result<Page<Map<String, Object>>> getMyItems(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Long userId = authContext.getCurrentUserIdOrDefault(2L);
        return Result.success(itemService.getMyItems(userId, page, size));
    }

    @GetMapping("/nearby")
    public Result<List<Map<String, Object>>> getNearbyItems(
            @RequestParam(required = false) String campus,
            @RequestParam(defaultValue = "6") Integer limit
    ) {
        return Result.success(itemService.getNearbyItems(campus, limit));
    }

    @PostMapping("/approve/{id}")
    public Result<Void> approveItem(@PathVariable Long id) {
        adminAccessGuard.requireAdminId();
        ItemAuditRequest request = new ItemAuditRequest();
        request.setStatus(BusinessConstants.ItemStatus.ACTIVE);
        itemService.auditItem(id, request);
        return Result.success();
    }

    @PostMapping("/reject/{id}")
    public Result<Void> rejectItem(@PathVariable Long id, @RequestBody(required = false) ItemAuditRequest request) {
        adminAccessGuard.requireAdminId();
        ItemAuditRequest actualRequest = request == null ? new ItemAuditRequest() : request;
        actualRequest.setStatus(BusinessConstants.ItemStatus.REJECTED);
        itemService.auditItem(id, actualRequest);
        return Result.success();
    }

    @PostMapping("/status/{id}")
    public Result<Void> updateItemStatus(@PathVariable Long id, @RequestBody ItemAuditRequest request) {
        Long userId = authContext.getCurrentUserIdOrDefault(2L);
        itemService.updateItemStatus(id, userId, request.getStatus());
        return Result.success();
    }
}
