package org.wgtech.wgmall_backend.service;

import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.utils.Result;

public interface UserService {

    // 用户注册
    Result<User> registerUser(String username, String phone, String password, String inviteCode, String fundPassword,String ip);

    // 用户登录
    Result<User> loginUser(String username, String password);

    // 根据id加钱
    Result<User> addMoney(Long userId, double amount);

    // 根据id减钱
    Result<User> minusMoney(Long userId, double amount);

    // 设置用户抢单资格（true: 有资格，false: 无资格）
    Result<User> setGrabOrderEligibility(Long userId, boolean eligible);

    // 设置用户抢单次数
    Result<User> setGrabOrderTimes(Long userId, int times);

    // 查询用户信息
    Result<User> getUserById(Long userId);

}
