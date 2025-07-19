package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.dto.PaginatedMessages;
import org.wgtech.wgmall_backend.dto.SendMessageRequest;
import org.wgtech.wgmall_backend.entity.Message;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.AdministratorRepository;
import org.wgtech.wgmall_backend.repository.MessageRepository;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.service.MessageService;
import org.wgtech.wgmall_backend.utils.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/message")
@Tag(name = "站内信接口", description = "提供站内信功能")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdministratorRepository administratorRepository;

    @PostMapping("/send")
    public Result<Message> sendMessage(@RequestBody SendMessageRequest request) {
        try {
            // 获取当前登录用户信息（发送者）
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName(); // 获取当前用户名

            // 从 administrator 表中根据当前用户名查找发送者信息
            org.wgtech.wgmall_backend.entity.Administrator currentAdministrator = administratorRepository.findByUsername(currentUsername)
                    .orElseThrow(() -> new IllegalArgumentException("当前管理员未找到"));

            Long currentAdminId = currentAdministrator.getId(); // 获取发送者的 ID
            String senderNickname = currentAdministrator.getNickname(); // 获取发送者的昵称

            // 获取接收者的信息
            Long receiverUserId = request.getReceiverUserId();
            String receiverUsername = request.getReceiverUsername();
            String content = request.getContent();

            // 从 user 表中根据接收者的用户名查找接收者信息
            org.wgtech.wgmall_backend.entity.User receiverUser = userRepository.findByUsername(receiverUsername)
                    .orElseThrow(() -> new IllegalArgumentException("接收者未找到"));

            // 确保接收者 ID 与接收者用户名一致
            if (receiverUserId == null || !receiverUser.getId().equals(receiverUserId)) {
                throw new IllegalArgumentException("接收者信息不一致");
            }

            // 调用服务层发送站内信，并传递 senderNickname
            Message message = messageService.sendMessage(currentAdminId, currentUsername, senderNickname, receiverUserId, receiverUsername, content);

            return Result.success(message);
        } catch (Exception e) {
            return Result.failure("发送站内信失败: " + e.getMessage());
        }
    }



    @GetMapping("/inbox")
    @Operation(summary = "查看站内信（分页）")
    public Result<Map<String, Object>> getUserMessages(
            @RequestParam Long receiverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            // 获取分页的消息
            PaginatedMessages paginatedMessages = messageService.getMessagesByReceiverId(receiverId, page, size);

            // 将用户的未读消息状态设置为 false，表示已读
            User receiverUser = userRepository.findById(receiverId)
                    .orElseThrow(() -> new IllegalArgumentException("接收者未找到"));
            receiverUser.setHasUnreadMessages(false);  // 标记为已读
            userRepository.save(receiverUser);  // 保存更新后的用户

            // 准备响应数据
            Map<String, Object> response = new HashMap<>();
            response.put("messages", paginatedMessages.getMessages());
            response.put("total", paginatedMessages.getTotal());

            return Result.success(response);
        } catch (Exception e) {
            return Result.failure("获取站内信失败: " + e.getMessage());
        }
    }



    @GetMapping("/all")
    @Operation(summary = "查询所有站内信（分页，给后台用）")
    public Result<Page<Message>> getAllMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Page<Message> messages = messageService.getAllMessages(page, size);
            return Result.success(messages);
        } catch (Exception e) {
            return Result.failure("获取所有站内信失败: " + e.getMessage());
        }
    }


    @PutMapping("/update/{messageId}")
    @Operation(summary = "修改站内信内容")
    public Result<Message> updateMessageContent(
            @PathVariable Long messageId,
            @RequestBody String newContent  // 直接接收纯文本
    ) {
        try {
            // 如果内容以双引号开始和结束，去除它们
            if (newContent.startsWith("\"") && newContent.endsWith("\"")) {
                newContent = newContent.substring(1, newContent.length() - 1);  // 去除首尾的双引号
            }

            // 确保更新后的消息内容没有额外的引号
            Message updatedMessage = messageService.updateMessageContent(messageId, newContent);
            return Result.success(updatedMessage);
        } catch (Exception e) {
            return Result.failure("修改站内信内容失败: " + e.getMessage());
        }
    }


    // 2. 删除站内信
    @DeleteMapping("/delete/{messageId}")
    @Operation(summary = "删除站内信")
    public Result<Void> deleteMessage(@PathVariable Long messageId) {
        try {
            // 调用服务层删除站内信
            messageService.deleteMessage(messageId);
            return Result.success(null);
        } catch (Exception e) {
            return Result.failure("删除站内信失败: " + e.getMessage());
        }
    }

}
