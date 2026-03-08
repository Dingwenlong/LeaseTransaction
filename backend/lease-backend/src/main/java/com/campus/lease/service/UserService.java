package com.campus.lease.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.lease.dto.LoginResponse;
import com.campus.lease.entity.User;

public interface UserService extends IService<User> {
    LoginResponse login(String code);
    User getUserByOpenid(String openid);
    User createUser(String openid, String nickname, String avatar);
}
