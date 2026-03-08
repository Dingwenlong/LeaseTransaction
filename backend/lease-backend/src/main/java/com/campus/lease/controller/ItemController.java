package com.campus.lease.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.lease.common.result.Result;
import com.campus.lease.dto.ItemPublishRequest;
import com.campus.lease.entity.Item;
import com.campus.lease.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping("/publish")
    public Result<Item> publishItem(@RequestBody ItemPublishRequest request) {
        log.info("物品发布请求，title: {}", request.getTitle());
        Long userId = 1L;
        Item item = itemService.publishItem(userId, request);
        return Result.success(item);
    }

    @GetMapping("/list")
    public Result<Page<Item>> getItemList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String campus,
            @RequestParam(required = false) String keyword
    ) {
        Page<Item> page = itemService.getItemList(pageNum, pageSize, category, type, campus, keyword);
        return Result.success(page);
    }

    @GetMapping("/detail/{id}")
    public Result<Item> getItemDetail(@PathVariable Long id) {
        itemService.updateViewCount(id);
        Item item = itemService.getItemDetail(id);
        return Result.success(item);
    }
}
