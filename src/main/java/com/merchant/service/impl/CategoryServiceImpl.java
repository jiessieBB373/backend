package com.merchant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchant.entity.Category;
import com.merchant.mapper.CategoryMapper;
import com.merchant.service.CategoryService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    
    @Override
    public List<Category> getByType(String type) {
        return baseMapper.selectByType(type);
    }
    
    @Override
    public List<Category> getRootCategories() {
        return baseMapper.selectRootCategories();
    }
}
