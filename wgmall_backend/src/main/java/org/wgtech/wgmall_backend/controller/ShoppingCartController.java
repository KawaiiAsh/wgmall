package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.entity.CartItem;
import org.wgtech.wgmall_backend.service.CartService;
import org.wgtech.wgmall_backend.utils.Result;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "购物车接口", description = "用户购物车增删查改相关操作")
public class ShoppingCartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    @Operation(summary = "查询购物车", description = "获取指定用户的购物车内容")
    public Result<List<CartItem>> getCart(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        List<CartItem> items = cartService.getCartItems(userId);
        return Result.success(items);
    }

    @PostMapping("/add")
    @Operation(summary = "加入购物车", description = "一次加入一个商品，如果购物车已满5件则添加失败")
    public Result<CartItem> addToCart(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "商品ID", required = true)
            @RequestParam Long productId) {
        try {
            CartItem item = cartService.addToCart(userId, productId);
            return Result.success(item);
        } catch (Exception e) {
            return Result.failure("加入购物车失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "批量删除购物车项", description = "通过购物车项ID列表删除，支持多选")
    public Result<Void> deleteItems(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @RequestBody List<Long> cartItemIds) {
        try {
            cartService.deleteCartItems(userId, cartItemIds);
            return Result.success();
        } catch (Exception e) {
            return Result.failure("删除失败: " + e.getMessage());
        }
    }

    @PostMapping("/merge/{userId}")
    @Operation(summary = "合并购物车中相同商品", description = "将用户购物车中重复商品合并为一个项，数量合并")
    public Result<Void> mergeCart(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        try {
            cartService.mergeSameProductItems(userId);
            return Result.success();
        } catch (Exception e) {
            return Result.failure("合并失败: " + e.getMessage());
        }
    }

    @PutMapping("/quantity")
    @Operation(summary = "修改购物车商品数量", description = "指定购物车项ID并修改数量")
    public Result<Void> updateQuantity(
            @Parameter(description = "购物车项ID", required = true)
            @RequestParam Long cartItemId,
            @Parameter(description = "商品数量", required = true)
            @RequestParam int quantity) {
        try {
            cartService.updateCartItemQuantity(cartItemId, quantity);
            return Result.success();
        } catch (Exception e) {
            return Result.failure("更新数量失败: " + e.getMessage());
        }
    }
}
