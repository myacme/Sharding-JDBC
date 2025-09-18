package com.example.shardingjdbc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sharding-JDBC Demo 主启动类
 */
@SpringBootApplication
@MapperScan("com.example.shardingjdbc.mapper")
public class ShardingJdbcDemoApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ShardingJdbcDemoApplication.class, args);
        System.out.println("=================================");
        System.out.println("Sharding-JDBC Demo 启动成功！");
        System.out.println("Swagger UI: http://localhost:8080/swagger-ui/");
        System.out.println("API文档: http://localhost:8080/v2/api-docs");
        System.out.println("=================================");
    }
}
