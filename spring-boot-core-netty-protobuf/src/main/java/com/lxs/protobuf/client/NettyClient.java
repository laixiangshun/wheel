package com.lxs.protobuf.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * netty 客户端
 * 监听的端口和一个监听器(用来监听是否和服务端断开连接，用于重连)
 *
 * @author lxs
 */
@Slf4j
@Component
public class NettyClient {
    
    private static boolean initFlg = true;
    
    private static final int PORT = 9876;
    
    private static final String HOST = "localhost";
    
    public static EventLoopGroup boss = new NioEventLoopGroup();
    
    public static Bootstrap b = new Bootstrap();
    
    @Autowired
    private NettyClientFilter nettyClientHandler;
    
    public void doConnection(Bootstrap bootstrap, EventLoopGroup eventLoopGroup) {
        if (bootstrap != null) {
            try {
                ChannelFuture channelFuture = bootstrap.group(eventLoopGroup)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .handler(nettyClientHandler)
                        .remoteAddress(HOST, PORT)
                        .connect()
                        .addListener((ChannelFuture future) -> {
                            EventLoop eventLoop = future.channel().eventLoop();
                            if (!future.isSuccess()) {
                                log.info("客户端与服务端端开连接，在10秒后进行重连");
                                eventLoop.schedule(() -> {
                                    doConnection(new Bootstrap(), eventLoop);
                                }, 10, TimeUnit.SECONDS);
                            }
                        });
                if (initFlg) {
                    log.info("客户端与服务端建立连接成功");
                    initFlg = false;
                }
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                log.error("客户端与服务端建立连接失败", e);
            }
        }
    }
}
