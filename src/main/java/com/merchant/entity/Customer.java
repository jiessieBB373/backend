package com.merchant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客户实体类
 */
@Data
@TableName("customer")
public class Customer {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 所属商户ID
     */
    @TableField("merchant_id")
    private Long merchantId;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String realName;

    @TableField(exist = false)
    private String phone;

    @TableField(exist = false)
    private String shopAddress;

    @TableField(exist = false)
    private Integer status;
}
