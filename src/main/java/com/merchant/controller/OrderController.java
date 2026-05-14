package com.merchant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.merchant.dto.CreateOrderRequest;
import com.merchant.dto.OrderVO;
import com.merchant.dto.PageQuery;
import com.merchant.dto.Result;
import com.merchant.entity.Order;
import com.merchant.entity.User;
import com.merchant.service.OrderService;
import com.merchant.service.UserService;
import com.merchant.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    
    private final OrderService orderService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    public OrderController(OrderService orderService, UserService userService, JwtUtil jwtUtil) {
        this.orderService = orderService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    
    @GetMapping
    public Result<Page<OrderVO>> list(PageQuery query, HttpServletRequest request) {
        Page<Order> page = new Page<>(query.getPageNum(), query.getPageSize());
        
        // 获取当前用户
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String userType = jwtUtil.getUserTypeFromToken(token);
            
            if ("ADMIN".equals(userType)) {
                // 管理员查看所有订单
                return Result.success(orderService.getPage(page));
            } else {
                // 商户只能看自己的订单
                Long userId = jwtUtil.getUserIdFromToken(token);
                return Result.success(orderService.getByMerchantId(userId, page));
            }
        }
        return Result.error(401, "未登录");
    }
    
    @GetMapping("/{id}")
    public Result<OrderVO> getById(@PathVariable Long id) {
        OrderVO order = orderService.getOrderDetail(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }
    
    @PostMapping
    public Result<Order> create(@RequestBody CreateOrderRequest request, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(token);
            try {
                Order order = orderService.createOrder(userId, request);
                return Result.success("下单成功", order);
            } catch (Exception e) {
                return Result.error(e.getMessage());
            }
        }
        return Result.error(401, "未登录");
    }
    
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam String status) {
        if (orderService.updateStatus(id, status)) {
            return Result.success("状态更新成功", null);
        }
        return Result.error("更新失败");
    }
    
    @PutMapping("/{id}/cancel")
    public Result<String> cancel(@PathVariable Long id) {
        if (orderService.cancelOrder(id)) {
            return Result.success("订单已取消", null);
        }
        return Result.error("取消失败");
    }
    
    @GetMapping("/recent")
    public Result<List<OrderVO>> getRecent(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(token);
            return Result.success(orderService.getRecentOrders(userId));
        }
        return Result.error(401, "未登录");
    }
}
