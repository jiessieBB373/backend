package com.merchant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.merchant.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    
    @Select("SELECT * FROM product_category WHERE type = #{type} AND status = 1 AND deleted = 0 ORDER BY sort_order")
    List<Category> selectByType(String type);
    
    @Select("SELECT * FROM product_category WHERE parent_id IS NULL AND status = 1 AND deleted = 0 ORDER BY sort_order")
    List<Category> selectRootCategories();

    @Select("SELECT c.*, u.shop_name AS merchant_name FROM product_category c " +
            "LEFT JOIN sys_user u ON c.merchant_id = u.id " +
            "WHERE c.status = 1 AND c.deleted = 0 ORDER BY c.sort_order")
    List<Category> selectAllWithMerchantName();

    @Select("SELECT c.*, u.shop_name AS merchant_name FROM product_category c " +
            "LEFT JOIN sys_user u ON c.merchant_id = u.id " +
            "WHERE c.status = 1 AND c.deleted = 0 AND c.merchant_id = #{merchantId} ORDER BY c.sort_order")
    List<Category> selectByMerchantIdWithMerchantName(Long merchantId);


    @Select("SELECT c.*, u.shop_name AS merchant_name FROM product_category c " +
            "LEFT JOIN sys_user u ON c.merchant_id = u.id " +
            "WHERE c.status = 1 AND c.deleted = 0 ORDER BY c.sort_order")
    Page<Category> selectPageWithMerchantName(Page<Category> page);

    @Select("SELECT c.*, u.shop_name AS merchant_name FROM product_category c " +
            "LEFT JOIN sys_user u ON c.merchant_id = u.id " +
            "WHERE c.status = 1 AND c.deleted = 0 AND (c.merchant_id = #{merchantId} OR c.merchant_id IS NULL) " +
            "ORDER BY c.sort_order")
    Page<Category> selectPageByMerchantIdWithMerchantName(Page<Category> page, @Param("merchantId") Long merchantId);

    @Select("SELECT c.*, u.shop_name AS merchant_name FROM product_category c " +
            "LEFT JOIN sys_user u ON c.merchant_id = u.id " +
            "WHERE c.status = 1 AND c.deleted = 0 AND (c.merchant_id = #{merchantId} OR c.merchant_id IS NULL) " +
            "ORDER BY c.sort_order")
    List<Category> selectListByMerchantIdWithMerchantName(@Param("merchantId") Long merchantId);


    /**
     * 根据层级和商户ID查询分类
     * @param merchantId 商户ID
     * @param level 层级：1=大类，2=小类
     */
    @Select("SELECT * FROM product_category WHERE status = 1 AND deleted = 0 " +
            "AND (merchant_id = #{merchantId} OR merchant_id IS NULL) " +
            "AND level = #{level} ORDER BY sort_order")
    List<Category> selectByLevel(@Param("merchantId") Long merchantId, @Param("level") Integer level);

    /**
     * 根据父ID查询子分类（小类）
     * @param parentId 父分类ID
     */
    @Select("SELECT * FROM product_category WHERE status = 1 AND deleted = 0 " +
            "AND parent_id = #{parentId} ORDER BY sort_order")
    List<Category> selectChildrenByParentId(@Param("parentId") Long parentId);

    /**
     * 根据商户ID查询所有分类（用于构建树形结构）
     * @param merchantId 商户ID
     */
    @Select("SELECT * FROM product_category WHERE status = 1 AND deleted = 0 " +
            "AND (merchant_id = #{merchantId} OR merchant_id IS NULL) " +
            "ORDER BY level, sort_order")
    List<Category> selectTreeByMerchantId(@Param("merchantId") Long merchantId);
}
