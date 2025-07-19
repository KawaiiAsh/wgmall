package org.wgtech.wgmall_backend.dto;

import org.wgtech.wgmall_backend.entity.RechargeRecord;

import java.util.List;

public class RechargeRecordResponse {

    private List<RechargeRecord> records;
    private long total;

    // 构造函数
    public RechargeRecordResponse(List<RechargeRecord> records, long total) {
        this.records = records;
        this.total = total;
    }

    // Getter 和 Setter
    public List<RechargeRecord> getRecords() {
        return records;
    }

    public void setRecords(List<RechargeRecord> records) {
        this.records = records;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
