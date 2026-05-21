package com.merchant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.merchant.dto.AddCustomerRequest;
import com.merchant.dto.PageQuery;
import com.merchant.dto.Result;
import com.merchant.entity.Customer;
import com.merchant.entity.User;
import com.merchant.service.CustomerService;
import com.merchant.service.UserService;
import com.merchant.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public CustomerController(CustomerService customerService, UserService userService, JwtUtil jwtUtil) {
        this.customerService = customerService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public Result<Page<Customer>> list(PageQuery query, HttpServletRequest request) {
        String userType = getUserType(request);
        if (!"MERCHANT".equals(userType) && !"ADMIN".equals(userType)) {
            return Result.error("无权限查看客户列表");
        }
        Page<Customer> page = new Page<>(query.getPageNum(), query.getPageSize());

        if ("ADMIN".equals(userType)) {
            return Result.success(customerService.page(page));
        }
        return Result.success(customerService.getPageByMerchantId(getUserId(request), page));
    }

    @GetMapping("/all")
    public Result<List<Customer>> listAll(HttpServletRequest request) {
        String userType = getUserType(request);
        if (!"MERCHANT".equals(userType) && !"ADMIN".equals(userType)) {
            return Result.error("无权限");
        }
        if ("ADMIN".equals(userType)) {
            return Result.success(customerService.list());
        }
        return Result.success(customerService.getListByMerchantId(getUserId(request)));
    }

    @GetMapping("/me")
    public Result<Customer> getMyInfo(HttpServletRequest request) {
        String userType = getUserType(request);
        if (!"CUSTOMER".equals(userType)) {
            return Result.error("无权限");
        }
        Long userId = getUserId(request);
        Customer customer = customerService.getByUserId(userId);
        if (customer == null) {
            return Result.error("客户信息不存在");
        }
        return Result.success(customer);
    }

    @PostMapping
    public Result<String> add(@RequestBody AddCustomerRequest addRequest, HttpServletRequest request) {
        String userType = getUserType(request);
        if (!"MERCHANT".equals(userType) && !"ADMIN".equals(userType)) {
            return Result.error("无权限添加客户");
        }

        // 1. 先创建用户
        User user = new User();
        user.setUsername(addRequest.getUsername());
        user.setPassword(addRequest.getPassword());
        user.setRealName(addRequest.getRealName());
        user.setPhone(addRequest.getPhone());
        user.setShopAddress(addRequest.getAddress());
        user.setUserType("CUSTOMER");
        user.setStatus(1);

        try {
            userService.register(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }

        // 2. 再创建客户关联
        Customer customer = new Customer();
        customer.setUserId(user.getId());
        customer.setMerchantId("MERCHANT".equals(userType) ? getUserId(request) : addRequest.getMerchantId());
        customerService.save(customer);

        return Result.success("添加成功", null);
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Customer customer, HttpServletRequest request) {
        String userType = getUserType(request);
        Customer exist = customerService.getById(id);
        if (exist == null) {
            return Result.error("客户不存在");
        }
        if ("MERCHANT".equals(userType) && !getUserId(request).equals(exist.getMerchantId())) {
            return Result.error("无权限修改此客户");
        }
        // 更新用户信息（姓名/电话/地址存在sys_user中）
        User user = userService.getById(exist.getUserId());
        if (user != null) {
            if (customer.getRealName() != null) user.setRealName(customer.getRealName());
            if (customer.getPhone() != null) user.setPhone(customer.getPhone());
            if (customer.getShopAddress() != null) user.setShopAddress(customer.getShopAddress());
            userService.updateById(user);
        }
        return Result.success("更新成功", null);
    }

    @PutMapping("/{id}/status")
    public Result<String> toggleStatus(@PathVariable Long id, @RequestParam Integer status, HttpServletRequest request) {
        String userType = getUserType(request);
        Customer exist = customerService.getById(id);
        if (exist == null) {
            return Result.error("客户不存在");
        }
        if ("MERCHANT".equals(userType) && !getUserId(request).equals(exist.getMerchantId())) {
            return Result.error("无权限操作此客户");
        }
        // 更新用户状态（客户状态在sys_user中）
        User user = userService.getById(exist.getUserId());
        if (user != null) {
            user.setStatus(status);
            userService.updateById(user);
        }
        return Result.success(status == 1 ? "已启用" : "已禁用", null);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id, HttpServletRequest request) {
        String userType = getUserType(request);
        Customer exist = customerService.getById(id);
        if (exist == null) {
            return Result.error("客户不存在");
        }
        if ("MERCHANT".equals(userType) && !getUserId(request).equals(exist.getMerchantId())) {
            return Result.error("无权限删除此客户");
        }
        customerService.removeById(id);
        userService.removeById(exist.getUserId());
        return Result.success("删除成功", null);
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