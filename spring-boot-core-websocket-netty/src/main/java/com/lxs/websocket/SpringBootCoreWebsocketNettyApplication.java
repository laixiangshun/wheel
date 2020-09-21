package com.lxs.websocket;

import com.lxs.websocket.config.NettyConfig;
import com.lxs.websocket.netty.TCPServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringBootCoreWebsocketNettyApplication {
    
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(SpringBootCoreWebsocketNettyApplication.class, args);
        //注入NettyConfig 获取对应Bean
        NettyConfig nettyConfig = context.getBean(NettyConfig.class);
        //注入TCPServer 获取对应Bean
        TCPServer tcpServer = context.getBean(TCPServer.class);
        //启动websocket的服务
        tcpServer.start();
    }
    
}
