package com.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchant.entity.Product;
import com.merchant.mapper.ProductMapper;
import com.merchant.service.ProductService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    
    @Override
    public Page<Product> getPage(Page<Product> page) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);
        wrapper.orderByDesc(Product::getCreateTime);
        return page(page, wrapper);
    }
    
    @Override
    public Page<Product> search(String keyword, Page<Product> page) {
        return baseMapper.searchByKeyword(page, keyword);
    }
    
    @Override
    public List<Product> getByCategoryId(Long categoryId) {
        return baseMapper.selectByCategoryId(categoryId);
    }
    
    @Override
    public boolean deductStock(Long productId, Integer quantity) {
        return baseMapper.deductStock(productId, quantity) > 0;
    }

    @Override
    public Page<Product> getByCategoryIdPage(Long categoryId, Page<Product> page) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getCategoryId, categoryId);
        wrapper.eq(Product::getStatus, 1);
        wrapper.orderByDesc(Product::getCreateTime);
        return page(page, wrapper);
    }
}
