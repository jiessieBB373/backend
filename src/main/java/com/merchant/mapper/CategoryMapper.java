package com.merchant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.merchant.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    
    @Select("SELECT * FROM product_category WHERE type = #{type} AND status = 1 AND deleted = 0 ORDER BY sort_order")
    List<Category> selectByType(String type);
    
    @Select("SELECT * FROM product_category WHERE parent_id IS NULL AND status = 1 AND deleted = 0 ORDER BY sort_order")
    List<Category> selectRootCategories();
}
