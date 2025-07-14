package org.wgtech.wgmall_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wgtech.wgmall_backend.entity.Message;
import org.wgtech.wgmall_backend.repository.MessageRepository;
import org.wgtech.wgmall_backend.service.MessageService;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public Message sendMessage(Long senderId, String receiverUsername, String content) {
        // 创建新的站内信并保存到数据库
        Message message = Message.builder()
                .senderId(senderId)
                .receiverUsername(receiverUsername)
                .content(content)
                .isRead(false)
                .sendTime(new java.util.Date())  // 发送时间设置为当前时间
                .build();
        return messageRepository.save(message);
    }

    @Override
    public List<Message> getMessagesByReceiverUsername(String receiverUsername) {
        // 根据接收者用户名获取所有站内信
        return messageRepository.findByReceiverUsername(receiverUsername);
    }

    @Override
    public void markAsRead(Long messageId) {
        // 查找消息并将其标记为已读
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new IllegalArgumentException("消息未找到"));
        message.setRead(true);
        messageRepository.save(message);  // 更新消息状态
    }
}
