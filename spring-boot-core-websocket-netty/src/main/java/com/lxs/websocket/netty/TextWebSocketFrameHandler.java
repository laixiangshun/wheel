package com.lxs.websocket.netty;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.lxs.websocket.async.MsgAsyncTask;
import com.lxs.websocket.model.ProtobufMsg;
import com.lxs.websocket.model.UserMsg;
import com.lxs.websocket.template.LikeRedisTemplate;
import com.lxs.websocket.template.LikeSomeCacheTemplate;
import com.lxs.websocket.utils.RandomNameUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * netty实现websocket核心消息处理类
 * 改造netty支持url参数获取
 *
 * @author lxs
 */
@Slf4j
@Component
@Qualifier(value = "textWebSocketFrameHandler")
@ChannelHandler.Sharable
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    
    @Autowired
    private LikeRedisTemplate redisTemplate;
    
    @Autowired
    private LikeSomeCacheTemplate cacheTemplate;
    
    @Autowired
    private MsgAsyncTask msgAsyncTesk;
    
    /**
     * 不支持获取url参数
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        
    }
    
    /**
     * TextWebSocketFrameHandler 的channelRead方法，首次连接会是一个FullHttpRequest类型，
     * 可以通过FullHttpRequest.uri()获取完整ws的URL地址，之后接受信息的话，会是一个TextWebSocketFrame类型。
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //首次连接是FullHttpRequest，处理参数
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String uri = request.uri();
            
            Map<String, String> paramMap = getUrlParams(uri);
            log.info("接收到的参数是：" + JSON.toJSONString(paramMap));
            //如果url包含参数，需要处理
            if (uri.contains("?")) {
                String newUri = uri.substring(0, uri.indexOf("?"));
                log.info(newUri);
                request.setUri(newUri);
            }
            log.info("连接地址：" + ctx.channel().remoteAddress());
            String uName = String.valueOf(RandomNameUtil.getName());  //用来获取一个随机的用户名，可以用其他方式代替
            //新用户接入
            Channel incoming = ctx.channel();
            for (Channel channel : MyChannelHandlerPool.channelGroup) {
//                channel.writeAndFlush(new TextWebSocketFrame("[新用户] - " + uName + " 加入"));
                sendMessage(channel, "[新用户] - " + uName + " 加入", StrUtil.EMPTY);
            }
            UserMsg userMsg = new UserMsg();
            userMsg.setId(paramMap.get("id"));
            userMsg.setName(uName);
            redisTemplate.save(incoming.id().asLongText(), userMsg);   //存储用户
            MyChannelHandlerPool.channelGroup.add(ctx.channel());
            sendMessage(ctx.channel(), "[新用户] - " + uName + " 加入", JSON.toJSONString(userMsg));
            
        } else if (msg instanceof TextWebSocketFrame) {
            //正常的TEXT消息类型
            TextWebSocketFrame frame = (TextWebSocketFrame) msg;
            log.info("客户端收到服务器数据：" + frame.text());
            sendAllMessage(ctx, frame.text());
        }
        super.channelRead(ctx, msg);
    }
    
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("连接地址：" + ctx.channel().remoteAddress());
        Channel incoming = ctx.channel();
        UserMsg userMsg = (UserMsg) redisTemplate.get(incoming.id().asLongText());
        if (ObjectUtil.isNull(userMsg)) {
            String uName = String.valueOf(RandomNameUtil.getName());  //用来获取一个随机的用户名，可以用其他方式代替
            //新用户接入
            for (Channel channel : MyChannelHandlerPool.channelGroup) {
//                channel.writeAndFlush(new TextWebSocketFrame("[新用户] - " + uName + " 加入"));
                sendMessage(channel, "[新用户] - " + uName + " 加入", StrUtil.EMPTY);
            }
            userMsg = new UserMsg();
            userMsg.setId(IdUtil.fastUUID());
            userMsg.setName(uName);
            redisTemplate.save(incoming.id().asLongText(), userMsg);   //存储用户
            MyChannelHandlerPool.channelGroup.add(ctx.channel());
            sendMessage(ctx.channel(), "[新用户] - " + uName + " 加入", JSON.toJSONString(userMsg));
        }
    }
    
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        UserMsg userMsg = (UserMsg) redisTemplate.get(incoming.id().asLongText());
        //用户离开
        for (Channel channel : MyChannelHandlerPool.channelGroup) {
//            channel.writeAndFlush(new TextWebSocketFrame("[用户] - " + userMsg.getName() + " 离开"));
            sendMessage(channel, "[用户] - " + userMsg.getName() + " 离开", StrUtil.EMPTY);
        }
        redisTemplate.delete(incoming.id().asLongText());   //删除用户
        MyChannelHandlerPool.channelGroup.remove(ctx.channel());
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        UserMsg userMsg = (UserMsg) redisTemplate.get(incoming.id().asLongText());
        log.info("用户:" + userMsg.getName() + "在线");
    }
    
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        UserMsg userMsg = (UserMsg) redisTemplate.get(incoming.id().asLongText());
        log.info("用户:" + userMsg.getName() + "掉线");
        msgAsyncTesk.saveChatMsgTask();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)throws Exception {
        Channel incoming = ctx.channel();
        UserMsg userMsg = (UserMsg) redisTemplate.get(incoming.id().asLongText());
        log.info("用户:" + userMsg.getName() + "异常");
        cause.printStackTrace();
        ctx.close();
    }
    
    private void sendMessage(Channel channel, String message, String data) {
        UserMsg userMsg = (UserMsg) redisTemplate.get(channel.id().asLongText());
        ProtobufMsg protobufMsg = new ProtobufMsg();
        protobufMsg.setId(userMsg.getId());
        protobufMsg.setMsg(message);
        protobufMsg.setBody(data);
        channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(protobufMsg)));
    }
    
    /**
     * 群发消息
     *
     * @param ctx
     * @param message
     */
    private void sendAllMessage(ChannelHandlerContext ctx, String message) {
        //收到信息后，群发给所有channel
//        MyChannelHandlerPool.channelGroup.writeAndFlush(new TextWebSocketFrame(message));
        Channel incoming = ctx.channel();
        UserMsg userMsg = (UserMsg) redisTemplate.get(incoming.id().asLongText());
        for (Channel channel : MyChannelHandlerPool.channelGroup) {
            //将当前每个聊天内容进行存储
            log.info("给用户：" + userMsg.getName() + "-发送数据：" + message);
            cacheTemplate.save(userMsg.getId(), userMsg.getName(), message);
            if (channel != incoming) {
//                channel.writeAndFlush(new TextWebSocketFrame("[" + userMsg.getName() + "]" + message));
                sendMessage(channel, "[" + userMsg.getName() + "]" + message, StrUtil.EMPTY);
            } else {
//                channel.writeAndFlush(new TextWebSocketFrame("[you]" + message));
                sendMessage(channel, "[you]" + message, StrUtil.EMPTY);
            }
        }
    }
    
    /**
     * 获取url中参数
     *
     * @param url
     * @return
     */
    private static Map<String, String> getUrlParams(String url) {
        Map<String, String> map = new HashMap<>();
        url = url.replace("?", ";");
        if (!url.contains(";")) {
            return map;
        }
        if (url.split(";").length > 0) {
            String[] arr = url.split(";")[1].split("&");
            for (String s : arr) {
                String key = s.split("=")[0];
                String value = s.split("=")[1];
                map.put(key, value);
            }
            return map;
            
        } else {
            return map;
        }
    }
}
