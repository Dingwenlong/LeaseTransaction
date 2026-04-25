package com.campus.lease.config;

import com.campus.lease.common.constant.BusinessConstants;
import com.campus.lease.dto.CreateOrderRequest;
import com.campus.lease.dto.MessageSendRequest;
import com.campus.lease.dto.OrderStatusUpdateRequest;
import com.campus.lease.dto.ReviewSubmitRequest;
import com.campus.lease.entity.Item;
import com.campus.lease.entity.User;
import com.campus.lease.service.ItemService;
import com.campus.lease.service.MessageService;
import com.campus.lease.service.OrderService;
import com.campus.lease.service.ReviewService;
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
    private final ReviewService reviewService;

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

        Item projector = createItem(sellerA.getId(), "便携投影仪", "1080P高清投影，适合宿舍观影和小组展示。", "https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=900&q=80", "电子产品", BusinessConstants.OrderType.LEASE, new BigDecimal("50.00"), new BigDecimal("600.00"), "东校区", BusinessConstants.ItemStatus.ACTIVE);
        createItem(buyerA.getId(), "机械键盘", "青轴手感，RGB背光，游戏办公两用。", "https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?auto=format&fit=crop&w=900&q=80", "电子产品", BusinessConstants.OrderType.SALE, new BigDecimal("199.00"), BigDecimal.ZERO, "西校区", BusinessConstants.ItemStatus.PENDING_REVIEW);
        Item badmintonRacket = createItem(sellerB.getId(), "羽毛球拍套装", "双拍含球，适合初学者和日常锻炼。", "https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?auto=format&fit=crop&w=900&q=80", "运动器材", BusinessConstants.OrderType.LEASE, new BigDecimal("15.00"), new BigDecimal("200.00"), "南校区", BusinessConstants.ItemStatus.ACTIVE);
        Item cetBooks = createItem(sellerA.getId(), "英语四六级真题", "近五年真题及解析，附听力音频下载链接。", "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=900&q=80", "书籍资料", BusinessConstants.OrderType.SALE, new BigDecimal("35.00"), BigDecimal.ZERO, "东校区", BusinessConstants.ItemStatus.ACTIVE);
        createItem(sellerB.getId(), "画板套装", "含画架、画板、颜料，写生必备。", "https://images.unsplash.com/photo-1513364776144-60967b0f800f?auto=format&fit=crop&w=900&q=80", "生活用品", BusinessConstants.OrderType.LEASE, new BigDecimal("20.00"), new BigDecimal("300.00"), "南校区", BusinessConstants.ItemStatus.OFFLINE);
        createItem(buyerA.getId(), "电吉他", "入门级电吉他，附音箱和连接线。", "https://images.unsplash.com/photo-1564186763535-ebb21ef5277f?auto=format&fit=crop&w=900&q=80", "电子产品", BusinessConstants.OrderType.LEASE, new BigDecimal("80.00"), new BigDecimal("1000.00"), "西校区", BusinessConstants.ItemStatus.REJECTED);
        Item printer = createItem(sellerA.getId(), "便携式打印机", "无线连接，支持手机直打，适合打印论文。", "https://images.unsplash.com/photo-1612815154858-60aa4c59eaa6?auto=format&fit=crop&w=900&q=80", "电子产品", BusinessConstants.OrderType.SALE, new BigDecimal("259.00"), BigDecimal.ZERO, "东校区", BusinessConstants.ItemStatus.ACTIVE);
        Item yogaMat = createItem(buyerA.getId(), "瑜伽垫", "加厚防滑，适合瑜伽和健身。", "https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?auto=format&fit=crop&w=900&q=80", "运动器材", BusinessConstants.OrderType.LEASE, new BigDecimal("8.00"), new BigDecimal("50.00"), "西校区", BusinessConstants.ItemStatus.ACTIVE);

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

        Long pendingReturnOrderId = createOrderAndGetId(buyerB.getId(), projector.getId(), BusinessConstants.OrderType.LEASE, 2, "周末小组展示使用");
        updateOrder(pendingReturnOrderId, BusinessConstants.OrderStatus.PAID, "押金已支付");
        updateOrder(pendingReturnOrderId, BusinessConstants.OrderStatus.IN_PROGRESS, "已当面交接投影仪");
        updateOrder(pendingReturnOrderId, BusinessConstants.OrderStatus.PENDING_RETURN, "租赁到期，等待归还验收");

        Long cancelledOrderId = createOrderAndGetId(buyerA.getId(), badmintonRacket.getId(), BusinessConstants.OrderType.LEASE, 3, "周末打球用");
        updateOrder(cancelledOrderId, BusinessConstants.OrderStatus.CANCELLED, "临时有事，取消预约");

        Long cetSaleOrderId = createOrderAndGetId(buyerB.getId(), cetBooks.getId(), BusinessConstants.OrderType.SALE, null, "急需备考资料");
        updateOrder(cetSaleOrderId, BusinessConstants.OrderStatus.PAID, "已付款");
        updateOrder(cetSaleOrderId, BusinessConstants.OrderStatus.COMPLETED, "已确认收货，资料很全");

        Long refundingOrderId = createOrderAndGetId(buyerA.getId(), printer.getId(), BusinessConstants.OrderType.SALE, null, "论文打印用");
        updateOrder(refundingOrderId, BusinessConstants.OrderStatus.PAID, "已付款");
        updateOrder(refundingOrderId, BusinessConstants.OrderStatus.REFUNDING, "打印机有故障，申请退款");

        Long completedLeaseOrderId = createOrderAndGetId(buyerB.getId(), yogaMat.getId(), BusinessConstants.OrderType.LEASE, 5, "健身打卡用");
        updateOrder(completedLeaseOrderId, BusinessConstants.OrderStatus.PAID, "押金已支付");
        updateOrder(completedLeaseOrderId, BusinessConstants.OrderStatus.IN_PROGRESS, "已交接瑜伽垫");
        updateOrder(completedLeaseOrderId, BusinessConstants.OrderStatus.PENDING_RETURN, "租赁到期，等待归还");
        updateOrder(completedLeaseOrderId, BusinessConstants.OrderStatus.COMPLETED, "已验收归还，物品完好");

        submitReview(buyerB.getId(), saleOrderId, 5, "考研资料内容详实，对我帮助很大，卖家包装也很仔细", null, 0);
        submitReview(sellerB.getId(), saleOrderId, 3, "买家收货后沟通较少", null, 1);

        submitReview(buyerB.getId(), cetSaleOrderId, 5, "真题很全，解析详细，备考必备！", null, 0);
        submitReview(sellerA.getId(), cetSaleOrderId, 4, "买家付款及时，沟通顺畅", null, 0);

        submitReview(buyerB.getId(), completedLeaseOrderId, 4, "瑜伽垫质量不错，租借流程很方便", null, 0);
        submitReview(buyerA.getId(), completedLeaseOrderId, 5, "租客很爱护物品，按时归还，推荐！", null, 0);

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

    private void submitReview(Long reviewerId, Long orderId, int rating, String content, String images, int isAnonymous) {
        ReviewSubmitRequest request = new ReviewSubmitRequest();
        request.setOrderId(orderId);
        request.setRating(rating);
        request.setContent(content);
        request.setImages(images);
        request.setIsAnonymous(isAnonymous);
        reviewService.submitReview(reviewerId, request);
    }
}
