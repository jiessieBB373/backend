package com.merchant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 商户批发下单系统主启动类
 * 
 * @author merchant-wholesale
 * @version 1.0.0
 */
@SpringBootApplication
@MapperScan("com.merchant.mapper")
public class WholesaleApplication {

    public static void main(String[] args) {
        SpringApplication.run(WholesaleApplication.class, args);
    }
}
