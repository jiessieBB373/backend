package com.merchant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@TableName("orders")
public class Order {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单编号
     */
    @TableField("order_no")
    private String orderNo;
    
    /**
     * 商户ID
     */
    @TableField("merchant_id")
    private Long merchantId;

    /**
     * 下单客户ID
     */
    @TableField("customer_id")
    private Long customerId;

    /**
     * 客户名称（冗余字段，便于查询）
     */
    @TableField("customer_name")
    private String customerName;
    
    /**
     * 订单总金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;
    
    /**
     * 优惠金额
     */
    @TableField("discount_amount")
    private BigDecimal discountAmount;
    
    /**
     * 实付金额
     */
    @TableField("pay_amount")
    private BigDecimal payAmount;
    
    /**
     * 订单状态：PENDING-待处理，CONFIRMED-已确认，DELIVERING-配送中，COMPLETED-已完成，CANCELLED-已取消
     */
    @TableField("status")
    private String status;
    
    /**
     * 支付状态：UNPAID-未支付，PAID-已支付
     */
    @TableField("pay_status")
    private String payStatus;
    
    /**
     * 配送地址
     */
    @TableField("delivery_address")
    private String deliveryAddress;
    
    /**
     * 联系人
     */
    @TableField("contact_name")
    private String contactName;
    
    /**
     * 联系电话
     */
    @TableField("contact_phone")
    private String contactPhone;
    
    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
    
    /**
     * 下单时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
