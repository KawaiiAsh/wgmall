package org.wgtech.wgmall_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wgtech.wgmall_backend.entity.UserRedBad;

import java.util.Optional;

@Repository
public interface UserRedBadRepository extends JpaRepository<UserRedBad, Long> {
    Optional<UserRedBad> findByUserId(Long userId);
}
