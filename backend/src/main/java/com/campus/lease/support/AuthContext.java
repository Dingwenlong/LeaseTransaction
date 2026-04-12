package com.campus.lease.support;

import com.campus.lease.common.exception.UnauthorizedException;
import com.campus.lease.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthContext {

    private final HttpServletRequest request;
    private final JwtUtil jwtUtil;

    public Long getCurrentUserIdOrDefault(Long defaultUserId) {
        Long userId = getCurrentUserId();
        return userId != null ? userId : defaultUserId;
    }

    public Long getCurrentUserId() {
        String token = resolveToken();
        if (StringUtils.isBlank(token) || !jwtUtil.validateToken(token)) {
            return null;
        }
        if (!JwtUtil.TOKEN_TYPE_CLIENT.equals(jwtUtil.getTokenType(token))) {
            return null;
        }
        return jwtUtil.getUserIdFromToken(token);
    }

    public Long requireAdminId() {
        String token = resolveToken();
        if (StringUtils.isBlank(token) || !jwtUtil.validateToken(token)) {
            throw new UnauthorizedException("后台登录已失效，请重新登录");
        }
        if (!JwtUtil.TOKEN_TYPE_ADMIN.equals(jwtUtil.getTokenType(token))) {
            throw new UnauthorizedException("无后台访问权限");
        }
        Long adminId = jwtUtil.getAdminIdFromToken(token);
        if (adminId == null) {
            throw new UnauthorizedException("无效的后台登录凭证");
        }
        return adminId;
    }

    public Long getCurrentAdminId() {
        String token = resolveToken();
        if (StringUtils.isBlank(token) || !jwtUtil.validateToken(token)) {
            return null;
        }
        if (!JwtUtil.TOKEN_TYPE_ADMIN.equals(jwtUtil.getTokenType(token))) {
            return null;
        }
        return jwtUtil.getAdminIdFromToken(token);
    }

    public String requireAdminRole() {
        String token = resolveToken();
        if (StringUtils.isBlank(token) || !jwtUtil.validateToken(token)) {
            throw new UnauthorizedException("后台登录已失效，请重新登录");
        }
        if (!JwtUtil.TOKEN_TYPE_ADMIN.equals(jwtUtil.getTokenType(token))) {
            throw new UnauthorizedException("无后台访问权限");
        }
        String role = jwtUtil.getAdminRoleFromToken(token);
        if (StringUtils.isBlank(role)) {
            throw new UnauthorizedException("无效的后台登录凭证");
        }
        return role;
    }

    private String resolveToken() {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isBlank(authorization) || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring(7);
    }
}
