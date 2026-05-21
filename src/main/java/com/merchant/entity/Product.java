package com.merchant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 */
@Data
@TableName("product")
public class Product {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 商品名称
     */
    @TableField("name")
    private String name;
    
    /**
     * 商品编码
     */
    @TableField("code")
    private String code;
    
    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;
    
    /**
     * 品牌
     */
    @TableField("brand")
    private String brand;
    
    /**
     * 规格（如：20支/包，500ml/瓶）
     */
    @TableField("specification")
    private String specification;
    
    /**
     * 单位（包、瓶、箱）
     */
    @TableField("unit")
    private String unit;
    
    /**
     * 零售价
     */
    @TableField("retail_price")
    private BigDecimal retailPrice;
    
    /**
     * 批发价
     */
    @TableField("wholesale_price")
    private BigDecimal wholesalePrice;
    
    /**
     * 起批数量
     */
    @TableField("min_quantity")
    private Integer minQuantity;
    
    /**
     * 库存数量
     */
    @TableField("stock")
    private Integer stock;
    
    /**
     * 商品图片
     */
    @TableField("image_url")
    private String imageUrl;
    
    /**
     * 商品描述
     */
    @TableField("description")
    private String description;
    
    @TableField("status")
    private Integer status;

    /**
     * 所属商户ID（管理员查看所有为null）
     */
    @TableField("merchant_id")
    private Long merchantId;

    @TableField(exist = false)
    private String merchantName;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
