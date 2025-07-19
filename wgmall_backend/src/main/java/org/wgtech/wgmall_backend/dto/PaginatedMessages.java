package org.wgtech.wgmall_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wgtech.wgmall_backend.entity.Message;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedMessages {
    private List<Message> messages;
    private long total;


}
