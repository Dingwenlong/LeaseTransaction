package com.campus.lease.config;

import com.campus.lease.common.constant.BusinessConstants;
import com.campus.lease.dto.CreateOrderRequest;
import com.campus.lease.dto.MessageSendRequest;
import com.campus.lease.dto.OrderStatusUpdateRequest;
import com.campus.lease.entity.Item;
import com.campus.lease.entity.User;
import com.campus.lease.service.ItemService;
import com.campus.lease.service.MessageService;
import com.campus.lease.service.OrderService;
import com.campus.lease.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DemoDataInitializer implements ApplicationRunner {

    private final UserService userService;
    private final ItemService itemService;
    private final OrderService orderService;
    private final MessageService messageService;

    @Value("${demo.data.enabled:true}")
    private boolean demoDataEnabled;

    @Override
    public void run(ApplicationArguments args) {
        if (!demoDataEnabled) {
            return;
        }
        if (userService.count() > 0 || itemService.count() > 0) {
            return;
        }

        User admin = createUser("admin", "平台管理员", "管理中心", "平台运营", 1, 1, 140);
        User sellerA = createUser("20230001", "林同学", "东校区", "信息工程学院", 1, 1, 118);
        User buyerA = createUser("20230002", "陈同学", "西校区", "经济管理学院", 1, 1, 112);
        User sellerB = createUser("20230003", "周同学", "南校区", "设计学院", 1, 1, 106);
        User buyerB = createUser("20230004", "王同学", "东校区", "法学院", 1, 0, 96);

        Item bike = createItem(sellerA.getId(), "山地自行车", "九成新，适合校园骑行和短途出行。", "https://images.unsplash.com/photo-1541625602330-2277a4c46182?auto=format&fit=crop&w=900&q=80", "运动器材", BusinessConstants.OrderType.LEASE, new BigDecimal("28.00"), new BigDecimal("300.00"), "东校区", BusinessConstants.ItemStatus.ACTIVE);
        createItem(sellerB.getId(), "专业计算器", "考试常用型号，待补充细节图。", "https://images.unsplash.com/photo-1587145820266-a5951ee6f620?auto=format&fit=crop&w=900&q=80", "电子产品", BusinessConstants.OrderType.SALE, new BigDecimal("88.00"), BigDecimal.ZERO, "南校区", BusinessConstants.ItemStatus.PENDING_REVIEW);
        Item camera = createItem(sellerA.getId(), "微单相机", "社团活动常用设备，配件齐全，可短租。", "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=900&q=80", "电子产品", BusinessConstants.OrderType.LEASE, new BigDecimal("68.00"), new BigDecimal("800.00"), "东校区", BusinessConstants.ItemStatus.ACTIVE);
        Item books = createItem(sellerB.getId(), "考研资料套装", "数学、英语、政治全套资料，适合冲刺阶段。", "https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=900&q=80", "书籍资料", BusinessConstants.OrderType.SALE, new BigDecimal("128.00"), BigDecimal.ZERO, "南校区", BusinessConstants.ItemStatus.ACTIVE);
        Item tent = createItem(sellerA.getId(), "双人露营帐篷", "适合周末露营，防水防风。", "https://images.unsplash.com/photo-1504280390367-361c6d9f38f4?auto=format&fit=crop&w=900&q=80", "生活用品", BusinessConstants.OrderType.LEASE, new BigDecimal("45.00"), new BigDecimal("500.00"), "东校区", BusinessConstants.ItemStatus.ACTIVE);

        Long leaseOrderId = createOrderAndGetId(buyerA.getId(), bike.getId(), BusinessConstants.OrderType.LEASE, 3, "下课后交接");
        updateOrder(leaseOrderId, BusinessConstants.OrderStatus.PAID, "押金已冻结");
        updateOrder(leaseOrderId, BusinessConstants.OrderStatus.IN_PROGRESS, "已完成当面交接");

        Long saleOrderId = createOrderAndGetId(buyerB.getId(), books.getId(), BusinessConstants.OrderType.SALE, null, "图书馆门口自提");
        updateOrder(saleOrderId, BusinessConstants.OrderStatus.PAID, "买家已完成支付");
        updateOrder(saleOrderId, BusinessConstants.OrderStatus.COMPLETED, "已确认收货");

        Long pendingLeaseOrderId = createOrderAndGetId(buyerB.getId(), tent.getId(), BusinessConstants.OrderType.LEASE, 2, "周五晚交接");
        updateOrder(pendingLeaseOrderId, BusinessConstants.OrderStatus.PENDING_PAYMENT, "等待买家付款");

        Long disputeOrderId = createOrderAndGetId(buyerA.getId(), camera.getId(), BusinessConstants.OrderType.LEASE, 1, "活动拍摄借用");
        updateOrder(disputeOrderId, BusinessConstants.OrderStatus.PAID, "押金已支付");
        updateOrder(disputeOrderId, BusinessConstants.OrderStatus.DISPUTE, "归还后发现轻微划痕，进入仲裁");

        sendMessage(buyerA.getId(), sellerA.getId(), "你好，我会在今天 18:30 到东门取车。");
        sendMessage(sellerA.getId(), buyerA.getId(), "收到，我会提前检查刹车和车锁。");
        messageService.sendSystemMessage(admin.getId(), "演示数据已完成初始化", "当前环境已预置租赁、交易、审核和仲裁场景。");

        log.info("演示数据初始化完成");
    }

    private User createUser(String studentId, String nickname, String campus, String department, int status, int verified, int creditScore) {
        User user = userService.getOrCreateUserByUsername(studentId, nickname, "");
        user.setNickname(nickname);
        user.setStudentId(studentId);
        user.setCampus(campus);
        user.setDepartment(department);
        user.setStatus(status);
        user.setIsVerified(verified);
        user.setCreditScore(creditScore);
        userService.updateById(user);
        return userService.getById(user.getId());
    }

    private Item createItem(Long userId, String title, String description, String images, String category, Integer type, BigDecimal price, BigDecimal deposit, String campus, Integer status) {
        Item item = new Item();
        item.setUserId(userId);
        item.setTitle(title);
        item.setDescription(description);
        item.setImages(images);
        item.setCategory(category);
        item.setType(type);
        item.setPrice(price);
        item.setDeposit(deposit);
        item.setCampus(campus);
        item.setStatus(status);
        item.setViewCount(20);
        item.setFavoriteCount(6);
        itemService.save(item);
        return itemService.getById(item.getId());
    }

    private Long createOrderAndGetId(Long buyerId, Long itemId, Integer type, Integer rentalDays, String remark) {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setItemId(itemId);
        request.setType(type);
        request.setRentalDays(rentalDays);
        request.setStartDate(LocalDateTime.now().plusDays(1));
        request.setEndDate(rentalDays == null ? null : LocalDateTime.now().plusDays(1 + rentalDays));
        request.setDeliveryMethod("校内面交");
        request.setRemark(remark);
        Map<String, Object> order = orderService.createOrder(buyerId, request);
        return Long.valueOf(order.get("id").toString());
    }

    private void updateOrder(Long orderId, Integer status, String remark) {
        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest();
        request.setStatus(status);
        request.setRemark(remark);
        orderService.updateOrderStatus(0L, orderId, request);
    }

    private void sendMessage(Long senderId, Long receiverId, String content) {
        MessageSendRequest request = new MessageSendRequest();
        request.setReceiverId(receiverId);
        request.setType(BusinessConstants.MessageType.TEXT);
        request.setContent(content);
        messageService.sendMessage(senderId, request);
    }
}
