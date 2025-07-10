package org.wgtech.wgmall_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.TaskLoggerRepository;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.service.GrabTaskService;

import java.util.Optional;

@Service
public class GrabTaskServiceImpl implements GrabTaskService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskLoggerRepository taskLoggerRepository;

    // 是否有抢单资格
    @Override
    public boolean hasGrabPermission(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.map(User::isToggle).orElse(false);
    }

    // 抢单数量
    @Override
    public int getRemainingGrabTimes(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.map(User::getOrderCount).orElse(0);
    }

    // 是否有未完成的订单
    @Override
    public boolean hasComplete(Long userId) {
        // true 表示有未完成的记录
        return taskLoggerRepository.existsByUserIdAndCompletedFalse(userId);
    }

    /**
     * 是否触发预约订单
     * @param userId 用户ID
     * @return
     */
    @Override
    public boolean shouldTriggerAppointment(Long userId) {
        return userRepository.findById(userId)
                .filter(User::isAppointmentStatus)
                .map(user -> user.getOrderCount() == user.getAppointmentNumber())
                .orElse(false);
    }

}
