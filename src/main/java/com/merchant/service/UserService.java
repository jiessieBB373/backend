package com.merchant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.merchant.dto.LoginRequest;
import com.merchant.dto.LoginResponse;
import com.merchant.entity.User;

public interface UserService extends IService<User> {
    
    LoginResponse login(LoginRequest request);
    
    User getByUsername(String username);
    
    boolean register(User user);
    
    Page<User> getMerchantList(Page<User> page);
}
