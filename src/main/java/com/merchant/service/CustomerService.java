package com.merchant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.merchant.entity.Customer;

import java.util.List;

public interface CustomerService extends IService<Customer> {

    /**
     * 根据用户ID获取商户ID
     */
    Long getMerchantIdByUserId(Long userId);

    Page<Customer> getPageByMerchantId(Long merchantId, Page<Customer> page);

    List<Customer> getListByMerchantId(Long merchantId);

    Customer getByUserId(Long userId);
}
