package com.merchant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.merchant.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    @Select("SELECT * FROM product WHERE category_id = #{categoryId} AND status = 1 AND deleted = 0")
    List<Product> selectByCategoryId(Long categoryId);
    
    @Select("SELECT * FROM product WHERE status = 1 AND deleted = 0 AND (name LIKE CONCAT('%',#{keyword},'%') OR brand LIKE CONCAT('%',#{keyword},'%'))")
    Page<Product> searchByKeyword(Page<Product> page, @Param("keyword") String keyword);
    
    @Update("UPDATE product SET stock = stock - #{quantity} WHERE id = #{productId} AND stock >= #{quantity}")
    int deductStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    
    @Select("SELECT * FROM product WHERE status = 1 AND deleted = 0 ORDER BY create_time DESC")
    Page<Product> selectPage(Page<Product> page);
}
