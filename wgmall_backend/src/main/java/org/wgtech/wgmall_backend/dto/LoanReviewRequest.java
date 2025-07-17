package org.wgtech.wgmall_backend.dto;

import lombok.Data;

@Data
public class LoanReviewRequest {
    private Long loanId;
    private String decision; // "APPROVE" or "REJECT"
    private String remarks;
}
