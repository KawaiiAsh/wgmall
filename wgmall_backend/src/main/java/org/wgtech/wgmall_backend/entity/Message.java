package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long senderId;  // 发送者ID

    @NotNull
    private String receiverUsername;  // 接收者username

    @NotNull
    private String content;  // 站内信内容

    private boolean isRead;  // 是否已读

    @NotNull
    private Date sendTime;  // 发送时间
}
