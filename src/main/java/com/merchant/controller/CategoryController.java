package com.merchant.controller;

import com.merchant.dto.Result;
import com.merchant.entity.Category;
import com.merchant.service.CategoryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    @GetMapping
    public Result<List<Category>> list() {
        return Result.success(categoryService.list());
    }
    
    @GetMapping("/type/{type}")
    public Result<List<Category>> getByType(@PathVariable String type) {
        return Result.success(categoryService.getByType(type));
    }
    
    @GetMapping("/roots")
    public Result<List<Category>> getRoots() {
        return Result.success(categoryService.getRootCategories());
    }
    
    @PostMapping
    public Result<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success("创建成功", null);
    }
    
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        categoryService.updateById(category);
        return Result.success("更新成功", null);
    }
    
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success("删除成功", null);
    }
}
