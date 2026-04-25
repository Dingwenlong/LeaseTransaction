-- 创建数据库
CREATE DATABASE IF NOT EXISTS lease_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE lease_db;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `openid` VARCHAR(100) DEFAULT NULL COMMENT '微信OpenID',
    `password` VARCHAR(255) DEFAULT NULL COMMENT '密码哈希',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
    `student_id` VARCHAR(50) DEFAULT NULL COMMENT '学号',
    `department` VARCHAR(100) DEFAULT NULL COMMENT '院系',
    `campus` VARCHAR(50) DEFAULT NULL COMMENT '校区',
    `credit_score` INT DEFAULT 100 COMMENT '信用积分',
    `is_verified` TINYINT DEFAULT 0 COMMENT '是否认证 0否 1是',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0禁用 1正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记 0未删 1已删',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_openid` (`openid`),
    KEY `idx_campus` (`campus`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 后台系统用户表
CREATE TABLE IF NOT EXISTS `system_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '系统用户ID',
    `username` VARCHAR(64) NOT NULL COMMENT '登录用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码哈希',
    `display_name` VARCHAR(64) NOT NULL COMMENT '显示名称',
    `role` VARCHAR(32) NOT NULL COMMENT '角色',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0禁用 1正常',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记 0未删 1已删',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_system_user_username` (`username`),
    KEY `idx_system_user_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台系统用户表';

-- 物品表
CREATE TABLE IF NOT EXISTS `item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '物品ID',
    `user_id` BIGINT NOT NULL COMMENT '发布者ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `description` TEXT COMMENT '描述',
    `images` TEXT COMMENT '图片URL，多个用逗号分隔',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '分类',
    `type` TINYINT NOT NULL COMMENT '类型 1租赁 2出售',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `deposit` DECIMAL(10,2) DEFAULT NULL COMMENT '押金',
    `campus` VARCHAR(50) DEFAULT NULL COMMENT '所在校区',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0下架 1上架 2已租 3已售',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `favorite_count` INT DEFAULT 0 COMMENT '收藏次数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记 0未删 1已删',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category` (`category`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`),
    KEY `idx_campus` (`campus`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物品表';

-- 订单表
CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
    `item_id` BIGINT NOT NULL COMMENT '物品ID',
    `seller_id` BIGINT NOT NULL COMMENT '卖家ID',
    `buyer_id` BIGINT NOT NULL COMMENT '买家ID',
    `type` TINYINT NOT NULL COMMENT '类型 1租赁 2出售',
    `status` TINYINT DEFAULT 1 COMMENT '状态 1待支付 2待发货 3进行中 4待验收 5已完成 6已取消 7纠纷中',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '总金额',
    `deposit` DECIMAL(10,2) DEFAULT NULL COMMENT '押金',
    `rental_price` DECIMAL(10,2) DEFAULT NULL COMMENT '租金',
    `start_date` DATETIME DEFAULT NULL COMMENT '租赁开始时间',
    `end_date` DATETIME DEFAULT NULL COMMENT '租赁结束时间',
    `rental_days` INT DEFAULT NULL COMMENT '租赁天数',
    `delivery_method` VARCHAR(50) DEFAULT NULL COMMENT '交付方式',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记 0未删 1已删',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_item_id` (`item_id`),
    KEY `idx_seller_id` (`seller_id`),
    KEY `idx_buyer_id` (`buyer_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 租赁记录表
CREATE TABLE IF NOT EXISTS `lease_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `item_id` BIGINT NOT NULL COMMENT '物品ID',
    `user_id` BIGINT NOT NULL COMMENT '出租人ID',
    `lessee_id` BIGINT NOT NULL COMMENT '承租人ID',
    `lease_start` DATETIME DEFAULT NULL COMMENT '租赁开始时间',
    `lease_end` DATETIME DEFAULT NULL COMMENT '租赁结束时间',
    `actual_return` DATETIME DEFAULT NULL COMMENT '实际归还时间',
    `is_overdue` TINYINT DEFAULT 0 COMMENT '是否逾期 0否 1是',
    `overdue_fee` DECIMAL(10,2) DEFAULT NULL COMMENT '逾期费用',
    `damage_description` TEXT COMMENT '损坏描述',
    `damage_compensation` DECIMAL(10,2) DEFAULT NULL COMMENT '损坏赔偿',
    `status` TINYINT DEFAULT 1 COMMENT '状态 1进行中 2已归还',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记 0未删 1已删',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_item_id` (`item_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_lessee_id` (`lessee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租赁记录表';

-- 支付流水表
CREATE TABLE IF NOT EXISTS `payment_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '支付ID',
    `payment_no` VARCHAR(64) NOT NULL COMMENT '支付流水号',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `type` TINYINT NOT NULL COMMENT '类型 1支付 2退款 3押金',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `payment_method` TINYINT DEFAULT NULL COMMENT '支付方式 1微信支付',
    `transaction_id` VARCHAR(100) DEFAULT NULL COMMENT '第三方交易号',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0失败 1成功 2处理中',
    `failure_reason` VARCHAR(200) DEFAULT NULL COMMENT '失败原因',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记 0未删 1已删',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_no` (`payment_no`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付流水表';

-- 评价表
CREATE TABLE IF NOT EXISTS `review` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `item_id` BIGINT NOT NULL COMMENT '物品ID',
    `reviewer_id` BIGINT NOT NULL COMMENT '评价人ID',
    `reviewee_id` BIGINT NOT NULL COMMENT '被评价人ID',
    `rating` TINYINT NOT NULL COMMENT '评分 1-5',
    `content` TEXT COMMENT '评价内容',
    `images` TEXT COMMENT '评价图片，多个用逗号分隔',
    `is_anonymous` TINYINT DEFAULT 0 COMMENT '是否匿名 0否 1是',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记 0未删 1已删',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_item_id` (`item_id`),
    KEY `idx_reviewer_id` (`reviewer_id`),
    KEY `idx_reviewee_id` (`reviewee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- 消息表
CREATE TABLE IF NOT EXISTS `message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `conversation_id` BIGINT DEFAULT NULL COMMENT '会话ID',
    `sender_id` BIGINT NOT NULL COMMENT '发送者ID',
    `receiver_id` BIGINT NOT NULL COMMENT '接收者ID',
    `type` TINYINT NOT NULL COMMENT '类型 1文本 2图片 3系统',
    `content` TEXT COMMENT '消息内容',
    `images` TEXT COMMENT '图片，多个用逗号分隔',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读 0否 1是',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记 0未删 1已删',
    PRIMARY KEY (`id`),
    KEY `idx_conversation_id` (`conversation_id`),
    KEY `idx_sender_id` (`sender_id`),
    KEY `idx_receiver_id` (`receiver_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- 信用记录表
CREATE TABLE IF NOT EXISTS `credit_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `type` TINYINT NOT NULL COMMENT '类型 1增加 2减少',
    `score_change` INT NOT NULL COMMENT '积分变动',
    `before_score` INT NOT NULL COMMENT '变动前积分',
    `after_score` INT NOT NULL COMMENT '变动后积分',
    `reason` VARCHAR(200) NOT NULL COMMENT '变动原因',
    `related_order_id` BIGINT DEFAULT NULL COMMENT '关联订单ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记 0未删 1已删',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='信用记录表';
