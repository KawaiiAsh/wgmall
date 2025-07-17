package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.dto.MarkAsReadRequest;
import org.wgtech.wgmall_backend.dto.SendMessageRequest;
import org.wgtech.wgmall_backend.entity.Message;
import org.wgtech.wgmall_backend.service.MessageService;
import org.wgtech.wgmall_backend.utils.Result;

import java.util.List;

@RestController
@RequestMapping("/message")
@Tag(name = "站内信接口", description = "提供站内信功能")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    @Operation(summary = "发送站内信（只有Sales和Boss可以）")
    public Result<Message> sendMessage(@RequestBody SendMessageRequest request) {
        try {
            Message message = messageService.sendMessage(
                    request.getSenderId(),
                    request.getReceiverUsername(),
                    request.getContent()
            );
            return Result.success(message);
        } catch (Exception e) {
            return Result.failure("发送站内信失败: " + e.getMessage());
        }
    }

    @GetMapping("/inbox")
    @Operation(summary = "查看站内信（给Buyer或者Seller用）")
    public Result<List<Message>> getUserMessages(@RequestParam String receiverUsername) {
        try {
            List<Message> messages = messageService.getMessagesByReceiverUsername(receiverUsername);
            return Result.success(messages);
        } catch (Exception e) {
            return Result.failure("获取站内信失败: " + e.getMessage());
        }
    }

    @PostMapping("/mark-as-read")
    @Operation(summary = "标记站内信为已读（所有人）")
    public Result<Void> markAsRead(@RequestBody MarkAsReadRequest request) {
        try {
            messageService.markAsRead(request.getMessageId());
            return Result.success();
        } catch (Exception e) {
            return Result.failure("标记已读失败: " + e.getMessage());
        }
    }
}
