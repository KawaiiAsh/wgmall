package org.wgtech.wgmall_backend.service;

import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.utils.Result;

public interface UserService {

    // 用户注册
    Result<User> registerUser(String username, String phone, String password, String inviteCode, double fundPassword,String ip);

    // 用户登录
    Result<User> loginUser(String username, String password);

    // 根据用户名加钱
    Result<User> addMoney(String username, double amount);

    // 根据用户名减钱
    Result<User> minusMoney(String username, double amount);

    // 根据用户名设置等级
    Result<User> setLevel(String username, int level);
}
