package com.merchant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.merchant.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    @Select("SELECT * FROM orders WHERE order_no = #{orderNo} AND deleted = 0")
    Order selectByOrderNo(String orderNo);
    
    @Select("SELECT * FROM orders WHERE merchant_id = #{merchantId} AND deleted = 0 ORDER BY create_time DESC")
    Page<Order> selectByMerchantId(Page<Order> page, @Param("merchantId") Long merchantId);
    
    @Select("SELECT * FROM orders WHERE status = #{status} AND deleted = 0 ORDER BY create_time DESC")
    Page<Order> selectByStatus(Page<Order> page, @Param("status") String status);
    
    @Select("SELECT COUNT(*) FROM orders WHERE status = #{status} AND deleted = 0")
    Long countByStatus(@Param("status") String status);
    
    @Select("SELECT * FROM orders WHERE merchant_id = #{merchantId} AND deleted = 0 ORDER BY create_time DESC LIMIT 10")
    List<Order> selectRecentByMerchantId(@Param("merchantId") Long merchantId);
}
