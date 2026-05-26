package com.merchant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.merchant.dto.CreateOrderRequest;
import com.merchant.dto.OrderVO;
import com.merchant.entity.Order;
import java.util.List;

public interface OrderService extends IService<Order> {
    
    Order createOrder(Long merchantId, String userType, CreateOrderRequest request);
    
    Page<OrderVO> getPage(Page<Order> page);

    Page<OrderVO> getPageByStatus(Page<Order> page, String status);
    
    Page<OrderVO> getByMerchantId(Long merchantId, Page<Order> page);

    Page<OrderVO> getByMerchantIdAndStatus(Long merchantId, Page<Order> page, String status);
    
    OrderVO getOrderDetail(Long orderId);
    
    boolean updateStatus(Long orderId, String status);
    
    boolean cancelOrder(Long orderId);
    
    List<OrderVO> getRecentOrders(Long merchantId);

    Page<OrderVO> getByCustomerId(Long customerId, Page<Order> page);

    Page<OrderVO> getByCustomerIdAndStatus(Long customerId, Page<Order> page, String status);

    Page<OrderVO> searchOrders(String keyword, Page<Order> page, String status, Long merchantId, Long customerId);
}
