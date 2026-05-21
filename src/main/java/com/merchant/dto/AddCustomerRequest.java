package com.merchant.dto;

import lombok.Data;

@Data
public class AddCustomerRequest {
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String address;
    private Long merchantId;
}
