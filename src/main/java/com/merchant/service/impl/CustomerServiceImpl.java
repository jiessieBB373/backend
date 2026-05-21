package com.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchant.entity.Customer;
import com.merchant.mapper.CustomerMapper;
import com.merchant.service.CustomerService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {


    @Override
    public Long getMerchantIdByUserId(Long userId) {
        return baseMapper.selectMerchantIdByUserId(userId);
    }
    @Override
    public Page<Customer> getPageByMerchantId(Long merchantId, Page<Customer> page) {
        return baseMapper.selectPageByMerchantId(page, merchantId);
    }

    @Override
    public List<Customer> getListByMerchantId(Long merchantId) {
        return baseMapper.selectListByMerchantId(merchantId);
    }

    @Override
    public Customer getByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }


}
