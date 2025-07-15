package org.wgtech.wgmall_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wgtech.wgmall_backend.entity.Administrator;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.entity.UserRedBad;
import org.wgtech.wgmall_backend.repository.AdministratorRepository;
import org.wgtech.wgmall_backend.repository.TaskLoggerRepository;
import org.wgtech.wgmall_backend.repository.UserRedBadRepository;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.service.UserService;
import org.wgtech.wgmall_backend.utils.InviteCodeGenerator;
import org.wgtech.wgmall_backend.utils.IpLocationDetector;
import org.wgtech.wgmall_backend.utils.Result;

import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private TaskLoggerRepository taskLoggerRepository;

    @Autowired
    private InviteCodeGenerator inviteCodeGenerator;

    @Autowired
    private IpLocationDetector ipLocationDetector;

    @Autowired
    private UserRedBadRepository userRedBadRepository;

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
                    .balance(BigDecimal.ZERO)
                    .noneUsefulBalance(BigDecimal.ZERO)
                    .debtAmount(BigDecimal.ZERO)
                    .toggle(false)
                    .canWithdraw(false)
                    .appointmentStatus(false)
                    .appointmentNumber(null)
                    .country(country)
                    .registerTime(new Date())
                    .lastLoginTime(new Date())
                    .repeatIp(repeatIp)
                    .totalProfit(BigDecimal.ZERO)
                    .buyerOrSaler(0)
                    .rebate(0.006) // 默认返利
                    .redBagDrawCount(0)
                    .redBagCount(0)
                    .build();

            // 6. 保存用户
            User savedUser = userRepository.save(newUser);

            // 添加默认红包配置
            UserRedBad redBad = UserRedBad.builder()
                    .userId(savedUser.getId())
                    .day1(10.0)
                    .day2(20.0)
                    .day3(30.0)
                    .day4(40.0)
                    .day5(50.0)
                    .day6(60.0)
                    .day7(70.0)
                    .build();
            userRedBadRepository.save(redBad);

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

        // 修复：把 double 转换成 BigDecimal
        BigDecimal added = BigDecimal.valueOf(amount);
        user.setBalance(user.getBalance().add(added));
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

        // 修复：转换为 BigDecimal 并判断余额
        BigDecimal subtract = BigDecimal.valueOf(amount);
        if (user.getBalance().compareTo(subtract) < 0) {
            return Result.failure("余额不足");
        }

        user.setBalance(user.getBalance().subtract(subtract));
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
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    @Override
    public Result<User> getUserInfoById(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Result.failure("用户不存在");
        }

        User user = userOpt.get();
        user.setPassword(null); // 避免暴露密码等敏感字段
        return Result.success(user);
    }

    /**
     * 根据用户id设置返点
     * @param userId
     * @param rebate
     */
    public void setRebate(Long userId, double rebate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        user.setRebate(rebate);
        userRepository.save(user);
    }

    @Override
    public BigDecimal getTodayProfit(Long userId) {
        LocalDateTime start = LocalDate.now().atStartOfDay(); // 今日 00:00
        LocalDateTime end = LocalDateTime.now();
        return taskLoggerRepository.calculateProfitBetween(userId, start, end);
    }

    @Override
    public BigDecimal getYesterdayProfit(Long userId) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime start = yesterday.atStartOfDay(); // 昨日 00:00
        LocalDateTime end = yesterday.atTime(LocalTime.MAX); // 昨日 23:59:59.999
        return taskLoggerRepository.calculateProfitBetween(userId, start, end);
    }

    @Override
    public int setBuyerOrSaler(Long userId,int buyerOrSaler) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setBuyerOrSaler(buyerOrSaler);
        userRepository.save(user);
        return buyerOrSaler;
    }

    @Override
    public Page<User> getAllUsersSortedByCreateTime(int page, int size) {
        return userRepository.findAllByOrderByRegisterTimeDesc(PageRequest.of(page, size));
    }

    @Override
    public void setTronAddress(Long userId, String address) {
        User user = getUserOrThrow(userId);
        user.setTronWalletAddress(address);
        userRepository.save(user);
    }

    @Override
    public String getTronAddress(Long userId) {
        return getUserOrThrow(userId).getTronWalletAddress();
    }

    @Override
    public void setBtcAddress(Long userId, String address) {
        User user = getUserOrThrow(userId);
        user.setBitCoinWalletAddress(address);
        userRepository.save(user);
    }

    @Override
    public String getBtcAddress(Long userId) {
        return getUserOrThrow(userId).getBitCoinWalletAddress();
    }

    @Override
    public void setEthAddress(Long userId, String address) {
        User user = getUserOrThrow(userId);
        user.setEthWalletAddress(address);
        userRepository.save(user);
    }

    @Override
    public String getEthAddress(Long userId) {
        return getUserOrThrow(userId).getEthWalletAddress();
    }

    @Override
    public void setCoinAddress(Long userId, String address) {
        User user = getUserOrThrow(userId);
        user.setCoinWalletAddress(address);
        userRepository.save(user);
    }

    @Override
    public String getCoinAddress(Long userId) {
        return getUserOrThrow(userId).getCoinWalletAddress();
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在，ID: " + userId));
    }

    @Override
    public boolean changePasswordByUsername(String username, String newPassword) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPassword(newPassword);
            userRepository.save(user);
            return true;
        }
        return false;
    }



}
