package org.wgtech.wgmall_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wgtech.wgmall_backend.entity.Product;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.entity.Wishlist;
import org.wgtech.wgmall_backend.repository.WishlistRepository;
import org.wgtech.wgmall_backend.repository.ProductRepository;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.service.WishlistService;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    @Override
    public List<Wishlist> getWishlistItems(Long userId) {
        return List.of();
    }

    @Override
    public Wishlist addToWishlist(Long userId, Long productId) {
        List<Wishlist> currentItems = wishlistRepository.findByUserId(userId);
        if (currentItems.size() >= 5) {
            throw new RuntimeException("心愿单最多只能添加 5 个商品");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        Wishlist item = Wishlist.builder()
                .user(user)
                .product(product)
                .addedTime(new Date())
                .build();

        return wishlistRepository.save(item);    }

    @Override
    public void deleteWishlistItems(Long userId, List<Long> wishlistItemIds) {
        List<Wishlist> toDelete = wishlistRepository.findAllById(wishlistItemIds);
        for (Wishlist item : toDelete) {
            if (!item.getUser().getId().equals(userId)) {
                throw new RuntimeException("权限错误，不能删除他人的心愿单项");
            }
        }
        wishlistRepository.deleteAll(toDelete);
    }
}

