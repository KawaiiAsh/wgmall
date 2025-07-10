package org.wgtech.wgmall_backend.service;

import org.wgtech.wgmall_backend.entity.TaskLogger;

import java.util.Optional;

public interface GrabTaskService {

    /**
     * 检查用户是否有抢单资格（对应 User.toggle 字段）
     * @param userId 用户ID
     * @return true 有资格；false 无资格
     */
    boolean hasGrabPermission(Long userId);

    /**
     * 获取用户剩余抢单次数（对应 User.orderCount 字段）
     * @param userId 用户ID
     * @return 剩余次数，用户不存在则返回 0
     */
    int getRemainingGrabTimes(Long userId);

    /**
     * 检查用户是否存在未完成的订单（如 TaskLogger.completed = false）
     * @param userId 用户ID
     * @return true 有未完成订单；false 没有
     */
    boolean hasComplete(Long userId);

    /**
     * 判断用户是否达到预约派单条件
     * 条件：orderCount == appointmentNumber 且 appointmentStatus 为 true
     * @param userId 用户ID
     * @return true 符合预约派单条件；false 不符合
     */
    boolean shouldTriggerAppointment(Long userId);
}
