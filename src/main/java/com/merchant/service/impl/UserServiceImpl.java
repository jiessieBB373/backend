package com.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merchant.dto.LoginRequest;
import com.merchant.dto.LoginResponse;
import com.merchant.dto.WechatBindResponse;
import com.merchant.entity.User;
import com.merchant.mapper.UserMapper;
import com.merchant.service.CustomerService;
import com.merchant.service.UserService;
import com.merchant.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${wechat.appid}")
    private String wechatAppId;

    @Value("${wechat.secret}")
    private String wechatSecret;

    private final CustomerService customerService;
    
    public UserServiceImpl(PasswordEncoder passwordEncoder, JwtUtil jwtUtil, CustomerService customerService) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.customerService = customerService;
    }
    
    @Override
    public LoginResponse login(LoginRequest request) {
        User user = baseMapper.selectByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        if (user.getStatus() != 1) {
            throw new RuntimeException("账户已被禁用");
        }

        return buildLoginResponse(user);
    }
    
    @Override
    public User getByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }
    
    @Override
    public boolean register(User user) {
        if (baseMapper.countByUsername(user.getUsername()) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        return save(user);
    }
    
    @Override
    public Page<User> getMerchantList(Page<User> page) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserType, "MERCHANT");
        wrapper.orderByDesc(User::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public LoginResponse wechatLogin(String code) {
        String openId = getWechatOpenId(code);
        if (openId == null) {
            throw new RuntimeException("微信登录失败");
        }

        User user = baseMapper.selectByWechatOpenId(openId);
        if (user == null) {
            throw new RuntimeException("该微信账号未绑定");
        }

        if (user.getStatus() != 1) {
            throw new RuntimeException("账户已被禁用");
        }

        return buildLoginResponse(user);
    }

    @Override
    public WechatBindResponse bindWechat(Long userId, String code, String avatarUrl) {
        String openId = getWechatOpenId(code);
        if (openId == null) {
            throw new RuntimeException("微信授权失败");
        }

        User existingUser = baseMapper.selectByWechatOpenId(openId);
        if (existingUser != null && !existingUser.getId().equals(userId)) {
            throw new RuntimeException("该微信账号已被其他用户绑定");
        }

        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setWechatOpenId(openId);
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            user.setAvatarUrl(avatarUrl);
        } else {
            user.setAvatarUrl("https://mmbiz.qpic.cn/mmbiz/icTdbqWNOwNRna42FI242Lcia07jQodd2FJGIYQfG0LAJGFxM4FbnQP6yfMxBgJ0F3YRqJCJ1aPAK2dQagdusBZg/0");
        }
        updateById(user);

        WechatBindResponse response = new WechatBindResponse();
        response.setWechatOpenId(openId);
        response.setAvatarUrl(user.getAvatarUrl());
        return response;
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
       if (user == null) {
               throw new RuntimeException("用户不存在");
           }
       if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
               throw new RuntimeException("原密码错误");
           }
       if (newPassword == null || newPassword.trim().isEmpty()) {
               throw new RuntimeException("新密码不能为空");
           }
       user.setPassword(passwordEncoder.encode(newPassword));
       updateById(user);
    }

    private String getWechatOpenId(String code) {

        // 调用微信接口
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                wechatAppId, wechatSecret, code
        );

        try {
            RestTemplate restTemplate = new RestTemplate();
            String responseStr = restTemplate.getForObject(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> response = objectMapper.readValue(responseStr, Map.class);

            if (response != null && response.containsKey("openid")) {
                return (String) response.get("openid");
            }

            // 错误处理
            if (response != null && response.containsKey("errcode")) {
                Integer errcode = (Integer) response.get("errcode");
                String errmsg = (String) response.get("errmsg");
                throw new RuntimeException("微信接口错误: " + errcode + " - " + errmsg);
            }

            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("调用微信接口失败: " + e.getMessage());
        }
    }

    /**
     * 构建统一的登录响应数据
     * 账号密码登录和微信登录都使用此方法，保证返回数据一致
     */
    private LoginResponse buildLoginResponse(User user) {
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setUserType(user.getUserType());
        response.setShopName(user.getShopName());
        response.setWechatOpenId(user.getWechatOpenId());
        response.setAvatarUrl(user.getAvatarUrl());

        if ("ADMIN".equals(user.getUserType())) {
            response.setPermissions(Arrays.asList("product:manage", "merchant:manage", "order:manage"));
        } else if("MERCHANT".equals(user.getUserType())){
            response.setPermissions(Arrays.asList("product:view", "order:manage", "order:view"));
        } else if("CUSTOMER".equals(user.getUserType())){
            Long merchantId = customerService.getMerchantIdByUserId(user.getId());
            response.setMerchantId(merchantId);
            response.setPermissions(Arrays.asList("product:view", "order:create", "order:view"));
        }

        return response;
    }
}
