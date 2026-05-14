package com.merchant.dto;

import lombok.Data;
import java.util.List;

/**
 * 创建订单请求
 */
@Data
public class CreateOrderRequest {
    
    private List<OrderItemDTO> items;
    private String deliveryAddress;
    private String contactName;
    private String contactPhone;
    private String remark;
    
    @Data
    public static class OrderItemDTO {
        private Long productId;
        private Integer quantity;
    }
}
