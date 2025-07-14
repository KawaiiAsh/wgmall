package org.wgtech.wgmall_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.wgtech.wgmall_backend.entity.User;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return Optional<User>
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据手机号查找用户
     * @param phone 手机号
     * @return Optional<User>
     */
    Optional<User> findByPhone(String phone);

    /**
     * 根据邀请码查找用户
     * @param inviteCode 邀请码
     * @return Optional<User>
     */
    Optional<User> findByInviteCode(String inviteCode);

    /**
     * 根据用户名获取其上级用户名
     * @param username 用户名
     * @return Optional<String> 上级用户名
     */
    @Query("SELECT u.superiorUsername FROM User u WHERE u.username = :username")
    Optional<String> findSuperiorUsernameByUsername(@Param("username") String username);

    /**
     * 根据IP地址统计注册用户数
     * @param ip 注册IP地址
     * @return 使用该IP的用户数量
     */
    long countByIp(String ip);

    Page<User> findAllByOrderByRegisterTimeDesc(Pageable pageable);

    int findTotalOrderCountById(Long id);


}
