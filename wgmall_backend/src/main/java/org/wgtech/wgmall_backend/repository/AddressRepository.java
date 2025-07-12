package org.wgtech.wgmall_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wgtech.wgmall_backend.entity.Address;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByUser_Id(Long userId);
}
