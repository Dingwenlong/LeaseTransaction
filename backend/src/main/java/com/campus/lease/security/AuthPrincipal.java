package com.campus.lease.security;

public record AuthPrincipal(
        Long userId,
        Long adminId,
        String username,
        String role,
        String tokenType
) {
}
