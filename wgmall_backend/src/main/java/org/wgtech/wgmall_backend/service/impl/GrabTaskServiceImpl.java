package org.wgtech.wgmall_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.TaskLoggerRepository;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.service.GrabTaskService;

import java.util.Optional;

/**
 * 抢单服务实现类
 * 提供抢单资格判断、次数查询、未完成订单判断等功能
 */
@Service
public class GrabTaskServiceImpl implements GrabTaskService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskLoggerRepository taskLoggerRepository;

    /**
     * 判断用户是否具有抢单资格（如：toggle 状态是否为 true）
     *
     * @param userId 用户ID
     * @return true 有资格，false 没资格
     */
    @Override
    public boolean hasGrabPermission(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.map(User::isToggle).orElse(false);
    }

    /**
     * 获取用户当前剩余的抢单次数
     *
     * @param userId 用户ID
     * @return 剩余次数（没有用户时为0）
     */
    @Override
    public int getRemainingGrabTimes(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.map(User::getOrderCount).orElse(0);
    }

    /**
     * 判断用户是否存在未完成的任务
     *
     * @param userId 用户ID
     * @return true 表示存在未完成任务
     */
    @Override
    public boolean hasComplete(Long userId) {
        return taskLoggerRepository.existsByUserIdAndCompletedFalse(userId);
    }

    /**
     * 判断是否应该触发“预约订单”逻辑
     *
     * 逻辑：
     * - 用户处于预约状态
     * - 当前抢单数已等于预约数量
     *
     * @param userId 用户ID
     * @return true 表示符合预约派单触发条件
     */
    @Override
    public boolean shouldTriggerAppointment(Long userId) {
        return userRepository.findById(userId)
                .filter(User::isAppointmentStatus)
                .map(user -> user.getOrderCount() == user.getAppointmentNumber())
                .orElse(false);
    }
}
