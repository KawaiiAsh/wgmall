package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.entity.Wishlist;
import org.wgtech.wgmall_backend.service.WishlistService;
import org.wgtech.wgmall_backend.utils.Result;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
@Tag(name = "心愿单接口", description = "用户心愿单增删查改相关操作")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping("/{userId}")
    @Operation(summary = "查询心愿单（用户）", description = "获取指定用户的心愿单内容")
    public Result<List<Wishlist>> getCart(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        List<Wishlist> items = wishlistService.getWishlistItems(userId);
        return Result.success(items);
    }

    @PostMapping("/add")
    @Operation(summary = "加入心愿单（用户）", description = "一次加入一个商品，如果心愿单已满5件则添加失败")
    public Result<Wishlist> addToCart(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "商品ID", required = true)
            @RequestParam Long productId) {
        try {
            Wishlist item = wishlistService.addToWishlist(userId, productId);
            return Result.success(item);
        } catch (Exception e) {
            return Result.failure("加入心愿单失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "批量删除心愿单项（用户）", description = "通过心愿单项ID列表删除，支持多选")
    public Result<Void> deleteItems(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @RequestBody List<Long> cartItemIds) {
        try {
            wishlistService.deleteWishlistItems(userId, cartItemIds);
            return Result.success();
        } catch (Exception e) {
            return Result.failure("删除失败: " + e.getMessage());
        }
    }

}
