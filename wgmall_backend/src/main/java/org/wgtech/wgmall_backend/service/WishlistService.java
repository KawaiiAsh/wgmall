package org.wgtech.wgmall_backend.service;

import org.wgtech.wgmall_backend.entity.Wishlist;

import java.util.List;

public interface WishlistService {
    /**
     * 获取用户的心愿单商品列表
     */
    List<Wishlist> getWishlistItems(Long userId);

    /**
     * 添加商品到心愿单
     */
    Wishlist addToWishlist(Long userId, Long productId);

    /**
     * 从心愿单中移除商品（支持批量删除）
     */
    void deleteWishlistItems(Long userId, List<Long> wishlistItemIds);
}
