package com.lxs.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync //通过@EnableAsync开启异步功能
public class SpringBootCoreWebsocketApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SpringBootCoreWebsocketApplication.class, args);
    }
    
}
