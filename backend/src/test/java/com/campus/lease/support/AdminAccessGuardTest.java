package com.campus.lease.support;

import com.campus.lease.common.exception.ForbiddenException;
import com.campus.lease.entity.SystemUser;
import com.campus.lease.service.SystemUserService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminAccessGuardTest {

    @Test
    void requireSuperAdminRejectsOperator() {
        AuthContext authContext = mock(AuthContext.class);
        SystemUserService systemUserService = mock(SystemUserService.class);
        AdminAccessGuard guard = new AdminAccessGuard(authContext, systemUserService);
        SystemUser operator = new SystemUser();
        operator.setId(7L);
        operator.setRole("OPERATOR");

        when(authContext.requireCurrentAdminId()).thenReturn(7L);
        when(systemUserService.getById(7L)).thenReturn(operator);

        assertThrows(ForbiddenException.class, guard::requireSuperAdminId);
    }

    @Test
    void requireSuperAdminReturnsAdminId() {
        AuthContext authContext = mock(AuthContext.class);
        SystemUserService systemUserService = mock(SystemUserService.class);
        AdminAccessGuard guard = new AdminAccessGuard(authContext, systemUserService);
        SystemUser superAdmin = new SystemUser();
        superAdmin.setId(1L);
        superAdmin.setRole("SUPER_ADMIN");

        when(authContext.requireCurrentAdminId()).thenReturn(1L);
        when(systemUserService.getById(1L)).thenReturn(superAdmin);

        assertEquals(1L, guard.requireSuperAdminId());
    }
}
