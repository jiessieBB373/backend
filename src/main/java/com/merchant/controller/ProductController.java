package com.merchant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.merchant.dto.PageQuery;
import com.merchant.dto.Result;
import com.merchant.entity.Product;
import com.merchant.service.ProductService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping
    public Result<Page<Product>> list(PageQuery query) {
        Page<Product> page = new Page<>(query.getPageNum(), query.getPageSize());
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            return Result.success(productService.search(query.getKeyword(), page));
        }
        return Result.success(productService.getPage(page));
    }
    
    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        return Result.success(product);
    }

    @GetMapping("/category/{categoryId}")
    public Result<Page<Product>> getByCategory(@PathVariable Long categoryId, PageQuery query) {
        Page<Product> page = new Page<>(query.getPageNum(), query.getPageSize());
        return Result.success(productService.getByCategoryIdPage(categoryId, page));
    }
    
    @PostMapping
    public Result<String> save(@RequestBody Product product) {
        productService.save(product);
        return Result.success("创建成功", null);
    }
    
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        productService.updateById(product);
        return Result.success("更新成功", null);
    }
    
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        productService.removeById(id);
        return Result.success("删除成功", null);
    }
}
