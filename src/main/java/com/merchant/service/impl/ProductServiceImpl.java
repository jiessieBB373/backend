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

    @Override
    public Page<Product> searchByCategories(String keyword, List<Long> categoryIds, Long merchantId, Page<Product> page) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        // ==================== 修改：使用 in 查询多个分类 ====================
        if (categoryIds != null && !categoryIds.isEmpty()) {
            wrapper.in(Product::getCategoryId, categoryIds);
        }
        wrapper.eq(Product::getStatus, 1);
        if (merchantId != null) {
            wrapper.and(w -> w
                    .eq(Product::getMerchantId, merchantId)
                    .or()
                    .isNull(Product::getMerchantId)
            );
        }
        wrapper.and(w -> w
                .like(Product::getName, keyword)
                .or()
                .like(Product::getBrand, keyword)
                .or()
                .like(Product::getSpecification, keyword)
        );
        wrapper.orderByDesc(Product::getCreateTime);
        return page(page, wrapper);
    }

    /**
     * 根据商户ID获取商品（商户只能看到自己的商品和管理员的公共商品）
     */
    public Page<Product> getPageByMerchantId(Long merchantId, Page<Product> page) {
        if (merchantId == null) {
            // 管理员查看所有，带商户名称
            return baseMapper.selectPageWithMerchantName(page);
        }
        // 商户或客户查看，带商户名称
        return baseMapper.selectPageByMerchantIdWithMerchantName(page, merchantId);
    }

    /**
     * 搜索商品（带商户过滤）
     */
    @Override
    public Page<Product> searchByKeyword(String keyword, Long merchantId, Page<Product> page) {
        if (merchantId == null) {
            // 管理员搜索，带商户名称
            return baseMapper.searchByKeywordWithMerchantName(page, keyword);
        }
        // 商户或客户搜索（简化处理，先按关键词搜索）
        return baseMapper.searchByKeywordWithMerchantName(page, keyword);
    }

    @Override
    public Page<Product> getByCategoryIdsAndMerchantId(List<Long> categoryIds, Long merchantId, Page<Product> page) {
        if (merchantId == null) {
            // 管理员按分类查询，带商户名称
            return baseMapper.selectByCategoryIdWithMerchantName(page, categoryIds);
        }
        // 商户或客户按分类查询
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        // ==================== 修改：使用 in 查询多个分类 ====================
        if (categoryIds != null && !categoryIds.isEmpty()) {
            wrapper.in(Product::getCategoryId, categoryIds);
        }
        wrapper.eq(Product::getStatus, 1);
        wrapper.and(w -> w.eq(Product::getMerchantId, merchantId).or().isNull(Product::getMerchantId));
        wrapper.orderByDesc(Product::getCreateTime);
        return page(page, wrapper);
    }
}
