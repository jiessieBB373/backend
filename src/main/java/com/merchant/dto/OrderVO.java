package com.merchant.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单视图对象
 */
@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private Long merchantId;
    private String merchantName;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal payAmount;
    private String status;
    private String statusName;
    private String payStatus;
    private String payStatusName;
    private String deliveryAddress;
    private String contactName;
    private String contactPhone;
    private String remark;
    private LocalDateTime createTime;
    private List<OrderItemVO> items;
}
