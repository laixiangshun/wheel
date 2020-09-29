package com.lxs.protobuf.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("nettyServer")
public class NettyServer {
    
    private static final int prot = 9876;
    
    private static EventLoopGroup boss = new NioEventLoopGroup();
    
    private static EventLoopGroup work = new NioEventLoopGroup();
    
    private static ServerBootstrap b = new ServerBootstrap();
    
    @Autowired
    private NettyServerFilter nettyServerFilter;
    
    public void run() {
        try {
            ChannelFuture sync = b.group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(nettyServerFilter)
                    .bind(prot)
                    .sync();
            log.info("服务端启动成功，端口是：{}", prot);
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            work.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }
}
