package com.campus.lease.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.lease.common.exception.BusinessException;
import com.campus.lease.common.exception.UnauthorizedException;
import com.campus.lease.dto.AdminLoginRequest;
import com.campus.lease.dto.AdminLoginResponse;
import com.campus.lease.dto.AdminUserInfo;
import com.campus.lease.dto.SystemUserCreateRequest;
import com.campus.lease.dto.SystemUserPasswordResetRequest;
import com.campus.lease.dto.SystemUserStatusUpdateRequest;
import com.campus.lease.dto.SystemUserUpdateRequest;
import com.campus.lease.entity.SystemUser;
import com.campus.lease.mapper.SystemUserMapper;
import com.campus.lease.service.SystemUserService;
import com.campus.lease.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements SystemUserService {

    private static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AdminLoginResponse login(AdminLoginRequest request) {
        SystemUser systemUser = getByUsername(request.getUsername());
        if (systemUser == null || !passwordEncoder.matches(request.getPassword(), systemUser.getPassword())) {
            throw new UnauthorizedException("用户名或密码错误");
        }
        if (systemUser.getStatus() == null || systemUser.getStatus() != 1) {
            throw new UnauthorizedException("后台账号已被禁用");
        }

        systemUser.setLastLoginTime(LocalDateTime.now());
        updateById(systemUser);

        String token = jwtUtil.generateAdminToken(systemUser.getId(), systemUser.getUsername(), systemUser.getRole());
        return new AdminLoginResponse(token, convertToInfo(systemUser));
    }

    @Override
    public AdminUserInfo getAdminInfo(Long adminId) {
        SystemUser systemUser = requireExisting(adminId);
        ensureActiveAdmin(systemUser.getId());
        return convertToInfo(systemUser);
    }

    @Override
    public Map<String, Object> getSystemUserPage(Integer page, Integer size, String keyword, Integer status) {
        Page<SystemUser> pageRequest = new Page<>(page, size);
        LambdaQueryWrapper<SystemUser> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(query -> query.like(SystemUser::getUsername, keyword)
                    .or()
                    .like(SystemUser::getDisplayName, keyword));
        }
        if (status != null) {
            wrapper.eq(SystemUser::getStatus, status);
        }

        wrapper.orderByAsc(SystemUser::getId);
        Page<SystemUser> systemUserPage = page(pageRequest, wrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("records", systemUserPage.getRecords().stream().map(this::convertToInfo).toList());
        result.put("total", systemUserPage.getTotal());
        result.put("size", systemUserPage.getSize());
        result.put("current", systemUserPage.getCurrent());
        result.put("pages", systemUserPage.getPages());
        return result;
    }

    @Override
    public AdminUserInfo createSystemUser(SystemUserCreateRequest request, Long operatorId) {
        if (getByUsername(request.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        SystemUser systemUser = new SystemUser();
        systemUser.setUsername(request.getUsername().trim());
        systemUser.setDisplayName(request.getDisplayName().trim());
        systemUser.setPassword(passwordEncoder.encode(request.getPassword()));
        systemUser.setRole(normalizeRole(request.getRole()));
        systemUser.setStatus(1);
        save(systemUser);
        return convertToInfo(requireExisting(systemUser.getId()));
    }

    @Override
    public AdminUserInfo updateSystemUser(Long id, SystemUserUpdateRequest request, Long operatorId) {
        SystemUser systemUser = requireExisting(id);
        if (StringUtils.isNotBlank(request.getDisplayName())) {
            systemUser.setDisplayName(request.getDisplayName().trim());
        }
        if (StringUtils.isNotBlank(request.getRole())) {
            systemUser.setRole(normalizeRole(request.getRole()));
        }
        if (request.getStatus() != null) {
            validateSelfDisable(id, request.getStatus(), operatorId);
            systemUser.setStatus(request.getStatus());
        }
        updateById(systemUser);
        return convertToInfo(requireExisting(id));
    }

    @Override
    public void updateSystemUserStatus(Long id, SystemUserStatusUpdateRequest request, Long operatorId) {
        SystemUser systemUser = requireExisting(id);
        validateSelfDisable(id, request.getStatus(), operatorId);
        systemUser.setStatus(request.getStatus());
        updateById(systemUser);
    }

    @Override
    public void resetPassword(Long id, SystemUserPasswordResetRequest request, Long operatorId) {
        SystemUser systemUser = requireExisting(id);
        systemUser.setPassword(passwordEncoder.encode(request.getPassword()));
        updateById(systemUser);
    }

    @Override
    public void ensureBootstrapAdmin(String username, String password, String displayName) {
        if (StringUtils.isAnyBlank(username, password, displayName)) {
            return;
        }
        if (getByUsername(username) != null) {
            return;
        }

        SystemUser systemUser = new SystemUser();
        systemUser.setUsername(username.trim());
        systemUser.setDisplayName(displayName.trim());
        systemUser.setPassword(passwordEncoder.encode(password));
        systemUser.setRole(ROLE_SUPER_ADMIN);
        systemUser.setStatus(1);
        save(systemUser);
    }

    @Override
    public void ensureActiveAdmin(Long adminId) {
        SystemUser systemUser = requireExisting(adminId);
        if (systemUser.getStatus() == null || systemUser.getStatus() != 1) {
            throw new UnauthorizedException("后台账号已被禁用");
        }
    }

    private SystemUser getByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return null;
        }
        LambdaQueryWrapper<SystemUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemUser::getUsername, username.trim());
        return getOne(wrapper, false);
    }

    private SystemUser requireExisting(Long id) {
        SystemUser systemUser = getById(id);
        if (systemUser == null) {
            throw new BusinessException("系统用户不存在");
        }
        return systemUser;
    }

    private void validateSelfDisable(Long targetId, Integer targetStatus, Long operatorId) {
        if (targetId != null && targetId.equals(operatorId) && targetStatus != null && targetStatus == 0) {
            throw new BusinessException("不能禁用当前登录账号");
        }
    }

    private String normalizeRole(String role) {
        String normalizedRole = StringUtils.defaultIfBlank(role, ROLE_SUPER_ADMIN).trim().toUpperCase();
        if (!ROLE_SUPER_ADMIN.equals(normalizedRole) && !"OPERATOR".equals(normalizedRole)) {
            throw new BusinessException("不支持的系统角色");
        }
        return normalizedRole;
    }

    private AdminUserInfo convertToInfo(SystemUser systemUser) {
        AdminUserInfo info = new AdminUserInfo();
        info.setId(systemUser.getId());
        info.setUsername(systemUser.getUsername());
        info.setDisplayName(systemUser.getDisplayName());
        info.setRole(systemUser.getRole());
        info.setStatus(systemUser.getStatus());
        info.setLastLoginTime(systemUser.getLastLoginTime());
        info.setCreatedAt(systemUser.getCreateTime());
        info.setUpdatedAt(systemUser.getUpdateTime());
        return info;
    }
}
