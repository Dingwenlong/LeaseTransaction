package com.campus.lease.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.lease.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
