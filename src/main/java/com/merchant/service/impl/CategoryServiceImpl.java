package com.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchant.entity.Category;
import com.merchant.mapper.CategoryMapper;
import com.merchant.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @Override
    public List<Category> getByLevel(Long merchantId, Integer level) {
        return baseMapper.selectByLevel(merchantId, level);
    }

    @Override
    public List<Category> getChildrenByParentId(Long parentId) {
        return baseMapper.selectChildrenByParentId(parentId);
    }

    @Override
    public List<Category> getTreeByMerchantId(Long merchantId) {
        // 1. 获取该商户所有分类
        List<Category> allCategories = baseMapper.selectTreeByMerchantId(merchantId);

        // 2. 按ID建立映射，方便查找
        Map<Long, Category> categoryMap = new HashMap<>();
        for (Category cat : allCategories) {
            categoryMap.put(cat.getId(), cat);
        }

        // 3. 构建树形结构
        List<Category> rootCategories = new ArrayList<>();
        for (Category cat : allCategories) {
            if (cat.getParentId() == null) {
                // 大类（根节点）
                cat.setChildren(new ArrayList<>());
                rootCategories.add(cat);
            } else {
                // 小类（子节点），找到父节点并添加
                Category parent = categoryMap.get(cat.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(cat);
                }
            }
        }

        return rootCategories;
    }

    @Override
    public List<Category> getAllTree() {
        // 1. 获取所有分类
        List<Category> allCategories = baseMapper.selectAllWithMerchantName();

        // 2. 按ID建立映射，方便查找
        Map<Long, Category> categoryMap = new HashMap<>();
        for (Category cat : allCategories) {
            categoryMap.put(cat.getId(), cat);
        }

        // 3. 构建树形结构
        List<Category> rootCategories = new ArrayList<>();
        for (Category cat : allCategories) {
            if (cat.getParentId() == null) {
                // 大类（根节点）
                cat.setChildren(new ArrayList<>());
                rootCategories.add(cat);
            } else {
                // 小类（子节点），找到父节点并添加
                Category parent = categoryMap.get(cat.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(cat);
                }
            }
        }
        System.out.println("【DEBUG】返回大类数量: " + rootCategories.size());
        return rootCategories;
    }
}
