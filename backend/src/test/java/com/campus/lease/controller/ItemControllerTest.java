package com.campus.lease.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.lease.common.constant.BusinessConstants;
import com.campus.lease.service.ItemService;
import com.campus.lease.support.AdminAccessGuard;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ItemControllerTest {

    @Test
    void getItemList_allowsNullStatusInAdminView() {
        ItemService itemService = mock(ItemService.class);
        TrackingAdminAccessGuard adminAccessGuard = new TrackingAdminAccessGuard();
        ItemController controller = new ItemController(itemService, null, adminAccessGuard);

        Page<Map<String, Object>> page = new Page<>(1, 12);
        when(itemService.getItemList(1, 12, null, null, null, null, null)).thenReturn(page);

        var result = controller.getItemList(1, 12, null, null, null, null, null, true);

        org.junit.jupiter.api.Assertions.assertTrue(adminAccessGuard.requireAdminCalled);
        verify(itemService).getItemList(eq(1), eq(12), isNull(), isNull(), isNull(), isNull(), isNull());
        assertSame(page, result.getData());
    }

    @Test
    void getItemList_defaultsToActiveStatusOutsideAdminView() {
        ItemService itemService = mock(ItemService.class);
        TrackingAdminAccessGuard adminAccessGuard = new TrackingAdminAccessGuard();
        ItemController controller = new ItemController(itemService, null, adminAccessGuard);

        Page<Map<String, Object>> page = new Page<>(1, 12);
        when(itemService.getItemList(1, 12, null, null, null, null, BusinessConstants.ItemStatus.ACTIVE)).thenReturn(page);

        var result = controller.getItemList(1, 12, null, null, null, null, null, false);

        verify(itemService).getItemList(1, 12, null, null, null, null, BusinessConstants.ItemStatus.ACTIVE);
        assertSame(page, result.getData());
    }

    private static class TrackingAdminAccessGuard extends AdminAccessGuard {

        private boolean requireAdminCalled;

        TrackingAdminAccessGuard() {
            super(null, null);
        }

        @Override
        public Long requireAdminId() {
            requireAdminCalled = true;
            return 1L;
        }
    }
}
