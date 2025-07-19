package org.wgtech.wgmall_backend.dto;

import lombok.Data;

@Data
public class SendMessageRequest {
    private Long receiverUserId;  // 接收者的 ID
    private String receiverUsername;  // 接收者的用户名
    private String content;       // 消息内容
    private String senderNickname;

}
