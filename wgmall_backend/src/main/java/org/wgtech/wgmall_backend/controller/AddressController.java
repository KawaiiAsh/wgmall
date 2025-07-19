package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.entity.Address;
import org.wgtech.wgmall_backend.service.AddressService;
import org.wgtech.wgmall_backend.utils.Result;

@RestController
@RequestMapping("/address")
@Tag(name = "用户地址接口", description = "每个用户仅有一个地址信息")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping("/set")
    @Operation(summary = "更新当前用户地址")
    public Result<Address> setAddress(@RequestParam Long userId, @RequestBody Address address) {
        return Result.success(addressService.setOrUpdateAddress(userId, address));
    }

    @GetMapping("/get")
    @Operation(summary = "获取当前用户地址")
    public Result<Address> getAddress(@RequestParam Long userId) {
        return Result.success(addressService.getAddressByUserId(userId));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除当前用户地址")
    public Result<Void> deleteAddress(@RequestParam Long userId) {
        addressService.deleteAddressByUserId(userId);
        return Result.success();
    }
}
