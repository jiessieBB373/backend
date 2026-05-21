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

    /**
     * 根据层级获取分类
     * @param merchantId 商户ID
     * @param level 层级：1=大类，2=小类
     */
    List<Category> getByLevel(Long merchantId, Integer level);

    /**
     * 获取子分类（小类）
     * @param parentId 父分类ID
     */
    List<Category> getChildrenByParentId(Long parentId);

    /**
     * 获取树形分类结构（包含大类和小类）
     * @param merchantId 商户ID
     */
    List<Category> getTreeByMerchantId(Long merchantId);

    /**
     * 获取所有分类的树形结构（管理员用）
     */
    List<Category> getAllTree();
}
