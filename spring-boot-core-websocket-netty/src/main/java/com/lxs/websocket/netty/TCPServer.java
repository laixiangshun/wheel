package com.lxs.websocket.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * 服务启动协助类
 *
 * @author lxs
 */
@Component
@Slf4j
@Data
public class TCPServer {
    
    @Autowired
    @Qualifier("serverBootstrap")
    private ServerBootstrap serverBootstrap;
    
    @Autowired
    @Qualifier("tcpSocketAddress")
    private InetSocketAddress tcpPort;
    
    private Channel serverChannel;
    
    public void start() throws Exception {
        serverChannel = serverBootstrap.bind(tcpPort).sync().channel().closeFuture().sync().channel();
    }
    
    @PreDestroy
    public void stop() throws Exception {
        serverChannel.close();
        serverChannel.parent().close();
    }
}
