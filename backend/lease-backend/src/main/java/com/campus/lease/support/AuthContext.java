package com.campus.lease.support;

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
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isBlank(authorization) || !authorization.startsWith("Bearer ")) {
            return defaultUserId;
        }

        Long userId = jwtUtil.getUserIdFromToken(authorization.substring(7));
        return userId != null ? userId : defaultUserId;
    }
}
