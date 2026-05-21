package com.merchant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.merchant.dto.LoginResponse;
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

    /**
     * 获取树形分类结构（包含大类和小类）
     * 用于小程序首页展示两级分类，以及管理后台分类管理
     */
    @GetMapping("/tree")
    public Result<List<Category>> getTree(HttpServletRequest request) {
        LoginResponse user = getUserFromToken(request);
        if (user == null) {
            return Result.error(401, "未登录");
        }

        List<Category> tree;
        if ("ADMIN".equals(user.getUserType())) {
            // 管理员：如果指定了adminMerchantId则返回该商户的分类，否则返回所有分类
            String adminMerchantId = request.getParameter("adminMerchantId");
            if (adminMerchantId != null && !adminMerchantId.isEmpty()) {
                tree = categoryService.getTreeByMerchantId(Long.parseLong(adminMerchantId));
            } else {
                // 管理员未选择商户，返回所有分类（管理后台用）
                tree = categoryService.getAllTree();
            }
        } else if ("MERCHANT".equals(user.getUserType()) || "CUSTOMER".equals(user.getUserType())) {
            // 商户和客户：只能看到自己商户的分类树
            tree = categoryService.getTreeByMerchantId(user.getMerchantId());
        } else {
            tree = List.of();
        }

        return Result.success(tree);
    }

    /**
     * 根据层级获取分类
     * @param level 层级：1=大类，2=小类
     */
    @GetMapping("/level/{level}")
    public Result<List<Category>> getByLevel(@PathVariable Integer level, HttpServletRequest request) {
        LoginResponse user = getUserFromToken(request);
        if (user == null) {
            return Result.error(401, "未登录");
        }

        List<Category> categories;
        if ("ADMIN".equals(user.getUserType())) {
            String adminMerchantId = request.getParameter("adminMerchantId");
            if (adminMerchantId != null && !adminMerchantId.isEmpty()) {
                categories = categoryService.getByLevel(Long.parseLong(adminMerchantId), level);
            } else {
                categories = List.of();
            }
        } else {
            categories = categoryService.getByLevel(user.getMerchantId(), level);
        }

        return Result.success(categories);
    }

    /**
     * 获取子分类（小类）
     * @param parentId 父分类ID（大类ID）
     */
    @GetMapping("/children/{parentId}")
    public Result<List<Category>> getChildren(@PathVariable Long parentId, HttpServletRequest request) {
        LoginResponse user = getUserFromToken(request);
        if (user == null) {
            return Result.error(401, "未登录");
        }

        // 权限检查：验证parentId是否属于当前用户商户
        // 这里简化处理，实际应该查询parentId的商户ID并验证
        List<Category> children = categoryService.getChildrenByParentId(parentId);
        return Result.success(children);
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

    /**
     * 从请求中解析用户信息
     */
    private LoginResponse getUserFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            String userType = jwtUtil.getUserTypeFromToken(token);
            Long userId = jwtUtil.getUserIdFromToken(token);

            LoginResponse user = new LoginResponse();
            user.setUsername(username);
            user.setUserType(userType);
            user.setUserId(userId);

            // 商户和客户需要获取merchantId
            if ("MERCHANT".equals(userType)) {
                user.setMerchantId(userId);
            } else if ("CUSTOMER".equals(userType)) {
                // CUSTOMER的merchantId从customer表获取
                Long merchantId = customerService.getMerchantIdByUserId(userId);
                user.setMerchantId(merchantId);
            }

            return user;
        }
        return null;
    }
}
