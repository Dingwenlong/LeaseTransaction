package com.campus.lease.common.constant;

public final class BusinessConstants {

    private BusinessConstants() {
    }

    public static final class ItemStatus {
        public static final int PENDING_REVIEW = 0;
        public static final int ACTIVE = 1;
        public static final int LEASING = 2;
        public static final int SOLD = 3;
        public static final int OFFLINE = 4;
        public static final int REJECTED = 5;

        private ItemStatus() {
        }
    }

    public static final class OrderType {
        public static final int LEASE = 1;
        public static final int SALE = 2;

        private OrderType() {
        }
    }

    public static final class OrderStatus {
        public static final int PENDING_PAYMENT = 1;
        public static final int PAID = 2;
        public static final int IN_PROGRESS = 3;
        public static final int PENDING_RETURN = 4;
        public static final int COMPLETED = 5;
        public static final int CANCELLED = 6;
        public static final int DISPUTE = 7;
        public static final int REFUNDING = 8;

        private OrderStatus() {
        }
    }

    public static final class MessageType {
        public static final int TEXT = 1;
        public static final int IMAGE = 2;
        public static final int SYSTEM = 3;

        private MessageType() {
        }
    }

    public static final class PaymentType {
        public static final int PAYMENT = 1;
        public static final int REFUND = 2;
        public static final int DEPOSIT = 3;

        private PaymentType() {
        }
    }

    public static final class PaymentStatus {
        public static final int FAILED = 0;
        public static final int SUCCESS = 1;
        public static final int PROCESSING = 2;

        private PaymentStatus() {
        }
    }

    public static final class Credit {
        public static final int DEFAULT_SCORE = 100;
        public static final int MIN_SCORE = 0;
        public static final int MAX_SCORE = 200;

        public static final String REAL_NAME = "REAL_NAME";
        public static final String SUCCESSFUL_TRANSACTION = "SUCCESSFUL_TRANSACTION";
        public static final String GOOD_REVIEW = "GOOD_REVIEW";
        public static final String BAD_REVIEW = "BAD_REVIEW";
        public static final String BREACH = "BREACH";
        public static final String COMPLAINT_CONFIRMED = "COMPLAINT_CONFIRMED";

        private Credit() {
        }
    }
}
