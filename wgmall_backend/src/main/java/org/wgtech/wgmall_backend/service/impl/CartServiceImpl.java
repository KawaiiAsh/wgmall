package org.wgtech.wgmall_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wgtech.wgmall_backend.entity.CartItem;
import org.wgtech.wgmall_backend.entity.Product;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.CartItemRepository;
import org.wgtech.wgmall_backend.repository.ProductRepository;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.service.CartService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public List<CartItem> getCartItems(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }

    @Override
    public CartItem addToCart(Long userId, Long productId) {
        List<CartItem> currentItems = cartItemRepository.findByUserId(userId);
        if (currentItems.size() >= 5) {
            throw new RuntimeException("购物车最多只能有5项商品");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        CartItem item = CartItem.builder()
                .user(user)
                .product(product)
                .quantity(1)
                .addedTime(new Date())
                .build();

        return cartItemRepository.save(item);
    }

    @Override
    public void deleteCartItems(Long userId, List<Long> cartItemIds) {
        List<CartItem> toDelete = cartItemRepository.findAllById(cartItemIds);
        for (CartItem item : toDelete) {
            if (!item.getUser().getId().equals(userId)) {
                throw new RuntimeException("权限错误，不能删除他人购物车项");
            }
        }
        cartItemRepository.deleteAll(toDelete);
    }

    @Override
    public void mergeSameProductItems(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        Map<Long, CartItem> mergedMap = new HashMap<>();

        for (CartItem item : items) {
            Long productId = item.getProduct().getId();
            if (mergedMap.containsKey(productId)) {
                CartItem existing = mergedMap.get(productId);
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                cartItemRepository.delete(item); // 删除重复项
            } else {
                mergedMap.put(productId, item);
            }
        }

        for (CartItem item : mergedMap.values()) {
            cartItemRepository.save(item); // 更新数量
        }
    }

    @Override
    public void updateCartItemQuantity(Long cartItemId, int newQuantity) {
        if (newQuantity <= 0) throw new RuntimeException("数量必须大于0");
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("购物车项不存在"));
        item.setQuantity(newQuantity);
        cartItemRepository.save(item);
    }
}

