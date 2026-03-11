package com.campus.lease.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.lease.dto.ItemAuditRequest;
import com.campus.lease.dto.ItemPublishRequest;
import com.campus.lease.entity.Item;

import java.util.List;
import java.util.Map;

public interface ItemService extends IService<Item> {
    Item publishItem(Long userId, ItemPublishRequest request);
    Page<Map<String, Object>> getItemList(Integer pageNum, Integer pageSize, String category, Integer type, String campus, String keyword, Integer status);
    Item getItemDetail(Long itemId);
    Map<String, Object> getItemDetailView(Long itemId);
    void updateViewCount(Long itemId);
    Page<Map<String, Object>> getMyItems(Long userId, Integer pageNum, Integer pageSize);
    void auditItem(Long itemId, ItemAuditRequest request);
    void updateItemStatus(Long itemId, Long userId, Integer status);
    List<Map<String, Object>> getNearbyItems(String campus, Integer limit);
}
