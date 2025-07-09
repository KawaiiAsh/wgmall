package org.wgtech.wgmall_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wgtech.wgmall_backend.entity.OrderTask;
import org.wgtech.wgmall_backend.entity.OrderTask.OrderStatus;
import org.wgtech.wgmall_backend.entity.OrderTask.OrderType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderTaskRepository extends JpaRepository<OrderTask, Long> {

}
