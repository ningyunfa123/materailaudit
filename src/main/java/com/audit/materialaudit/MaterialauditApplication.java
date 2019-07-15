package com.audit.materialaudit;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.audit.materialaudit.mapper")
@EnableScheduling
@EnableCaching
public class MaterialauditApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaterialauditApplication.class, args);
    }
}
