package com.campus.lease.support;

import com.campus.lease.common.exception.UnauthorizedException;
import com.campus.lease.security.AuthPrincipal;
import com.campus.lease.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthContextTest {

    private final AuthContext authContext = new AuthContext();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void requireCurrentUserIdRejectsAnonymousAccess() {
        assertThrows(UnauthorizedException.class, authContext::requireCurrentUserId);
    }

    @Test
    void requireCurrentUserIdReturnsClientUserId() {
        AuthPrincipal principal = new AuthPrincipal(12L, null, null, null, JwtUtil.TOKEN_TYPE_CLIENT);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, "token", List.of(new SimpleGrantedAuthority("ROLE_CLIENT")))
        );

        assertEquals(12L, authContext.requireCurrentUserId());
    }

    @Test
    void requireCurrentAdminIdReturnsAdminId() {
        AuthPrincipal principal = new AuthPrincipal(null, 3L, "admin", "SUPER_ADMIN", JwtUtil.TOKEN_TYPE_ADMIN);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, "token", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
        );

        assertEquals(3L, authContext.requireCurrentAdminId());
    }

    @Test
    void requireCurrentUserIdRejectsAdminToken() {
        AuthPrincipal principal = new AuthPrincipal(null, 3L, "admin", "OPERATOR", JwtUtil.TOKEN_TYPE_ADMIN);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, "token", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
        );

        assertThrows(UnauthorizedException.class, authContext::requireCurrentUserId);
    }

    @Test
    void requireCurrentAdminIdRejectsClientToken() {
        AuthPrincipal principal = new AuthPrincipal(12L, null, null, null, JwtUtil.TOKEN_TYPE_CLIENT);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, "token", List.of(new SimpleGrantedAuthority("ROLE_CLIENT")))
        );

        assertThrows(UnauthorizedException.class, authContext::requireCurrentAdminId);
    }
}
