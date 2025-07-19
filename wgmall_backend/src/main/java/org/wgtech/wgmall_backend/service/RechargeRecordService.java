package org.wgtech.wgmall_backend.service;

import org.springframework.data.domain.Page;
import org.wgtech.wgmall_backend.entity.RechargeRecord;
import org.wgtech.wgmall_backend.utils.Result;

import java.util.List;

public interface RechargeRecordService {

    /**
     * 添加充值记录
     * @param rechargeRecord 充值记录对象
     * @return 新添加的充值记录
     */
    RechargeRecord addRechargeRecord(RechargeRecord rechargeRecord);

    /**
     * 删除充值记录
     * @param id 充值记录的ID
     */
    void deleteRechargeRecord(Long id);

    /**
     * 修改充值记录
     * @param id 充值记录ID
     * @param rechargeRecord 修改后的充值记录对象
     * @return 修改后的充值记录
     */
    RechargeRecord updateRechargeRecord(Long id, RechargeRecord rechargeRecord);

    /**
     * 获取所有充值记录（分页）
     * @param page 当前页码
     * @param size 每页大小
     * @return 充值记录列表
     */
    Page<RechargeRecord> getAllRechargeRecords(int page, int size);
    /**
     * 根据用户名查询充值记录（分页，返回总数）
     * @param username 用户名
     * @param page 当前页码
     * @param size 每页大小
     * @return 返回查询到的充值记录及总数
     */
    Result<Object> getRechargeRecordsByUsername(String username, int page, int size);
}
