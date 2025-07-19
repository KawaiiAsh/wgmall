package org.wgtech.wgmall_backend.service;

import org.springframework.data.domain.Page;
import org.wgtech.wgmall_backend.dto.PaginatedMessages;
import org.wgtech.wgmall_backend.entity.Message;

import java.util.List;

public interface MessageService {

    // 发送站内信
    Message sendMessage(Long senderId, String senderUsername, String senderNickname, Long receiverUserId, String receiverUsername, String content);

    // 获取指定接收者的所有站内信
    List<Message> getMessagesByReceiverUsername(String receiverUsername);

    PaginatedMessages getMessagesByReceiverId(Long receiverId, int page, int size);

    Page<Message> getAllMessages(int page, int size);

    void updateHasUnreadMessages(Long receiverId);

    Message updateMessageContent(Long messageId, String newContent);

    void deleteMessage(Long messageId);
}
