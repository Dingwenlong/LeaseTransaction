package com.campus.lease.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.lease.common.exception.BusinessException;
import com.campus.lease.dto.CampusVerifyRequest;
import com.campus.lease.dto.LoginRequest;
import com.campus.lease.dto.LoginResponse;
import com.campus.lease.dto.UserInfo;
import com.campus.lease.dto.UserProfileUpdateRequest;
import com.campus.lease.entity.User;
import com.campus.lease.mapper.UserMapper;
import com.campus.lease.service.UserService;
import com.campus.lease.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user;
        if (StringUtils.isNotBlank(request.getCode())) {
            String openid = "wx_" + Math.abs(request.getCode().hashCode());
            String nickname = StringUtils.defaultIfBlank(request.getNickname(), "校园用户");
            String avatar = StringUtils.defaultString(request.getAvatarUrl());
            user = getUserByOpenid(openid);
            if (user == null) {
                user = createUser(openid, nickname, avatar);
            }
        } else if (StringUtils.isNotBlank(request.getUsername())) {
            user = getOrCreateUserByUsername(
                    request.getUsername(),
                    StringUtils.defaultIfBlank(request.getNickname(), request.getUsername()),
                    StringUtils.defaultString(request.getAvatarUrl())
            );
        } else {
            throw new BusinessException("缺少登录凭证");
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用，请联系管理员");
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
        user.setNickname(StringUtils.defaultIfBlank(nickname, "校园用户"));
        user.setAvatar(StringUtils.defaultString(avatar));
        user.setCreditScore(100);
        user.setIsVerified(0);
        user.setStatus(1);
        save(user);
        return user;
    }

    @Override
    public User getOrCreateUserByUsername(String username, String nickname, String avatar) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStudentId, username)
                .or()
                .eq(User::getOpenid, "account_" + username);
        User user = getOne(wrapper, false);
        if (user != null) {
            if (StringUtils.isBlank(user.getNickname()) && StringUtils.isNotBlank(nickname)) {
                user.setNickname(nickname);
            }
            if (StringUtils.isBlank(user.getAvatar()) && StringUtils.isNotBlank(avatar)) {
                user.setAvatar(avatar);
            }
            updateById(user);
            return user;
        }

        user = createUser("account_" + username, nickname, avatar);
        user.setStudentId(username);
        if (StringUtils.containsIgnoreCase(username, "admin")) {
            user.setDepartment("平台运营");
            user.setCampus("管理中心");
            user.setIsVerified(1);
        }
        updateById(user);
        return user;
    }

    @Override
    public UserInfo getUserInfo(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToUserInfo(user);
    }

    @Override
    public UserInfo updateProfile(Long userId, UserProfileUpdateRequest request) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (StringUtils.isNotBlank(request.getNickname())) {
            user.setNickname(request.getNickname());
        }
        if (StringUtils.isNotBlank(request.getAvatar())) {
            user.setAvatar(request.getAvatar());
        }
        if (StringUtils.isNotBlank(request.getDepartment())) {
            user.setDepartment(request.getDepartment());
        }
        if (StringUtils.isNotBlank(request.getCampus())) {
            user.setCampus(request.getCampus());
        }

        updateById(user);
        return convertToUserInfo(user);
    }

    @Override
    public UserInfo verifyCampus(Long userId, CampusVerifyRequest request) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (StringUtils.isAnyBlank(request.getStudentId(), request.getDepartment(), request.getCampus())) {
            throw new BusinessException("请完整填写学号、院系和校区信息");
        }

        user.setStudentId(request.getStudentId());
        user.setDepartment(request.getDepartment());
        user.setCampus(request.getCampus());
        user.setIsVerified(1);
        user.setCreditScore(Math.max(100, user.getCreditScore() == null ? 100 : user.getCreditScore()));
        updateById(user);
        return convertToUserInfo(user);
    }

    @Override
    public Map<String, Object> getUserPage(Integer current, Integer size, String keyword, Integer status, Integer verified) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(condition -> condition
                    .like(User::getNickname, keyword)
                    .or()
                    .like(User::getStudentId, keyword)
                    .or()
                    .like(User::getDepartment, keyword)
                    .or()
                    .like(User::getCampus, keyword));
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        if (verified != null) {
            wrapper.eq(User::getIsVerified, verified);
        }

        wrapper.orderByDesc(User::getCreateTime);
        Page<User> userPage = page(page, wrapper);
        List<Map<String, Object>> records = userPage.getRecords().stream()
                .map(this::convertToUserMap)
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", userPage.getTotal());
        result.put("size", userPage.getSize());
        result.put("current", userPage.getCurrent());
        result.put("pages", userPage.getPages());
        return result;
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(status);
        updateById(user);
    }

    private UserInfo convertToUserInfo(User user) {
        UserInfo info = new UserInfo();
        info.setId(user.getId());
        info.setNickname(StringUtils.defaultIfBlank(user.getNickname(), "校园用户"));
        info.setAvatar(StringUtils.defaultString(user.getAvatar()));
        info.setStudentId(user.getStudentId());
        info.setDepartment(user.getDepartment());
        info.setCampus(user.getCampus());
        info.setCreditScore(user.getCreditScore() == null ? 100 : user.getCreditScore());
        info.setIsVerified(user.getIsVerified() == null ? 0 : user.getIsVerified());
        info.setStatus(user.getStatus() == null ? 1 : user.getStatus());
        return info;
    }

    private Map<String, Object> convertToUserMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("username", StringUtils.defaultIfBlank(user.getStudentId(), "U" + user.getId()));
        map.put("nickname", StringUtils.defaultIfBlank(user.getNickname(), "校园用户"));
        map.put("avatar", StringUtils.defaultString(user.getAvatar()));
        map.put("studentId", user.getStudentId());
        map.put("department", user.getDepartment());
        map.put("campus", user.getCampus());
        map.put("creditScore", user.getCreditScore() == null ? 100 : user.getCreditScore());
        map.put("isVerified", user.getIsVerified() == null ? 0 : user.getIsVerified());
        map.put("status", user.getStatus() == null ? 1 : user.getStatus());
        map.put("createdAt", user.getCreateTime());
        map.put("updatedAt", user.getUpdateTime());
        return map;
    }
}
