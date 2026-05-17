package com.merchant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.merchant.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC")
    List<Cart> selectByUserId(Long userId);

    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND product_id = #{productId} AND deleted = 0")
    Cart selectByUserIdAndProductId(Long userId, Long productId);

    @Select("SELECT COUNT(*) FROM cart WHERE user_id = #{userId} AND deleted = 0")
    int countByUserId(Long userId);
}
