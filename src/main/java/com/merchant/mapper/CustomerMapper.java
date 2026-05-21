package com.merchant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.merchant.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {

    @Select("SELECT c.*, u.username, u.real_name, u.phone, u.shop_address, u.status " +
            "FROM customer c LEFT JOIN sys_user u ON c.user_id = u.id " +
            "WHERE c.merchant_id = #{merchantId} AND c.deleted = 0 " +
            "ORDER BY u.create_time DESC")
    Page<Customer> selectPageByMerchantId(Page<Customer> page, @Param("merchantId") Long merchantId);

    @Select("SELECT c.*, u.username, u.real_name, u.phone, u.shop_address, u.status " +
            "FROM customer c LEFT JOIN sys_user u ON c.user_id = u.id " +
            "WHERE c.merchant_id = #{merchantId} AND c.deleted = 0 " +
            "ORDER BY u.create_time DESC")
    List<Customer> selectListByMerchantId(@Param("merchantId") Long merchantId);

    @Select("SELECT c.*, u.username, u.real_name, u.phone, u.shop_address, u.status " +
            "FROM customer c LEFT JOIN sys_user u ON c.user_id = u.id " +
            "WHERE c.user_id = #{userId} AND c.deleted = 0")
    Customer selectByUserId(@Param("userId") Long userId);
}
