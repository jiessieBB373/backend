package com.merchant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.merchant.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    User selectByUsername(String username);
    
    @Select("SELECT COUNT(*) FROM sys_user WHERE username = #{username} AND deleted = 0")
    int countByUsername(String username);

    // 新增
    @Select("SELECT * FROM sys_user WHERE wechat_open_id = #{openId} AND deleted = 0")
    User selectByWechatOpenId(String openId);
}
