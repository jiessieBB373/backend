package com.merchant.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 订单项视图对象
 */
@Data
public class OrderItemVO {
    private Long id;
    private Long productId;
    private String productName;
    private String productCode;
    private String specification;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
}
