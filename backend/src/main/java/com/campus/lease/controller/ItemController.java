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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "物品管理", description = "物品发布、列表查询、详情查看和后台审核接口")
@RestController
@RequestMapping("/api/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final AuthContext authContext;
    private final AdminAccessGuard adminAccessGuard;

    @Operation(summary = "发布物品", description = "用户发布新的租赁或出售物品信息")
    @PostMapping("/publish")
    public Result<Item> publishItem(@RequestBody ItemPublishRequest request) {
        log.info("物品发布请求，title: {}", request.getTitle());
        Long userId = authContext.getCurrentUserIdOrDefault(2L);
        Item item = itemService.publishItem(userId, request);
        return Result.success(item);
    }

    @Operation(summary = "分页查询物品列表", description = "查询物品列表。普通视图默认仅返回上架物品；adminView=true 时返回后台审核视图并允许按状态筛选")
    @GetMapping("/list")
    public Result<Page<Map<String, Object>>> getItemList(
            @Parameter(description = "页码，从 1 开始", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "物品分类", example = "电子产品")
            @RequestParam(required = false) String category,
            @Parameter(description = "物品类型，1 为租赁，2 为出售", example = "1")
            @RequestParam(required = false) Integer type,
            @Parameter(description = "校区名称", example = "东校区")
            @RequestParam(required = false) String campus,
            @Parameter(description = "关键字，匹配标题和描述", example = "相机")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "物品状态，后台视图下可传入审核或上架状态", example = "1")
            @RequestParam(required = false) Integer status,
            @Parameter(description = "是否启用后台视图；为 true 时需要后台管理员身份", example = "false")
            @RequestParam(defaultValue = "false") Boolean adminView
    ) {
        if (Boolean.TRUE.equals(adminView)) {
            adminAccessGuard.requireAdminId();
        }

        Integer actualStatus;
        if (Boolean.TRUE.equals(adminView)) {
            actualStatus = status;
        } else {
            actualStatus = status == null ? BusinessConstants.ItemStatus.ACTIVE : status;
        }

        Page<Map<String, Object>> result = itemService.getItemList(page, size, category, type, campus, keyword, actualStatus);
        return Result.success(result);
    }

    @Operation(summary = "获取物品详情", description = "根据物品 ID 获取详情，并自动累计浏览量")
    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> getItemDetail(
            @Parameter(description = "物品 ID", example = "1")
            @PathVariable Long id
    ) {
        itemService.updateViewCount(id);
        return Result.success(itemService.getItemDetailView(id));
    }

    @Operation(summary = "查询我的物品", description = "获取当前登录用户发布的物品列表")
    @GetMapping("/my")
    public Result<Page<Map<String, Object>>> getMyItems(
            @Parameter(description = "页码，从 1 开始", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Long userId = authContext.getCurrentUserIdOrDefault(2L);
        return Result.success(itemService.getMyItems(userId, page, size));
    }

    @Operation(summary = "查询附近推荐物品", description = "按校区获取附近推荐物品列表")
    @GetMapping("/nearby")
    public Result<List<Map<String, Object>>> getNearbyItems(
            @Parameter(description = "校区名称，不传则使用默认推荐逻辑", example = "东校区")
            @RequestParam(required = false) String campus,
            @Parameter(description = "返回数量上限", example = "6")
            @RequestParam(defaultValue = "6") Integer limit
    ) {
        return Result.success(itemService.getNearbyItems(campus, limit));
    }

    @Operation(summary = "审核通过物品", description = "后台管理员将指定物品审核通过并上架")
    @PostMapping("/approve/{id}")
    public Result<Void> approveItem(
            @Parameter(description = "物品 ID", example = "1")
            @PathVariable Long id
    ) {
        adminAccessGuard.requireAdminId();
        ItemAuditRequest request = new ItemAuditRequest();
        request.setStatus(BusinessConstants.ItemStatus.ACTIVE);
        itemService.auditItem(id, request);
        return Result.success();
    }

    @Operation(summary = "驳回物品", description = "后台管理员驳回指定物品，可附带驳回原因")
    @PostMapping("/reject/{id}")
    public Result<Void> rejectItem(
            @Parameter(description = "物品 ID", example = "1")
            @PathVariable Long id,
            @RequestBody(required = false) ItemAuditRequest request
    ) {
        adminAccessGuard.requireAdminId();
        ItemAuditRequest actualRequest = request == null ? new ItemAuditRequest() : request;
        actualRequest.setStatus(BusinessConstants.ItemStatus.REJECTED);
        itemService.auditItem(id, actualRequest);
        return Result.success();
    }

    @Operation(summary = "更新物品状态", description = "发布者更新自己物品的状态，例如下架、重新上架等")
    @PostMapping("/status/{id}")
    public Result<Void> updateItemStatus(
            @Parameter(description = "物品 ID", example = "1")
            @PathVariable Long id,
            @RequestBody ItemAuditRequest request
    ) {
        Long userId = authContext.getCurrentUserIdOrDefault(2L);
        itemService.updateItemStatus(id, userId, request.getStatus());
        return Result.success();
    }
}
