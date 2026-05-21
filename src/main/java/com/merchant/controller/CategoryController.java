package com.merchant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.merchant.dto.PageQuery;
import com.merchant.dto.Result;
import com.merchant.entity.Category;
import com.merchant.entity.Customer;
import com.merchant.service.CategoryService;
import com.merchant.service.CustomerService;
import com.merchant.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CustomerService customerService;
    private final JwtUtil jwtUtil;

    public CategoryController(CategoryService categoryService, CustomerService customerService, JwtUtil jwtUtil) {
        this.categoryService = categoryService;
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public Result<Page<Category>> list(PageQuery query, HttpServletRequest request) {
        String userType = getUserType(request);
        Long merchantId = getMerchantId(request, userType);
        Page<Category> page = new Page<>(query.getPageNum(), query.getPageSize());

        return Result.success(categoryService.getPageByMerchantId(merchantId, page));
    }

    @GetMapping("/all")
    public Result<List<Category>> listAll(HttpServletRequest request) {
        String userType = getUserType(request);
        Long merchantId = getMerchantId(request, userType);

        return Result.success(categoryService.getListByMerchantId(merchantId));
    }

    @GetMapping("/type/{type}")
    public Result<List<Category>> getByType(@PathVariable String type, HttpServletRequest request) {
        return Result.success(categoryService.getByType(type));
    }

    @GetMapping("/roots")
    public Result<List<Category>> getRoots(HttpServletRequest request) {
        return Result.success(categoryService.getRootCategories());
    }

    @PostMapping
    public Result<String> save(@RequestBody Category category, HttpServletRequest request) {
        String userType = getUserType(request);
        if (!"MERCHANT".equals(userType) && !"ADMIN".equals(userType)) {
            return Result.error("无权限添加分类");
        }
        if ("MERCHANT".equals(userType)) {
            category.setMerchantId(getUserId(request));
        }
        categoryService.save(category);
        return Result.success("添加成功", null);
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Category category, HttpServletRequest request) {
        String userType = getUserType(request);
        Category exist = categoryService.getById(id);
        if (exist == null) {
            return Result.error("分类不存在");
        }
        if ("MERCHANT".equals(userType) && !getUserId(request).equals(exist.getMerchantId())) {
            return Result.error("无权限修改此分类");
        }
        category.setId(id);
        categoryService.updateById(category);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id, HttpServletRequest request) {
        String userType = getUserType(request);
        Category exist = categoryService.getById(id);
        if (exist == null) {
            return Result.error("分类不存在");
        }
        if ("MERCHANT".equals(userType) && !getUserId(request).equals(exist.getMerchantId())) {
            return Result.error("无权限删除此分类");
        }
        categoryService.removeById(id);
        return Result.success("删除成功", null);
    }

    // ========== 辅助方法 ==========

    private Long getMerchantId(HttpServletRequest request, String userType) {
        if ("ADMIN".equals(userType)) {
            return null;
        } else if ("MERCHANT".equals(userType)) {
            return getUserId(request);
        } else if ("CUSTOMER".equals(userType)) {
            Long customerUserId = getUserId(request);
            Customer customer = customerService.getByUserId(customerUserId);
            return customer != null ? customer.getMerchantId() : null;
        }
        return null;
    }

    private String getUserType(HttpServletRequest request) {
        String token = getToken(request);
        return token != null ? jwtUtil.getUserTypeFromToken(token) : null;
    }

    private Long getUserId(HttpServletRequest request) {
        String token = getToken(request);
        return token != null ? jwtUtil.getUserIdFromToken(token) : null;
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }
}
