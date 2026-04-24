package com.campus.lease.support;

import com.campus.lease.common.exception.ForbiddenException;
import com.campus.lease.entity.SystemUser;
import com.campus.lease.service.SystemUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminAccessGuard {

    private final AuthContext authContext;
    private final SystemUserService systemUserService;

    public Long requireAdminId() {
        Long adminId = authContext.requireCurrentAdminId();
        systemUserService.ensureActiveAdmin(adminId);
        return adminId;
    }

    public Long requireSuperAdminId() {
        Long adminId = requireAdminId();
        SystemUser currentAdmin = systemUserService.getById(adminId);
        if (currentAdmin == null || !"SUPER_ADMIN".equals(currentAdmin.getRole())) {
            throw new ForbiddenException("仅超级管理员可执行此操作");
        }
        return adminId;
    }
}
