package org.wgtech.wgmall_backend.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // 根据邀请码查找用户
    Optional<User> findByInviteCode(String inviteCode); // 根据邀请码查找用户

    // 根据username找上级的username
    @Query("SELECT u.superiorUsername FROM User u WHERE u.username = :username")
    Optional<String> findSuperiorUsernameByUsername(@Param("username") String username);

    // 检测ip是否重复
    long countByIp(String ip);
}
