package com.lxs.bigdata.node;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
@ComponentScan(basePackages = "com.lxs")
public class ZipkinNode1Application {

    public static void main(String[] args) {
        SpringApplication.run(ZipkinNode1Application.class, args);
    }

}
