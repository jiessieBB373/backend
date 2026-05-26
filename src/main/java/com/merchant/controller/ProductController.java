package com.merchant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.merchant.dto.PageQuery;
import com.merchant.dto.Result;
import com.merchant.entity.Category;
import com.merchant.entity.Customer;
import com.merchant.entity.Product;
import com.merchant.service.CategoryService;
import com.merchant.service.CustomerService;
import com.merchant.service.ProductService;
import com.merchant.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CustomerService customerService;

    private final CategoryService categoryService;
    private final JwtUtil jwtUtil;

    public ProductController(ProductService productService, CustomerService customerService, CategoryService categoryService, JwtUtil jwtUtil) {
        this.productService = productService;
        this.customerService = customerService;
        this.categoryService = categoryService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public Result<Page<Product>> list(PageQuery query, HttpServletRequest request) {
        String userType = getUserType(request);
        Long merchantId;
        if ("ADMIN".equals(userType)) {
            merchantId = query.getAdminMerchantId();
        } else {
            merchantId = getMerchantId(request, userType);
        }

        Page<Product> page = new Page<>(query.getPageNum(), query.getPageSize());
        boolean hasKeyword = query.getKeyword() != null && !query.getKeyword().isEmpty();

        List<Long> categoryIds = null;
        if (query.getCategoryId() != null) {
            Category category = categoryService.getById(query.getCategoryId());
            if (category != null && category.getLevel() != null && category.getLevel() == 1) {
                // 是大类，查询其下所有小类
                List<Category> children = categoryService.getChildrenByParentId(query.getCategoryId());
                if (children != null && !children.isEmpty()) {
                    categoryIds = children.stream()
                            .map(Category::getId)
                            .collect(Collectors.toList());
                } else {
                    // 大类下没有小类，用不存在的ID确保查不到
                    categoryIds = Collections.singletonList(-1L);
                }
            } else {
                // 是小类，直接用该ID
                categoryIds = Collections.singletonList(query.getCategoryId());
            }
        }
        boolean hasCategory = categoryIds != null && !categoryIds.isEmpty();
        if (hasKeyword && hasCategory) {
            return Result.success(productService.searchByCategories(query.getKeyword(), categoryIds, merchantId, page));
        } else if (hasKeyword) {
            return Result.success(productService.searchByKeyword(query.getKeyword(), merchantId, page));
        } else if (hasCategory) {
            return Result.success(productService.getByCategoryIdsAndMerchantId(categoryIds, merchantId, page));
        }
        return Result.success(productService.getPageByMerchantId(merchantId, page));
    }

    @GetMapping("/search")
    public Result<Page<Product>> search(@RequestParam String keyword, PageQuery query, HttpServletRequest request) {
        String userType = getUserType(request);
        Long merchantId = getMerchantId(request, userType);
        Page<Product> page = new Page<>(query.getPageNum(), query.getPageSize());
        return Result.success(productService.searchByKeyword(keyword, merchantId, page));
    }

    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        return Result.success(product);
    }

    @PostMapping
    public Result<String> save(@RequestBody Product product, HttpServletRequest request) {
        String userType = getUserType(request);
        if (!"MERCHANT".equals(userType) && !"ADMIN".equals(userType)) {
            return Result.error("无权限添加商品");
        }
        if ("MERCHANT".equals(userType)) {
            product.setMerchantId(getUserId(request));
        }
        productService.save(product);
        return Result.success("创建成功", null);
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Product product, HttpServletRequest request) {
        String userType = getUserType(request);
        Product exist = productService.getById(id);
        if (exist == null) {
            return Result.error("商品不存在");
        }
        if ("MERCHANT".equals(userType) && !getUserId(request).equals(exist.getMerchantId())) {
            return Result.error("无权限修改此商品");
        }
        product.setId(id);
        productService.updateById(product);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id, HttpServletRequest request) {
        String userType = getUserType(request);
        Product exist = productService.getById(id);
        if (exist == null) {
            return Result.error("商品不存在");
        }
        if ("MERCHANT".equals(userType) && !getUserId(request).equals(exist.getMerchantId())) {
            return Result.error("无权限删除此商品");
        }
        productService.removeById(id);
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
