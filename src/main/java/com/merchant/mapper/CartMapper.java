package com.merchant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.merchant.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC")
    List<Cart> selectByUserId(Long userId);

    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND product_id = #{productId} AND deleted = 0")
    Cart selectByUserIdAndProductId(Long userId, Long productId);

    @Select("SELECT COUNT(*) FROM cart WHERE user_id = #{userId} AND deleted = 0")
    int countByUserId(Long userId);

    // ==================== 新增：查询包含已删除的记录 ====================
    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND product_id = #{productId} LIMIT 1")
    Cart selectByUserIdAndProductIdIncludeDeleted(@Param("userId") Long userId, @Param("productId") Long productId);

    // ==================== 新增：恢复已删除的购物车记录 ====================
    @Update("UPDATE cart SET deleted = 0, quantity = #{quantity}, price = #{price}, " +
            "stock = #{stock}, update_time = NOW() WHERE id = #{id}")
    int restoreCart(@Param("id") Long id, @Param("quantity") Integer quantity,
                    @Param("price") BigDecimal price, @Param("stock") Integer stock);
}
