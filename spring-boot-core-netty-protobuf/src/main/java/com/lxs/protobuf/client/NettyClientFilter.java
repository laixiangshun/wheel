package com.lxs.protobuf.client;

import com.lxs.protobuf.protobuf.UserInfo;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 客户端过滤其这块基本和服务端一直。不过需要注意的是，传输协议、编码和解码应该一致，还有心跳的读写时间应该小于服务端所设置的时间。
 */
@Component
public class NettyClientFilter extends ChannelInitializer<SocketChannel> {
    
    @Autowired
    private NettyClientHandler nettyClientHandler;
    
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //设置心跳超时时间
        pipeline.addLast(new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));
        
        //解码和编码协议
        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        
        pipeline.addLast(new ProtobufDecoder(UserInfo.UserMsg.getDefaultInstance()));
        
        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        
        //设置protobuf协议编码器
        pipeline.addLast(new ProtobufEncoder());
        
        //业务逻辑实现类
        pipeline.addLast(nettyClientHandler);
    }
}
