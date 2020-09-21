package com.lxs.bigdata.datasource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * AOP实现的多数据进行读写分离操作
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableEurekaClient
@EnableFeignClients
public class SpringBootCoreDatasourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootCoreDatasourceApplication.class, args);
    }

}
