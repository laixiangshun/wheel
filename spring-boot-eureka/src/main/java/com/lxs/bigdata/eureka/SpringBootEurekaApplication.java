package com.lxs.bigdata.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaServer
@ComponentScan(basePackages = "com.lxs")
public class SpringBootEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootEurekaApplication.class, args);
    }

}
