package com.merchant.dto;

import lombok.Data;
import java.util.List;

/**
 * 登录响应
 */
@Data
public class LoginResponse {
    private String token;
    private String username;
    private String userType;
    private List<String> permissions;

    // 新增
    private String shopName;
    private String wechatOpenId;
    private String avatarUrl;
}
