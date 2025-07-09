package org.wgtech.wgmall_backend.service;

import org.wgtech.wgmall_backend.entity.OrderTask;
import org.wgtech.wgmall_backend.entity.User;

public interface TakeOrderTaskService {

    /**
     * 用户领取一个订单任务（自动判断任务类型：指派 / 预约 / 普通）
     *
     * @param user 当前登录的用户对象
     * @return 分配的 OrderTask 对象（带任务详情）
     * @throws Exception 如果无任务可领或其他异常
     */
    OrderTask takeOrder(User user) throws Exception;

}
