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

    // 根据id设置等级
    Result<User> setLevel(Long userId, int level);

}
