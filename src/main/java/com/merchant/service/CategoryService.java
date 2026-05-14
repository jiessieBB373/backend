package com.merchant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.merchant.entity.Category;
import java.util.List;

public interface CategoryService extends IService<Category> {
    
    List<Category> getByType(String type);
    
    List<Category> getRootCategories();
}
