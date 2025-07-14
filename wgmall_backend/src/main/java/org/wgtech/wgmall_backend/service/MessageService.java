package org.wgtech.wgmall_backend.service;

import org.wgtech.wgmall_backend.entity.Message;

import java.util.List;

public interface MessageService {

    // 发送站内信
    Message sendMessage(Long senderId, String receiverUsername, String content);

    // 获取指定接收者的所有站内信
    List<Message> getMessagesByReceiverUsername(String receiverUsername);

    // 标记站内信为已读
    void markAsRead(Long messageId);
}
