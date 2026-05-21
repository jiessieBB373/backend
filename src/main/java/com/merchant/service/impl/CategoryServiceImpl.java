package com.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    /**
     * 分页获取分类列表
     */
    public Page<Category> getPage(Page<Category> page) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getStatus, 1);
        wrapper.orderByAsc(Category::getSortOrder);
        return page(page, wrapper);
    }

    /**
     * 根据商户ID获取分类（带商户名称）
     */
    public Page<Category> getPageByMerchantId(Long merchantId, Page<Category> page) {
        if (merchantId == null) {
            // 管理员查看所有，带商户名称
            return baseMapper.selectPageWithMerchantName(page);
        }
        // 商户或客户查看，带商户名称
        return baseMapper.selectPageByMerchantIdWithMerchantName(page, merchantId);
    }

    /**
     * 获取商户的分类列表（不分页，带商户名称）
     */
    public List<Category> getListByMerchantId(Long merchantId) {
        return baseMapper.selectListByMerchantIdWithMerchantName(merchantId);
    }

    @Override
    public List<Category> getAllWithMerchantName() {
        return baseMapper.selectAllWithMerchantName();
    }

    @Override
    public List<Category> getByMerchantIdWithMerchantName(Long merchantId) {
        return baseMapper.selectByMerchantIdWithMerchantName(merchantId);
    }
}
