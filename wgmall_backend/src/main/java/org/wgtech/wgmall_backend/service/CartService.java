package org.wgtech.wgmall_backend.service;

import org.wgtech.wgmall_backend.entity.CartItem;

import java.util.List;

public interface CartService {
    List<CartItem> getCartItems(Long userId);
    CartItem addToCart(Long userId, Long productId);
    void deleteCartItems(Long userId, List<Long> cartItemIds);
    void mergeSameProductItems(Long userId);
    void updateCartItemQuantity(Long cartItemId, int newQuantity);
}
