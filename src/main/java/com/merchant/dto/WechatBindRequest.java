package com.merchant.dto;


import lombok.Data;

@Data
public class WechatBindRequest {
    private String code;
    private String avatarUrl;
}
