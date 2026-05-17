package com.merchant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.merchant.entity.Cart;

import java.util.List;

public interface CartService extends IService<Cart> {

    /**
     * 获取用户购物车列表
     */
    List<Cart> getByUserId(Long userId);

    /**
     * 添加商品到购物车
     */
    Cart addToCart(Long userId, Long productId, Integer quantity);

    /**
     * 更新购物车商品数量
     */
    boolean updateQuantity(Long cartId, Long userId, Integer quantity);

    /**
     * 删除购物车商品
     */
    boolean removeCartItem(Long cartId, Long userId);

    /**
     * 清空用户购物车
     */
    boolean clearCart(Long userId);

    /**
     * 获取购物车商品数量
     */
    int getCartCount(Long userId);

    /**
     * 删除购物车中已下单的商品
     */
    boolean removeByProductIds(Long userId, List<Long> productIds);
}
