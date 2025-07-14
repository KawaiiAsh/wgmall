package org.wgtech.wgmall_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wgtech.wgmall_backend.entity.Message;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // 根据接收者的用户名查找所有站内信
    List<Message> findByReceiverUsername(String receiverUsername);
}
