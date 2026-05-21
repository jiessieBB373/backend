package com.merchant.dto;

import lombok.Data;

/**
 * 分页查询参数
 */
@Data
public class PageQuery {
    private Long pageNum = 1L;
    private Long pageSize = 10L;
    private String keyword;
    private String status;

    private Long merchantId;
    private Long categoryId;
    private Long customerId;
}
