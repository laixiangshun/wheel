package com.lxs.protobuf;

import com.lxs.protobuf.server.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringBootCoreNettyProtobufApplication {
    
    public static void main(String[] args) {
        // 启动嵌入式的 Tomcat 并初始化 Spring 环境及其各 Spring 组件
        ConfigurableApplicationContext context = SpringApplication.run(SpringBootCoreNettyProtobufApplication.class, args);
        
        //启动netty服务端
        NettyServer nettyServer = context.getBean(NettyServer.class);
        nettyServer.run();
        
        
    }
    
}
