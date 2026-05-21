package com.merchant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.merchant.entity.Category;
import java.util.List;

public interface CategoryService extends IService<Category> {
    
    List<Category> getByType(String type);
    
    List<Category> getRootCategories();

    Page<Category> getPage(Page<Category> page);

    Page<Category> getPageByMerchantId(Long merchantId, Page<Category> page);

    List<Category> getListByMerchantId(Long merchantId);

    List<Category> getAllWithMerchantName();

    List<Category> getByMerchantIdWithMerchantName(Long merchantId);
}
