package com.merchant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("cart")
public class Cart {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long productId;

    private String productName;

    private String productImage;

    private BigDecimal price;

    private String unit;

    private Integer quantity;

    private Integer minQuantity;

    private Integer stock;

    /**
     * 所属商户ID
     */
    @TableField("merchant_id")
    private Long merchantId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
