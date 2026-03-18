package com.campus.lease.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.lease.entity.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
