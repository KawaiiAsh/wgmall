package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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
    private Long senderId;

    @NotNull
    private String senderUsername;

    @NotNull
    private String senderNickname;

    @NotNull
    private Long receiverId;

    @NotNull
    private String receiverUsername;

    @NotNull
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;  // 用作发送时间

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
