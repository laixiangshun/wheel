package com.lxs.bigdata.aop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ComponentScan(value = "com.lxs")
@EnableSwagger2
public class SpringBootCoreAopApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootCoreAopApplication.class, args);
    }

}
