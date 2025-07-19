package org.wgtech.wgmall_backend.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.wgtech.wgmall_backend.dto.PaginatedMessages;
import org.wgtech.wgmall_backend.entity.Message;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.MessageRepository;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.service.MessageService;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional  // 确保在同一个事务内执行
    public Message sendMessage(Long senderId, String senderUsername, String senderNickname, Long receiverUserId, String receiverUsername, String content) {

        // 创建新的站内信并保存到数据库
        Message message = Message.builder()
                .senderId(senderId)              // 发送者 ID 从请求参数传递过来
                .senderUsername(senderUsername)  // 发送者用户名从请求参数传递过来
                .senderNickname(senderNickname)  // 发送者昵称从请求参数传递过来
                .receiverId(receiverUserId)      // 接收者 ID 从请求参数传递过来
                .receiverUsername(receiverUsername) // 接收者用户名从请求参数传递过来
                .content(content)                 // 消息内容从请求参数传递过来
                .createdAt(LocalDateTime.now())   // 当前时间作为创建时间
                .build();

        // 保存站内信到数据库
        Message savedMessage = messageRepository.save(message);

        // 更新接收者的未读消息状态
        User receiverUser = userRepository.findById(receiverUserId)
                .orElseThrow(() -> new IllegalArgumentException("接收者未找到"));
        receiverUser.setHasUnreadMessages(true);  // 标记该用户有未读消息
        userRepository.save(receiverUser);  // 保存更新后的用户

        // 返回保存后的消息
        return savedMessage;
    }

    @Override
    public List<Message> getMessagesByReceiverUsername(String receiverUsername) {
        // 根据接收者用户名获取所有站内信
        return messageRepository.findByReceiverUsername(receiverUsername);
    }

    @Override
    public PaginatedMessages getMessagesByReceiverId(Long receiverId, int page, int size) {
        // 获取该用户的分页消息，并按 createdAt 时间倒序排序
        Page<Message> messagePage = messageRepository.findByReceiverId(
                receiverId,
                PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")))  // 按时间倒序排序
        );

        // 获取消息列表
        List<Message> messages = messagePage.getContent();

        // 获取总消息数
        long total = messageRepository.countByReceiverId(receiverId);

        // 返回分页结果
        return new PaginatedMessages(messages, total);
    }


    @Override
    public Page<Message> getAllMessages(int page, int size) {
        // 按 createdAt 字段倒序排序，最新的消息排在最前面
        return messageRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt"))));
    }


    @Override
    public void updateHasUnreadMessages(Long receiverId) {

        // 获取用户并更新其未读消息状态
        User user = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("用户未找到"));
        user.setHasUnreadMessages(true);

        // 保存更新后的用户
        userRepository.save(user);
    }

    // 更新站内信内容
    @Override
    @Transactional
    public Message updateMessageContent(Long messageId, String newContent) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("站内信未找到"));

        message.setContent(newContent); // 更新内容
        return messageRepository.save(message); // 保存更新后的消息
    }

    // 删除站内信
    @Override
    @Transactional
    public void deleteMessage(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("站内信未找到"));

        messageRepository.delete(message); // 删除消息
    }
}
