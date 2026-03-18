package com.campus.lease.controller;

import com.campus.lease.common.constant.BusinessConstants;
import com.campus.lease.common.result.Result;
import com.campus.lease.entity.Item;
import com.campus.lease.entity.Order;
import com.campus.lease.entity.PaymentRecord;
import com.campus.lease.entity.User;
import com.campus.lease.mapper.PaymentRecordMapper;
import com.campus.lease.service.ItemService;
import com.campus.lease.service.OrderService;
import com.campus.lease.service.UserService;
import com.campus.lease.support.AdminAccessGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final UserService userService;
    private final ItemService itemService;
    private final OrderService orderService;
    private final PaymentRecordMapper paymentRecordMapper;
    private final AdminAccessGuard adminAccessGuard;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard() {
        adminAccessGuard.requireAdminId();
        List<User> users = userService.list();
        List<Item> items = itemService.list();
        List<Order> orders = orderService.list();
        List<PaymentRecord> payments = paymentRecordMapper.selectList(null);

        long activeUsers = users.stream().filter(user -> user.getStatus() == null || user.getStatus() == 1).count();
        long verifiedUsers = users.stream().filter(user -> user.getIsVerified() != null && user.getIsVerified() == 1).count();
        long pendingItems = items.stream().filter(item -> item.getStatus() != null && item.getStatus() == BusinessConstants.ItemStatus.PENDING_REVIEW).count();
        long activeItems = items.stream().filter(item -> item.getStatus() != null && item.getStatus() == BusinessConstants.ItemStatus.ACTIVE).count();
        long activeOrders = orders.stream().filter(order -> order.getStatus() != null &&
                (order.getStatus() == BusinessConstants.OrderStatus.PAID || order.getStatus() == BusinessConstants.OrderStatus.IN_PROGRESS || order.getStatus() == BusinessConstants.OrderStatus.PENDING_RETURN)).count();
        long disputeOrders = orders.stream().filter(order -> order.getStatus() != null && order.getStatus() == BusinessConstants.OrderStatus.DISPUTE).count();
        BigDecimal financeTotal = payments.stream()
                .filter(record -> record.getStatus() != null && record.getStatus() == 1 && record.getType() != null && record.getType() == BusinessConstants.PaymentType.PAYMENT)
                .map(PaymentRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        Map<String, Object> result = new HashMap<>();
        result.put("hero", Map.of(
                "title", "Campus Lease Console",
                "subtitle", "查看首页关键指标和当前待处理事项。",
                "updatedAt", LocalDateTime.now()
        ));
        result.put("metrics", List.of(
                metric("活跃用户", String.valueOf(activeUsers), "已认证 " + verifiedUsers, activeUsers > 0 ? "cyan" : "slate"),
                metric("上架物品", String.valueOf(activeItems), "待审 " + pendingItems, pendingItems > 0 ? "yellow" : "green"),
                metric("进行中订单", String.valueOf(activeOrders), "纠纷 " + disputeOrders, disputeOrders > 0 ? "magenta" : "cyan"),
                metric("累计交易额", "¥" + financeTotal.toPlainString(), "已支付流水", "green")
        ));
        result.put("campusDistribution", users.stream()
                .filter(user -> user.getCampus() != null)
                .collect(Collectors.groupingBy(User::getCampus, Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> Map.of("name", entry.getKey(), "value", entry.getValue()))
                .sorted(Comparator.comparing(item -> -((Long) item.get("value"))))
                .toList());
        result.put("categoryRanking", items.stream()
                .filter(item -> item.getCategory() != null)
                .collect(Collectors.groupingBy(Item::getCategory, Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> Map.of("name", entry.getKey(), "value", entry.getValue()))
                .sorted(Comparator.comparing(item -> -((Long) item.get("value"))))
                .toList());
        result.put("orderStatusDistribution", orders.stream()
                .collect(Collectors.groupingBy(order -> statusText(order.getStatus()), Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> Map.of("name", entry.getKey(), "value", entry.getValue()))
                .toList());
        result.put("watchList", List.of(
                Map.of("title", "待审核物品", "value", pendingItems, "text", "优先清理图片不完整、价格异常和分类不准确的发布。"),
                Map.of("title", "进行中租赁", "value", activeOrders, "text", "关注即将到期和押金较高的订单，提前触发提醒。"),
                Map.of("title", "校区覆盖", "value", users.stream().map(User::getCampus).filter(campus -> campus != null && !campus.isBlank()).distinct().count(), "text", "评估附近推荐和校区配送策略。")
        ));
        return Result.success(result);
    }

    @GetMapping("/report")
    public Result<Map<String, Object>> getReport() {
        adminAccessGuard.requireAdminId();
        List<Order> orders = orderService.list();
        List<PaymentRecord> payments = paymentRecordMapper.selectList(null);

        BigDecimal depositTotal = payments.stream()
                .filter(record -> record.getType() != null && record.getType() == BusinessConstants.PaymentType.DEPOSIT)
                .map(PaymentRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal refundTotal = payments.stream()
                .filter(record -> record.getType() != null && record.getType() == BusinessConstants.PaymentType.REFUND)
                .map(PaymentRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> result = new HashMap<>();
        result.put("finance", Map.of(
                "income", payments.stream()
                        .filter(record -> record.getType() != null && record.getType() == BusinessConstants.PaymentType.PAYMENT && record.getStatus() != null && record.getStatus() == 1)
                        .map(PaymentRecord::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                "refund", refundTotal,
                "deposit", depositTotal
        ));
        result.put("leaseOrders", orders.stream().filter(order -> order.getType() != null && order.getType() == BusinessConstants.OrderType.LEASE).count());
        result.put("saleOrders", orders.stream().filter(order -> order.getType() != null && order.getType() == BusinessConstants.OrderType.SALE).count());
        result.put("completedOrders", orders.stream().filter(order -> order.getStatus() != null && order.getStatus() == BusinessConstants.OrderStatus.COMPLETED).count());
        return Result.success(result);
    }

    private Map<String, Object> metric(String label, String value, String delta, String tone) {
        Map<String, Object> result = new HashMap<>();
        result.put("label", label);
        result.put("value", value);
        result.put("delta", delta);
        result.put("tone", tone);
        return result;
    }

    private String statusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case BusinessConstants.OrderStatus.PENDING_PAYMENT -> "待付款";
            case BusinessConstants.OrderStatus.PAID -> "待交付";
            case BusinessConstants.OrderStatus.IN_PROGRESS -> "进行中";
            case BusinessConstants.OrderStatus.PENDING_RETURN -> "待归还验收";
            case BusinessConstants.OrderStatus.COMPLETED -> "已完成";
            case BusinessConstants.OrderStatus.CANCELLED -> "已取消";
            case BusinessConstants.OrderStatus.DISPUTE -> "纠纷中";
            case BusinessConstants.OrderStatus.REFUNDING -> "退款中";
            default -> "未知";
        };
    }
}
