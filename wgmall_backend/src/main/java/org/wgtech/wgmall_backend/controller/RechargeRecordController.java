package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.entity.RechargeRecord;
import org.wgtech.wgmall_backend.service.RechargeRecordService;
import org.wgtech.wgmall_backend.utils.Result;

import java.util.List;

@RestController
@RequestMapping("/recharge-records")
@RequiredArgsConstructor
@Tag(name = "充值记录管理", description = "包含充值记录的增、删、改、查等操作")
public class RechargeRecordController {

    private final RechargeRecordService rechargeRecordService;

    @Operation(summary = "添加一条充值记录", description = "客服可以通过此接口添加一条新的充值记录")
    @PostMapping
    public RechargeRecord addRechargeRecord(
            @Parameter(description = "充值记录对象，包含金额、时间、用户名等信息", required = true)
            @RequestBody RechargeRecord rechargeRecord) {
        return rechargeRecordService.addRechargeRecord(rechargeRecord);
    }

    @Operation(summary = "删除充值记录", description = "客服可以通过此接口删除某个指定的充值记录")
    @DeleteMapping("/{id}")
    public void deleteRechargeRecord(
            @Parameter(description = "充值记录ID", required = true)
            @PathVariable Long id) {
        rechargeRecordService.deleteRechargeRecord(id);
    }

    @Operation(summary = "修改充值记录", description = "客服可以通过此接口修改已存在的充值记录")
    @PutMapping("/{id}")
    public RechargeRecord updateRechargeRecord(
            @Parameter(description = "充值记录ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "新的充值记录对象", required = true)
            @RequestBody RechargeRecord rechargeRecord) {
        return rechargeRecordService.updateRechargeRecord(id, rechargeRecord);
    }

    @Operation(summary = "查询所有充值记录（分页，按 ID 倒序）", description = "获取所有的充值记录，并支持分页查询，按 ID 倒序排列")
    @GetMapping("/all")
    public Result<Object> getAllRechargeRecords(
            @Parameter(description = "当前页码", required = true)
            @RequestParam int page,
            @Parameter(description = "每页记录数", required = true)
            @RequestParam int size) {

        // 调用服务层获取分页结果
        Page<RechargeRecord> pageResult = rechargeRecordService.getAllRechargeRecords(page, size);

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



    @Operation(summary = "根据用户名查询充值记录（分页，返回总数）", description = "通过用户名查询该用户的充值记录，支持分页，并返回总记录数")
    @GetMapping("/username")
    public Result<Object> getRechargeRecordsByUsername(
            @Parameter(description = "用户名", required = true)
            @RequestParam String username,
            @Parameter(description = "当前页码", required = true)
            @RequestParam int page,
            @Parameter(description = "每页记录数", required = true)
            @RequestParam int size) {
        return rechargeRecordService.getRechargeRecordsByUsername(username, page, size);
    }

}
