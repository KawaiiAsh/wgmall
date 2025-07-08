package org.wgtech.wgmall_backend.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class InviteCodeGenerator {

    @Autowired
    private UserRepository userRepository;

    // 定义字符集（大写字母、小写字母和数字）
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    // 定义生成的邀请码长度为6位
    private static final int INVITE_CODE_LENGTH = 6;

    // 随机数生成器
    private static final Random random = new Random();

    // 生成唯一的邀请码
    public String generateUniqueInviteCode() {
        String inviteCode;
        do {
            inviteCode = generateInviteCode();
        } while (inviteCodeExists(inviteCode));  // 确保邀请码唯一
        return inviteCode;
    }

    // 生成随机邀请码
    private String generateInviteCode() {
        StringBuilder inviteCode = new StringBuilder(INVITE_CODE_LENGTH);
        for (int i = 0; i < INVITE_CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            inviteCode.append(CHARACTERS.charAt(randomIndex));
        }
        return inviteCode.toString();
    }

    // 检查生成的邀请码是否已存在
    private boolean inviteCodeExists(String inviteCode) {
        return userRepository.findByUsername(inviteCode).isPresent();
    }
}
