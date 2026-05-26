package com.merchant.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchant.dto.CreateOrderRequest;
import com.merchant.dto.OrderItemVO;
import com.merchant.dto.OrderVO;
import com.merchant.entity.Order;
import com.merchant.entity.OrderItem;
import com.merchant.entity.Product;
import com.merchant.entity.User;
import com.merchant.mapper.OrderItemMapper;
import com.merchant.mapper.OrderMapper;
import com.merchant.mapper.ProductMapper;
import com.merchant.mapper.UserMapper;
import com.merchant.service.CustomerService;
import com.merchant.service.OrderService;
import com.merchant.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;
    private final ProductService productService;
    private final CustomerService customerService;
    
    public OrderServiceImpl(OrderItemMapper orderItemMapper, ProductMapper productMapper, 
                           UserMapper userMapper, ProductService productService, CustomerService customerService) {
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
        this.userMapper = userMapper;
        this.productService = productService;
        this.customerService = customerService;
    }
    
    @Override
    @Transactional
    public Order createOrder(Long userId, String userType, CreateOrderRequest request) {
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        if ("CUSTOMER".equals(userType)) {
            // 客户下单：获取所属商户ID
            Long merchantId = customerService.getMerchantIdByUserId(userId);
            order.setMerchantId(merchantId);  // ← 保存的是商户ID
        } else {
            order.setMerchantId(userId);
        }
        order.setCustomerId(userId);
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setContactName(request.getContactName());
        order.setContactPhone(request.getContactPhone());
        order.setRemark(request.getRemark());
        order.setStatus("PENDING");
        order.setPayStatus("UNPAID");
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();
        
        for (CreateOrderRequest.OrderItemDTO itemDTO : request.getItems()) {
            Product product = productMapper.selectById(itemDTO.getProductId());
            if (product == null) {
                throw new RuntimeException("商品不存在");
            }
            if (product.getStock() < itemDTO.getQuantity()) {
                throw new RuntimeException("商品库存不足: " + product.getName());
            }
            
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductCode(product.getCode());
            item.setSpecification(product.getSpecification());
            item.setUnitPrice(product.getWholesalePrice());
            item.setQuantity(itemDTO.getQuantity());
            item.setSubtotal(product.getWholesalePrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
            
            items.add(item);
            totalAmount = totalAmount.add(item.getSubtotal());
            
            // 扣减库存
            productService.deductStock(product.getId(), itemDTO.getQuantity());
        }
        
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        
        save(order);
        
        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }
        
        return order;
    }
    
    @Override
    public Page<OrderVO> getPage(Page<Order> page) {
        Page<Order> orderPage = page(page);
        return convertToVO(orderPage);
    }
    
    @Override
    public Page<OrderVO> getByMerchantId(Long merchantId, Page<Order> page) {
        Page<Order> orderPage = baseMapper.selectByMerchantId(page, merchantId);
        return convertToVO(orderPage);
    }
    
    @Override
    public OrderVO getOrderDetail(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            return null;
        }
        return convertToVO(order);
    }
    
    @Override
    public boolean updateStatus(Long orderId, String status) {
        Order order = getById(orderId);
        if (order == null) {
            return false;
        }
        order.setStatus(status);
        return updateById(order);
    }
    
    @Override
    @Transactional
    public boolean cancelOrder(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            return false;
        }
        
        // 恢复库存
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productMapper.updateById(product);
            }
        }
        
        order.setStatus("CANCELLED");
        return updateById(order);
    }
    
    @Override
    public List<OrderVO> getRecentOrders(Long merchantId) {
        List<Order> orders = baseMapper.selectRecentByMerchantId(merchantId);
        return orders.stream().map(this::convertToVO).collect(Collectors.toList());
    }
    
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", new Random().nextInt(10000));
        return "WS" + timestamp + random;
    }
    
    private Page<OrderVO> convertToVO(Page<Order> orderPage) {
        List<OrderVO> voList = orderPage.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        Page<OrderVO> voPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }
    
    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setMerchantId(order.getMerchantId());
        
        User merchant = userMapper.selectById(order.getMerchantId());
        if (merchant != null) {
            vo.setMerchantName(merchant.getShopName());
        }
        
        vo.setTotalAmount(order.getTotalAmount());
        vo.setDiscountAmount(order.getDiscountAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setStatus(order.getStatus());
        vo.setStatusName(getStatusName(order.getStatus()));
        vo.setPayStatus(order.getPayStatus());
        vo.setPayStatusName(getPayStatusName(order.getPayStatus()));
        vo.setDeliveryAddress(order.getDeliveryAddress());
        vo.setContactName(order.getContactName());
        vo.setContactPhone(order.getContactPhone());
        vo.setRemark(order.getRemark());
        vo.setCreateTime(order.getCreateTime());
        
        // 查询订单项
        List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
        List<OrderItemVO> itemVOs = items.stream().map(item -> {
            OrderItemVO itemVO = new OrderItemVO();
            itemVO.setId(item.getId());
            itemVO.setProductId(item.getProductId());
            itemVO.setProductName(item.getProductName());
            itemVO.setProductCode(item.getProductCode());
            itemVO.setSpecification(item.getSpecification());
            itemVO.setUnitPrice(item.getUnitPrice());
            itemVO.setQuantity(item.getQuantity());
            itemVO.setSubtotal(item.getSubtotal());
            return itemVO;
        }).collect(Collectors.toList());
        vo.setItems(itemVOs);
        
        return vo;
    }
    
    private String getStatusName(String status) {
        switch (status) {
            case "PENDING": return "待处理";
            case "CONFIRMED": return "已确认";
            case "DELIVERING": return "配送中";
            case "COMPLETED": return "已完成";
            case "CANCELLED": return "已取消";
            default: return status;
        }
    }
    
    private String getPayStatusName(String payStatus) {
        switch (payStatus) {
            case "UNPAID": return "未支付";
            case "PAID": return "已支付";
            default: return payStatus;
        }
    }

    @Override
    public Page<OrderVO> getPageByStatus(Page<Order> page, String status) {
        Page<Order> orderPage = baseMapper.selectByStatus(page, status);
        return convertToVO(orderPage);
    }

    @Override
    public Page<OrderVO> getByMerchantIdAndStatus(Long merchantId, Page<Order> page, String status) {
        Page<Order> orderPage = baseMapper.selectByMerchantIdAndStatus(page, merchantId, status);
        return convertToVO(orderPage);
    }

    @Override
    public Page<OrderVO> getByCustomerId(Long customerId, Page<Order> page) {
        Page<Order> orderPage = baseMapper.selectByCustomerId(page, customerId);
        return convertToVO(orderPage);
    }

    @Override
    public Page<OrderVO> getByCustomerIdAndStatus(Long customerId, Page<Order> page, String status) {
        Page<Order> orderPage = baseMapper.selectByCustomerIdAndStatus(page, customerId, status);
        return convertToVO(orderPage);
    }

    @Override
    public Page<OrderVO> searchOrders(String keyword, Page<Order> page, String status, Long merchantId, Long customerId) {
        Page<Order> orderPage = baseMapper.searchOrders(page, keyword, status, merchantId, customerId);
        return convertToVO(orderPage);
    }
}
