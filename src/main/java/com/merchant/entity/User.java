package com.merchant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类（管理员/商户）
 */
@Data
@TableName("sys_user")
public class User {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("username")
    private String username;
    
    @TableField("password")
    private String password;
    
    @TableField("real_name")
    private String realName;
    
    @TableField("phone")
    private String phone;
    
    @TableField("email")
    private String email;
    
    /**
     * 用户类型：ADMIN-管理员，MERCHANT-商户
     */
    @TableField("user_type")
    private String userType;
    
    /**
     * 店铺名称（商户必填）
     */
    @TableField("shop_name")
    private String shopName;
    
    /**
     * 店铺地址
     */
    @TableField("shop_address")
    private String shopAddress;
    
    /**
     * 营业执照号
     */
    @TableField("business_license")
    private String businessLicense;
    
    @TableField("status")
    private Integer status;

    /**
     * 微信OpenID
     */
    @TableField("wechat_open_id")
    private String wechatOpenId;

    /**
     * 微信头像URL
     */
    @TableField("avatar_url")
    private String avatarUrl;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
