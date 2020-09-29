package com.lxs.protobuf.client;

import com.lxs.protobuf.protobuf.UserInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 客户端业务处理
 * 主要实现的几点逻辑是心跳按时发送以及解析服务发送的protobuf格式的数据
 *
 * @author lxs
 */
@Slf4j
@Component
@ChannelHandler.Sharable //注解Sharable主要是为了多个handler可以被多个channel安全地共享，也就是保证线程安全。
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    
    @Autowired
    private NettyClient nettyClient;
    
    /**
     * 循环次数
     */
    private int fcount = 1;
    
    /**
     * 建立连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端:{}建立连接：{}", ctx.channel().id(), new Date());
        ctx.fireChannelActive();
    }
    
    /**
     * 关闭连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端:{}断开连接：{}", ctx.channel().id(), new Date());
        final EventLoop eventLoop = ctx.channel().eventLoop();
        nettyClient.doConnection(new Bootstrap(), eventLoop);
//        eventLoop.schedule(() -> {
//            nettyClient.doConnection(new Bootstrap(), eventLoop);
//        }, 1, TimeUnit.SECONDS);
        super.channelInactive(ctx);
    }
    
    /**
     * 发送心跳, 每4秒发送一次心跳请求;
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("发送心跳的时间：{}，次数：{}", new Date(), fcount);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (IdleState.WRITER_IDLE.equals(idleStateEvent.state())) {
                UserInfo.UserMsg.Builder userState = UserInfo.UserMsg.newBuilder().setState(2);
                ctx.channel().writeAndFlush(userState);
                fcount++;
            }
        }
    }
    
    /**
     * 接收服务端数据，业务处理
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            // 如果不是protobuf类型的数据
            if (!(msg instanceof UserInfo.UserMsg)) {
                log.info("未知数据!" + msg);
                return;
            }
            // 得到protobuf的数据
            UserInfo.UserMsg userMsg = (UserInfo.UserMsg) msg;
            // todo 进行相应的业务处理
            
            // 模拟用户接收数据，进行处理
            log.info("客户端接受到的用户信息。编号:" + userMsg.getId() + ",姓名:" + userMsg.getName() + ",年龄:" + userMsg.getAge());
            
            // 这里返回一个已经接受到数据的状态
            UserInfo.UserMsg.Builder userState = UserInfo.UserMsg.newBuilder().setState(1);
            ctx.writeAndFlush(userState);
            log.info("客户端接收服务端数据处理成功，成功发送响应给服务端!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //收到关闭msg资源
            ReferenceCountUtil.release(msg);
        }
    }
    
    /**
     * 异常处理
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端:{}发生异常", ctx.channel().id(), cause);
        ctx.close();
    }
}
