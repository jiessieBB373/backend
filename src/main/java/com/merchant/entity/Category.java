package com.merchant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品分类实体类
 */
@Data
@TableName("product_category")
public class Category {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 分类名称
     */
    @TableField("name")
    private String name;
    
    /**
     * 分类编码
     */
    @TableField("code")
    private String code;
    
    /**
     * 分类类型：CIGARETTE-香烟，BEVERAGE-饮料，WATER-矿泉水
     */
    @TableField("type")
    private String type;
    
    /**
     * 父分类ID
     */
    @TableField("parent_id")
    private Long parentId;
    
    /**
     * 排序
     */
    @TableField("sort_order")
    private Integer sortOrder;
    
    @TableField("status")
    private Integer status;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
