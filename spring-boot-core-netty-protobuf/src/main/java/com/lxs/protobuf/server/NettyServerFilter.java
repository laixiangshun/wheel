package com.lxs.protobuf.server;

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
 * 心跳超时设置，传输协议设置
 *
 * @author lxs
 */
@Component
public class NettyServerFilter extends ChannelInitializer<SocketChannel> {
    
    @Autowired
    private NettyServerHandler nettyServerHandler;
    
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //设置心跳超时时间
        pipeline.addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
        
        //解码和编码协议
        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        
        pipeline.addLast(new ProtobufDecoder(UserInfo.UserMsg.getDefaultInstance()));
        
        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        
        //设置protobuf协议编码器
        pipeline.addLast(new ProtobufEncoder());
        
        //业务逻辑实现类
        pipeline.addLast(nettyServerHandler);
    }
}
