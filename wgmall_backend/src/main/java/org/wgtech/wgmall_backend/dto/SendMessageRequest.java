package org.wgtech.wgmall_backend.dto;

import lombok.Data;

@Data
public class SendMessageRequest {
    private Long senderId;
    private String receiverUsername;
    private String content;
}
