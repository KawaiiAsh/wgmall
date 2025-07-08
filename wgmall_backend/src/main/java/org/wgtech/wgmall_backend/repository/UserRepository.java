package org.wgtech.wgmall_backend.repository;

import org.springframework.stereotype.Repository;
import org.wgtech.wgmall_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 根据用户名查找用户
    Optional<User> findByUsername(String username);

    // 根据电话查找用户
    Optional<User> findByPhone(String phone);

    // 根据IP查找用户
    Optional<User> findByIp(String ip); // 新增根据IP查找用户的方法

    // 根据邀请码查找用户
    Optional<User> findByInviteCode(String inviteCode); // 根据邀请码查找用户
}
