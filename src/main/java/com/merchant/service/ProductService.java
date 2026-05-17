package com.merchant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.merchant.entity.Product;
import java.util.List;

public interface ProductService extends IService<Product> {
    
    Page<Product> getPage(Page<Product> page);
    
    Page<Product> search(String keyword, Page<Product> page);
    
    List<Product> getByCategoryId(Long categoryId);
    
    boolean deductStock(Long productId, Integer quantity);

    Page<Product> getByCategoryIdPage(Long categoryId, Page<Product> page);

    Page<Product> searchByCategory(String keyword, Long categoryId, Page<Product> page);
}
