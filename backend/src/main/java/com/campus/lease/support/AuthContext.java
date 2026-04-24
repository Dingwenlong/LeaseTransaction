package com.campus.lease.support;

import com.campus.lease.common.exception.UnauthorizedException;
import com.campus.lease.security.AuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthContext {

    public Long requireCurrentUserId() {
        AuthPrincipal principal = getPrincipal();
        if (principal == null || principal.userId() == null) {
            throw new UnauthorizedException("请先登录后再操作");
        }
        return principal.userId();
    }

    public Long requireCurrentAdminId() {
        AuthPrincipal principal = getPrincipal();
        if (principal == null || principal.adminId() == null) {
            throw new UnauthorizedException("后台登录已失效，请重新登录");
        }
        return principal.adminId();
    }

    public Long getCurrentAdminIdOrNull() {
        AuthPrincipal principal = getPrincipal();
        if (principal == null) {
            return null;
        }
        return principal.adminId();
    }

    private AuthPrincipal getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthPrincipal principal)) {
            return null;
        }
        return principal;
    }
}
