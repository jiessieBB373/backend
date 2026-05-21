package com.merchant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.merchant.dto.PageQuery;
import com.merchant.dto.Result;
import com.merchant.entity.User;
import com.merchant.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/merchants")
public class MerchantController {
    
    private final UserService userService;
    
    public MerchantController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    public Result<Page<User>> list(PageQuery query) {
        Page<User> page = new Page<>(query.getPageNum(), query.getPageSize());
        return Result.success(userService.getMerchantList(page));
    }

    @GetMapping("/all")
    public Result<List<User>> getAllMerchants() {
        Page<User> page = new Page<>(1, 1000);
        Page<User> result = userService.getMerchantList(page);
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(result.getRecords());
    }
    
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("商户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }
    
    @PostMapping
    public Result<String> save(@RequestBody User user) {
        user.setUserType("MERCHANT");
        userService.register(user);
        return Result.success("创建成功", null);
    }
    
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        user.setPassword(null); // 不更新密码
        userService.updateById(user);
        return Result.success("更新成功", null);
    }
    
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success("删除成功", null);
    }
    
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userService.updateById(user);
        return Result.success("状态更新成功", null);
    }
}
