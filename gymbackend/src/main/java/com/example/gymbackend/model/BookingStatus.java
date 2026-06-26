package com.example.gymbackend.model;

public enum BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    /** 用户主动取消（已确认且已付款后），保留记录供后台查看，不占有效预约/收入统计 */
    USER_CANCELLED
}

