package com.campus.lease.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.lease.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
