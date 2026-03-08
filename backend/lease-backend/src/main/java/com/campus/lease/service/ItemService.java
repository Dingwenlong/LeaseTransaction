package com.campus.lease.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.lease.dto.ItemPublishRequest;
import com.campus.lease.entity.Item;

public interface ItemService extends IService<Item> {
    Item publishItem(Long userId, ItemPublishRequest request);
    Page<Item> getItemList(Integer pageNum, Integer pageSize, String category, Integer type, String campus, String keyword);
    Item getItemDetail(Long itemId);
    void updateViewCount(Long itemId);
}
