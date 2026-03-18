package com.campus.lease.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.lease.dto.AdminLoginRequest;
import com.campus.lease.dto.AdminLoginResponse;
import com.campus.lease.dto.AdminUserInfo;
import com.campus.lease.dto.SystemUserCreateRequest;
import com.campus.lease.dto.SystemUserPasswordResetRequest;
import com.campus.lease.dto.SystemUserStatusUpdateRequest;
import com.campus.lease.dto.SystemUserUpdateRequest;
import com.campus.lease.entity.SystemUser;

import java.util.Map;

public interface SystemUserService extends IService<SystemUser> {

    AdminLoginResponse login(AdminLoginRequest request);

    AdminUserInfo getAdminInfo(Long adminId);

    Map<String, Object> getSystemUserPage(Integer page, Integer size, String keyword, Integer status);

    AdminUserInfo createSystemUser(SystemUserCreateRequest request, Long operatorId);

    AdminUserInfo updateSystemUser(Long id, SystemUserUpdateRequest request, Long operatorId);

    void updateSystemUserStatus(Long id, SystemUserStatusUpdateRequest request, Long operatorId);

    void resetPassword(Long id, SystemUserPasswordResetRequest request, Long operatorId);

    void ensureBootstrapAdmin(String username, String password, String displayName);

    void ensureActiveAdmin(Long adminId);
}
