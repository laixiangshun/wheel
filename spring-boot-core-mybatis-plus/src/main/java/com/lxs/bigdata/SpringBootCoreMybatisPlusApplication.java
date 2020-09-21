package com.lxs.bigdata;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan(basePackages = "com.lxs")
@ComponentScan(basePackages = "com.lxs")
public class SpringBootCoreMybatisPlusApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootCoreMybatisPlusApplication.class, args);
    }

}
