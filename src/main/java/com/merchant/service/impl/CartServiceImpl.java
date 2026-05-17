package com.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchant.entity.Cart;
import com.merchant.entity.Product;
import com.merchant.mapper.CartMapper;
import com.merchant.mapper.ProductMapper;
import com.merchant.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Cart> getByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public Cart addToCart(Long userId, Long productId, Integer quantity) {
        // 查询商品信息
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() != 1) {
            throw new RuntimeException("商品不存在或已下架");
        }

        // 检查是否已在购物车中
        Cart existCart = baseMapper.selectByUserIdAndProductId(userId, productId);
        if (existCart != null) {
            // 已存在，更新数量
            existCart.setQuantity(existCart.getQuantity() + quantity);
            existCart.setPrice(product.getWholesalePrice());
            existCart.setStock(product.getStock());
            updateById(existCart);
            return existCart;
        }

        // 新增购物车项
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setProductId(productId);
        cart.setProductName(product.getName());
        cart.setProductImage(product.getImageUrl());
        cart.setPrice(product.getWholesalePrice());
        cart.setUnit(product.getUnit());
        cart.setQuantity(quantity);
        cart.setMinQuantity(product.getMinQuantity());
        cart.setStock(product.getStock());
        save(cart);
        return cart;
    }

    @Override
    public boolean updateQuantity(Long cartId, Long userId, Integer quantity) {
        Cart cart = getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new RuntimeException("购物车项不存在");
        }
        cart.setQuantity(quantity);
        return updateById(cart);
    }

    @Override
    public boolean removeCartItem(Long cartId, Long userId) {
        Cart cart = getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new RuntimeException("购物车项不存在");
        }
        return removeById(cartId);
    }

    @Override
    public boolean clearCart(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        return remove(wrapper);
    }

    @Override
    public int getCartCount(Long userId) {
        return baseMapper.countByUserId(userId);
    }

    @Override
    public boolean removeByProductIds(Long userId, List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return true;
        }
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        wrapper.in(Cart::getProductId, productIds);
        return remove(wrapper);
    }
}
