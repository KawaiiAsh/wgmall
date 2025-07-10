package org.wgtech.wgmall_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wgtech.wgmall_backend.entity.Administrator;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.AdministratorRepository;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.service.UserService;
import org.wgtech.wgmall_backend.utils.InviteCodeGenerator;
import org.wgtech.wgmall_backend.utils.IpLocationDetector;
import org.wgtech.wgmall_backend.utils.Result;

import java.util.Date;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private InviteCodeGenerator inviteCodeGenerator;

    @Autowired
    private IpLocationDetector ipLocationDetector;

    /**
     * 用户注册
     */
    @Override
    @Transactional
    public Result<User> registerUser(String username, String phone, String password, String inviteCode, String fundPassword, String ip) {
        try {
            // 1. 检查用户名是否存在
            if (userRepository.findByUsername(username).isPresent()) {
                return Result.failure("用户名已存在");
            }

            // 2. 检查手机号是否已注册
            if (userRepository.findByPhone(phone).isPresent()) {
                return Result.failure("手机号已被注册");
            }

            // 3. 校验邀请码，并查找上级用户名（可以是管理员或用户）
            String superiorUsername = null;
            if (inviteCode != null && !inviteCode.isEmpty()) {
                Optional<Administrator> adminOpt = administratorRepository.findByInviteCode(inviteCode);
                if (adminOpt.isPresent()) {
                    superiorUsername = adminOpt.get().getNickname();
                } else {
                    Optional<User> userOpt = userRepository.findByInviteCode(inviteCode);
                    if (userOpt.isPresent()) {
                        superiorUsername = userOpt.get().getUsername();
                    } else {
                        return Result.failure("无效的邀请码");
                    }
                }
            }

            // 4. 检测 IP 归属地和是否重复注册
            String country = ipLocationDetector.getCountryByIp(ip);
            int repeatIp = userRepository.countByIp(ip) > 0 ? 1 : 0;

            // 5. 构造新用户对象
            User newUser = User.builder()
                    .username(username)
                    .nickname(username)
                    .phone(phone)
                    .password(password)
                    .inviteCode(inviteCodeGenerator.generateUniqueInviteCode()) // 生成邀请码
                    .fundPassword(String.valueOf(fundPassword))
                    .superiorUsername(superiorUsername)
                    .ip(ip)
                    .orderCount(0)
                    .isBanned(false)
                    .balance(0.0)
                    .toggle(false)
                    .canWithdraw(false)
                    .assignedStatus(false)
                    .appointmentStatus(false)
                    .appointmentNumber(null)
                    .country(country)
                    .registerTime(new Date())
                    .lastLoginTime(new Date())
                    .repeatIp(repeatIp)
                    .rebate(0.006) // 默认返利
                    .build();

            // 6. 保存用户
            User savedUser = userRepository.save(newUser);
            return Result.success(savedUser);

        } catch (Exception e) {
            return Result.failure("注册失败，系统错误");
        }
    }

    /**
     * 用户登录（支持用户名或手机号）
     */
    @Override
    public Result<User> loginUser(String usernameOrPhone, String password) {
        try {
            // 1. 查询用户（用户名或手机号）
            Optional<User> userOptional = userRepository.findByUsername(usernameOrPhone);
            if (!userOptional.isPresent()) {
                userOptional = userRepository.findByPhone(usernameOrPhone);
            }

            // 2. 校验用户是否存在
            if (!userOptional.isPresent()) {
                return Result.failure("用户名或手机号不存在");
            }

            User user = userOptional.get();

            // 3. 校验密码
            if (!user.getPassword().equals(password)) {
                return Result.failure("密码错误");
            }

            // 4. 校验是否被封禁
            if (user.isBanned()) {
                return Result.failure("用户已被封禁");
            }

            // 5. 更新登录时间
            user.setLastLoginTime(new Date());
            userRepository.save(user);

            return Result.success(user);
        } catch (Exception e) {
            return Result.failure("登录失败，系统错误");
        }
    }

    /**
     * 增加用户余额
     */
    @Override
    public Result<User> addMoney(Long userId, double amount) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return Result.failure("用户ID不存在");
        }

        User user = optionalUser.get();
        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);
        return Result.success(user);
    }

    /**
     * 扣除用户余额
     */
    @Override
    public Result<User> minusMoney(Long userId, double amount) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return Result.failure("用户ID不存在");
        }

        User user = optionalUser.get();
        user.setBalance(user.getBalance() - amount);
        userRepository.save(user);
        return Result.success(user);
    }

    /**
     * 设置用户是否允许抢单
     */
    @Override
    public Result<User> setGrabOrderEligibility(Long userId, boolean eligible) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return Result.failure("用户不存在");
        }

        User user = optionalUser.get();
        user.setToggle(eligible);
        userRepository.save(user);
        return Result.success(user);
    }

    /**
     * 设置用户抢单次数
     */
    @Override
    public Result<User> setGrabOrderTimes(Long userId, int times) {
        if (times < 0) {
            return Result.failure("抢单次数不能为负");
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return Result.failure("用户不存在");
        }

        User user = optionalUser.get();
        user.setOrderCount(times);
        userRepository.save(user);
        return Result.success(user);
    }

    /**
     * 根据 ID 查询用户信息
     */
    @Override
    public Result<User> getUserById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.map(Result::success).orElseGet(() -> Result.failure("用户不存在"));
    }
}
