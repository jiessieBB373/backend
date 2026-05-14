package com.merchant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单明细实体类
 */
@Data
@TableName("order_item")
public class OrderItem {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单ID
     */
    @TableField("order_id")
    private Long orderId;
    
    /**
     * 商品ID
     */
    @TableField("product_id")
    private Long productId;
    
    /**
     * 商品名称
     */
    @TableField("product_name")
    private String productName;
    
    /**
     * 商品编码
     */
    @TableField("product_code")
    private String productCode;
    
    /**
     * 规格
     */
    @TableField("specification")
    private String specification;
    
    /**
     * 单价
     */
    @TableField("unit_price")
    private BigDecimal unitPrice;
    
    /**
     * 数量
     */
    @TableField("quantity")
    private Integer quantity;
    
    /**
     * 小计金额
     */
    @TableField("subtotal")
    private BigDecimal subtotal;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
