package org.wgtech.wgmall_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wgtech.wgmall_backend.entity.Administrator;

import javax.management.relation.Role;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    // 根据用户名查找业务员
    Optional<Administrator> findByUsername(String username);

    // 根据邀请码查找业务员
    Optional<Administrator> findByInviteCode(String inviteCode); // 根据邀请码查找管理员

    // 根据身份查找
    List<Administrator> findByRole(Administrator.Role role);


}
