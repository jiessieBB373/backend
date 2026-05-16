package com.merchant.controller;

import com.merchant.dto.*;
import com.merchant.entity.User;
import com.merchant.service.UserService;
import com.merchant.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.login(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        try {
            userService.register(user);
            return Result.success("注册成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            User user = userService.getByUsername(username);
            if (user != null) {
                user.setPassword(null);
                return Result.success(user);
            }
        }
        return Result.error(401, "未登录");
    }

    @PostMapping("/wechat-login")
    public Result<LoginResponse> wechatLogin(@RequestBody WechatLoginRequest request) {
        try {
            LoginResponse response = userService.wechatLogin(request.getCode());
            return Result.success(response);
        } catch (RuntimeException e) {
            if ("该微信账号未绑定".equals(e.getMessage())) {
                return Result.error(404, e.getMessage());
            }
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/bind-wechat")
    public Result<WechatBindResponse> bindWechat(@RequestBody WechatBindRequest request, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error(401, "未登录");
        }
        token = token.substring(7);
        Long userId = jwtUtil.getUserIdFromToken(token);

        try {
            WechatBindResponse response = userService.bindWechat(userId, request.getCode(),request.getAvatarUrl());
            return Result.success("绑定成功", response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
