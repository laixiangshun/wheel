package com.lxs.bigdat.deadqueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableHystrix
@EnableFeignClients
@ComponentScan(basePackages = {"com.lxs"})
public class SpringBootCoreDeadQueueApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootCoreDeadQueueApplication.class, args);
    }

}
