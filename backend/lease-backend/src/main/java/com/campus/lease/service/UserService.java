package com.campus.lease.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.lease.dto.CampusVerifyRequest;
import com.campus.lease.dto.LoginRequest;
import com.campus.lease.dto.LoginResponse;
import com.campus.lease.dto.UserInfo;
import com.campus.lease.dto.UserProfileUpdateRequest;
import com.campus.lease.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {
    LoginResponse login(LoginRequest request);
    User getUserByOpenid(String openid);
    User createUser(String openid, String nickname, String avatar);
    User getOrCreateUserByUsername(String username, String nickname, String avatar);
    UserInfo getUserInfo(Long userId);
    UserInfo updateProfile(Long userId, UserProfileUpdateRequest request);
    UserInfo verifyCampus(Long userId, CampusVerifyRequest request);
    Map<String, Object> getUserPage(Integer current, Integer size, String keyword, Integer status, Integer verified);
    void updateUserStatus(Long userId, Integer status);
}
