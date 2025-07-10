package org.wgtech.wgmall_backend.service;

import org.wgtech.wgmall_backend.entity.TaskLogger;

import java.util.Optional;

public interface GrabTaskService {

    /**
     * 检测用户是否有抢单资格（toggle）
     * @param userId 用户ID
     * @return true：有资格；false：无资格（比如抢单已关闭）
     */
    boolean hasGrabPermission(Long userId);

    /**
     * 获取用户剩余抢单次数（orderCount）
     * @param userId 用户ID
     * @return 剩余抢单次数（>= 0），如果用户不存在，返回 0
     */
    int getRemainingGrabTimes(Long userId);

    /**
     * 检测用户是否有未完成订单
     * @param userId
     * @return
     */
    boolean hasComplete(Long userId);

    /**
     * 判断用户是否触发预约派单条件（orderCount == appointmentNumber 且 appointmentStatus 为 true）
     * @param userId 用户ID
     * @return true 表示触发预约派单
     */
    boolean shouldTriggerAppointment(Long userId);

}
