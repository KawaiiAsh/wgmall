package org.wgtech.wgmall_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wgtech.wgmall_backend.entity.OrderTask;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.OrderTaskRepository;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.service.TakeOrderTaskService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
public class TaKeOrderTaskServiceImpl implements TakeOrderTaskService {

    @Autowired
    private OrderTaskRepository orderTaskRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public OrderTask takeOrder(User user) throws Exception {
        // 0. 校验是否允许抢单
        // toggle 为 false 表示未开启抢单权限
        if (!user.isToggle()) {
            throw new Exception("当前账户未开启抢单权限");
        }

        // 1. 检查用户是否还有可刷单次数
        if (user.getOrderCount() <= 0) {
            throw new Exception("可刷单次数不足");
        }

        OrderTask orderTask = null;
        return null;
    }

}
