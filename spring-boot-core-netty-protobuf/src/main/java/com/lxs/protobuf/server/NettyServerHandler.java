package com.lxs.protobuf.server;

import com.lxs.protobuf.protobuf.UserInfo;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 继承SimpleChannelInboundHandler类之后，会在接收到数据后会自动release掉数据占用的Bytebuffer资源。并且继承该类需要指定数据格式。
 * 而继承ChannelInboundHandlerAdapter则不会自动释放，需要手动调用ReferenceCountUtil.release()等方法进行释放。继承该类不需要指定数据格式。
 * 推荐服务端继承ChannelInboundHandlerAdapter，手动进行释放，防止数据未处理完就自动释放了。而且服务端可能有多个客户端进行连接，并且每一个客户端请求的数据格式都不一致，这时便可以进行相应的处理。
 * 客户端根据情况可以继承SimpleChannelInboundHandler类。好处是直接指定好传输的数据格式，就不需要再进行格式的转换了。
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    
    /**
     * 空闲次数
     */
    private int idle_count = 1;
    
    /**
     * 发送次数
     */
    private int count = 1;
    
    /**
     * 建立连接时，发送一条消息
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接的客户端:{},地址:{}", ctx.channel().id(), ctx.channel().remoteAddress());
        NettyServerChannelPool.channels.add(ctx.channel());
        UserInfo.UserMsg userMsg = UserInfo.UserMsg
                .newBuilder()
                .setId(1)
                .setAge(18)
                .setName("xuwujing")
                .setState(0)
                .build();
        ctx.writeAndFlush(userMsg);
        super.channelActive(ctx);
    }
    
    /**
     * 超时处理 如果5秒没有接受客户端的心跳，就触发; 如果超过两次，则直接关闭;
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (IdleState.READER_IDLE.equals(idleStateEvent.state())) {
                log.info("服务端已经5秒没有收到客户端:{}消息啦！", ctx.channel().id());
                if (idle_count > 2) {
                    log.info("关闭这个不活跃的channel");
                    ctx.channel().close();
                    NettyServerChannelPool.channels.remove(ctx.channel());
                }
                idle_count++;
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
    
    /**
     * 接受到数据，业务处理
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("第{}次，服务端接受到客户端：{}的消息：{}", count, ctx.channel().id(), msg);
        try {
            if (msg instanceof UserInfo.UserMsg) {
                UserInfo.UserMsg userMsg = (UserInfo.UserMsg) msg;
                if (userMsg.getState() == 1) {
                    log.info("客户端处理成功");
                    NettyServerChannelPool.channels.writeAndFlush("客户端：" + ctx.channel().id() + "已经处理完消息,棒棒的");
                } else if (userMsg.getState() == 2) {
                    log.info("服务端接受到心跳处理");
                } else {
                    log.info("未知命令");
                }
            } else {
                log.error("服务端接收到未知数据：{}", msg);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //手动释放消息
            ReferenceCountUtil.release(msg);
        }
        count++;
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务端与客户端：{}连接异常：{}", ctx.channel().id(), cause.getMessage());
        ctx.close();
        NettyServerChannelPool.channels.remove(ctx.channel());
    }
}
