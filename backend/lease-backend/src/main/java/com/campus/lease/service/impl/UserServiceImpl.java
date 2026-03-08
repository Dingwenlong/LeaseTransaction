package com.campus.lease.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.lease.dto.LoginResponse;
import com.campus.lease.dto.UserInfo;
import com.campus.lease.entity.User;
import com.campus.lease.mapper.UserMapper;
import com.campus.lease.service.UserService;
import com.campus.lease.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(String code) {
        String openid = "mock_openid_" + code.hashCode();
        String nickname = "校园用户";
        String avatar = "";

        User user = getUserByOpenid(openid);
        if (user == null) {
            user = createUser(openid, nickname, avatar);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getOpenid());
        UserInfo userInfo = convertToUserInfo(user);

        return new LoginResponse(token, userInfo);
    }

    @Override
    public User getUserByOpenid(String openid) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenid, openid);
        return getOne(wrapper);
    }

    @Override
    public User createUser(String openid, String nickname, String avatar) {
        User user = new User();
        user.setOpenid(openid);
        user.setNickname(nickname);
        user.setAvatar(avatar);
        user.setCreditScore(100);
        user.setIsVerified(0);
        user.setStatus(1);
        save(user);
        return user;
    }

    private UserInfo convertToUserInfo(User user) {
        UserInfo info = new UserInfo();
        info.setId(user.getId());
        info.setNickname(user.getNickname());
        info.setAvatar(user.getAvatar());
        info.setStudentId(user.getStudentId());
        info.setDepartment(user.getDepartment());
        info.setCampus(user.getCampus());
        info.setCreditScore(user.getCreditScore());
        info.setIsVerified(user.getIsVerified());
        return info;
    }
}
