package com.lxs.protobuf.config;

import com.lxs.protobuf.client.NettyClient;
import io.netty.bootstrap.Bootstrap;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class NettyClientTask implements CommandLineRunner, ApplicationContextAware {
    
    private ApplicationContext context;
    
    @Override
    public void run(String... args) throws Exception {
        //启动netty客户端连接服务端
        NettyClient nettyClient = context.getBean(NettyClient.class);
        nettyClient.doConnection(new Bootstrap(), NettyClient.boss);
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
