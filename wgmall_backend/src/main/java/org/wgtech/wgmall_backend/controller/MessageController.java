package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.entity.Message;
import org.wgtech.wgmall_backend.service.MessageService;
import org.wgtech.wgmall_backend.utils.Result;

import java.util.List;

@RestController
@RequestMapping("/message")
@Tag(name = "站内信接口", description = "提供站内信功能") // Swagger 标签，分组显示
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 发送站内信
     *
     * @param senderId         发送者ID
     * @param receiverUsername 接收者用户名（用户ID）
     * @param content          站内信内容
     * @return 发送的站内信对象
     */
    @Operation(summary = "发送站内信（只有Sales和Boss可以）")
    @PostMapping("/send")
    public Result<Message> sendMessage(@RequestParam Long senderId,
                                       @RequestParam String receiverUsername,
                                       @RequestParam String content) {
        try {
            Message message = messageService.sendMessage(senderId, receiverUsername, content);
            return Result.success(message);
        } catch (Exception e) {
            return Result.failure("发送站内信失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定接收者的站内信
     *
     * @param receiverUsername 接收者用户名（用户ID）
     * @return 站内信列表
     */
    @GetMapping("/inbox")
    @Operation(summary = "查看站内信（给Buyer或者Seller用用）")
    public Result<List<Message>> getUserMessages(@RequestParam String receiverUsername) {
        try {
            List<Message> messages = messageService.getMessagesByReceiverUsername(receiverUsername);
            return Result.success(messages);
        } catch (Exception e) {
            return Result.failure("获取站内信失败: " + e.getMessage());
        }
    }

    /**
     * 标记站内信为已读
     *
     * @param messageId 站内信ID
     * @return 处理结果
     */
    @PostMapping("/mark-as-read")
    @Operation(summary = "发送站内信（只有Sales和Boss可以）")
    public Result<Void> markAsRead(@RequestParam Long messageId) {
        try {
            messageService.markAsRead(messageId);
            return Result.success();
        } catch (Exception e) {
            return Result.failure("标记已读失败: " + e.getMessage());
        }
    }
}
