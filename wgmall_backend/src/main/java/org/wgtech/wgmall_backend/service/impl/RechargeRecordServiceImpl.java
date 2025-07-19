package org.wgtech.wgmall_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.wgtech.wgmall_backend.entity.RechargeRecord;
import org.wgtech.wgmall_backend.dto.RechargeRecordResponse;
import org.wgtech.wgmall_backend.repository.RechargeRecordRepository;
import org.wgtech.wgmall_backend.service.RechargeRecordService;
import org.wgtech.wgmall_backend.utils.Result;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RechargeRecordServiceImpl implements RechargeRecordService {

    private final RechargeRecordRepository rechargeRecordRepository;

    @Override
    public RechargeRecord addRechargeRecord(RechargeRecord rechargeRecord) {
        // 设置充值时间为当前时间
        if (rechargeRecord.getRechargeTime() == null) {
            rechargeRecord.setRechargeTime(LocalDateTime.now());
        }
        return rechargeRecordRepository.save(rechargeRecord);
    }

    @Override
    public void deleteRechargeRecord(Long id) {
        rechargeRecordRepository.deleteById(id);
    }

    @Override
    public RechargeRecord updateRechargeRecord(Long id, RechargeRecord rechargeRecord) {
        return rechargeRecordRepository.findById(id)
                .map(existingRecord -> {
                    // 只更新 amount 和 username，保留原来的 rechargeTime
                    if (rechargeRecord.getAmount() != null) {
                        existingRecord.setAmount(rechargeRecord.getAmount());
                    }
                    if (rechargeRecord.getUsername() != null) {
                        existingRecord.setUsername(rechargeRecord.getUsername());
                    }

                    // 保证 rechargeTime 不被修改，保留现有的值
                    if (existingRecord.getRechargeTime() != null) {
                        existingRecord.setRechargeTime(existingRecord.getRechargeTime());
                    }

                    return rechargeRecordRepository.save(existingRecord);
                })
                .orElseThrow(() -> new RuntimeException("充值记录未找到"));
    }



    @Override
    public Page<RechargeRecord> getAllRechargeRecords(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));
        return rechargeRecordRepository.findAll(pageable); // 返回 Page 对象
    }


    @Override
    public Result<Object> getRechargeRecordsByUsername(String username, int page, int size) {
        // 按照 ID 降序排列，并进行分页查询
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

        // 查询该用户名的充值记录，按 ID 倒序排列
        Page<RechargeRecord> pageResult = rechargeRecordRepository.findByUsername(username, pageable);

        // 构造分页信息
        Result.Pagination pagination = new Result.Pagination(
                page, // 当前页
                pageResult.getTotalPages(), // 总页数
                pageResult.getTotalElements(), // 总记录数
                size // 每页大小
        );

        // 返回分页结果，使用 Result 封装数据
        return Result.success(pageResult.getContent(), pagination);  // 使用 Result.success 返回分页数据
    }


    // 可以定义一个 PaginatedResponse 类来封装分页结果
    public static class PaginatedResponse<T> {
        private List<T> content;
        private long totalCount;

        public PaginatedResponse(List<T> content, long totalCount) {
            this.content = content;
            this.totalCount = totalCount;
        }

        // Getters and Setters
    }
}
