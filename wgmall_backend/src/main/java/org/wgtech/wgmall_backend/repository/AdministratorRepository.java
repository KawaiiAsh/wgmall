package org.wgtech.wgmall_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wgtech.wgmall_backend.entity.Administrator;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long> {

    /**
     * 根据用户名查找管理员
     * @param username 管理员用户名
     * @return Optional<Administrator>
     */
    Optional<Administrator> findByUsername(String username);

    /**
     * 根据邀请码查找管理员
     * @param inviteCode 管理员的邀请码
     * @return Optional<Administrator>
     */
    Optional<Administrator> findByInviteCode(String inviteCode);

    /**
     * 根据管理员角色查找（如业务员等）
     * @param role 枚举类型角色
     * @return 对应角色的管理员列表
     */
    List<Administrator> findByRole(Administrator.Role role);

    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);


    // 查询所有业务员，按 ID 倒序，分页
    Page<Administrator> findByRoleOrderByIdDesc(Administrator.Role role, Pageable pageable);

    // 根据 nickname 模糊匹配业务员，按 ID 倒序，分页
    Page<Administrator> findByRoleAndNicknameContainingIgnoreCaseOrderByIdDesc(
            Administrator.Role role, String keyword, Pageable pageable);

    Page<Administrator> findAllByRole(String role, Pageable pageable);

}
