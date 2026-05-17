package com.merchant.controller;

import com.merchant.dto.Result;
import com.merchant.entity.Cart;
import com.merchant.service.CartService;
import com.merchant.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取当前用户购物车列表
     */
    @GetMapping
    public Result<List<Cart>> list(HttpServletRequest request) {
        Long userId = getUserId(request);
        List<Cart> cartList = cartService.getByUserId(userId);
        return Result.success(cartList);
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping
    public Result<Cart> add(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = getUserId(request);
        Long productId = Long.valueOf(params.get("productId").toString());
        Integer quantity = Integer.valueOf(params.get("quantity").toString());
        Cart cart = cartService.addToCart(userId, productId, quantity);
        return Result.success("已加入购物车", cart);
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/{id}")
    public Result<Void> updateQuantity(@PathVariable Long id, @RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = getUserId(request);
        Integer quantity = Integer.valueOf(params.get("quantity").toString());
        cartService.updateQuantity(id, userId, quantity);
        return Result.success();
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        cartService.removeCartItem(id, userId);
        return Result.success();
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public Result<Void> clear(HttpServletRequest request) {
        Long userId = getUserId(request);
        cartService.clearCart(userId);
        return Result.success();
    }

    /**
     * 获取购物车商品数量
     */
    @GetMapping("/count")
    public Result<Integer> count(HttpServletRequest request) {
        Long userId = getUserId(request);
        int count = cartService.getCartCount(userId);
        return Result.success(count);
    }

    /**
     * 删除购物车中已下单的商品
     */
    @PostMapping("/removeOrdered")
    public Result<Void> removeOrdered(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = getUserId(request);
        @SuppressWarnings("unchecked")
        List<Long> productIds = ((List<Number>) params.get("productIds"))
                .stream().map(Number::longValue).toList();
        cartService.removeByProductIds(userId, productIds);
        return Result.success();
    }

    private Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        throw new RuntimeException("未登录");
    }
}
