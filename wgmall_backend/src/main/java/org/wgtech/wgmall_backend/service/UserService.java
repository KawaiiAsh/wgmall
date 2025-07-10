package org.wgtech.wgmall_backend.service;

import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.utils.Result;

public interface UserService {

    /**
     * 用户注册
     * @param username 用户名
     * @param phone 手机号
     * @param password 登录密码
     * @param inviteCode 邀请码
     * @param fundPassword 资金密码
     * @param ip 注册IP地址
     * @return 注册结果
     */
    Result<User> registerUser(String username, String phone, String password, String inviteCode, String fundPassword, String ip);

    /**
     * 用户登录
     * @param username 用户名或手机号
     * @param password 登录密码
     * @return 登录结果
     */
    Result<User> loginUser(String username, String password);

    /**
     * 增加用户余额
     * @param userId 用户ID
     * @param amount 金额
     * @return 操作结果
     */
    Result<User> addMoney(Long userId, double amount);

    /**
     * 扣除用户余额
     * @param userId 用户ID
     * @param amount 金额
     * @return 操作结果
     */
    Result<User> minusMoney(Long userId, double amount);

    /**
     * 设置用户抢单开关（是否允许抢单）
     * @param userId 用户ID
     * @param eligible 是否允许
     * @return 操作结果
     */
    Result<User> setGrabOrderEligibility(Long userId, boolean eligible);

    /**
     * 设置用户抢单次数
     * @param userId 用户ID
     * @param times 次数
     * @return 操作结果
     */
    Result<User> setGrabOrderTimes(Long userId, int times);

    /**
     * 根据ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    Result<User> getUserById(Long userId);
}
