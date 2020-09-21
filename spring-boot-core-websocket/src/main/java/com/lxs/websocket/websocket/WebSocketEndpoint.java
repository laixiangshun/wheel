package com.lxs.websocket.websocket;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lxs.websocket.bridge.WebSocketRabbitMqBridge;
import com.lxs.websocket.config.SpringContext;
import com.lxs.websocket.mq.handler.KanjiaMsgHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 配置websocket的endpoint
 * websocket分布式实现方案：MQ广播消息推送事件
 * 1.各个服务端节点各自保存SessionId与用户标识关联关系
 * 2.通过mq广播消息推送事件
 * 3.各个服务端节点接收到mq消息，实现Session消息推送
 * 需要做的是各个服务端节点初始化各自生成队列，绑定到固定交换机
 *
 * @author lxs
 */
@Slf4j
@Component
@ServerEndpoint(value = "/websocket/{biz}/{key}")
public class WebSocketEndpoint {
    
    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    
    private static ConcurrentMap<String, CopyOnWriteArrayList<WebSocketEndpoint>> webSocketMap = new ConcurrentHashMap<>();
    
    private Session session;
    
    //消息的地址:业务区分.key，例如biz.key
    private String address;
    
    //WebSocket和RabbitMq的消息互通bridge
    private WebSocketRabbitMqBridge webSocketRabbitMqBridge = SpringContext.getBean(WebSocketRabbitMqBridge.class);
    
    /**
     * 打开连接
     *
     * @param biz
     * @param key
     * @param session
     */
    @OnOpen
    public void onOpen(@PathParam("biz") String biz, @PathParam("key") String key, Session session) {
        //参数合法性check
        if (StrUtil.isBlank(biz) || StrUtil.isBlank(key)) {
            String msg = "websocket连接参数不合法，biz=" + biz + ",key=" + key;
            log.error(msg);
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, msg));
            } catch (IOException e) {
                log.error("关闭websocket连接异常:" + e.toString());
            }
        }
        
        //保存websocket连接
        this.address = biz + "." + key;
        this.session = session;
        
        //按address区分websocket的session连接
        CopyOnWriteArrayList<WebSocketEndpoint> webSocketList = webSocketMap.get(address);
        if (CollUtil.isEmpty(webSocketList)) {
            CopyOnWriteArrayList<WebSocketEndpoint> tempList = new CopyOnWriteArrayList<>();
            tempList.add(this);
            webSocketMap.put(address, tempList);
        } else {
            webSocketList.add(this);
        }
        addOnlineCount();
    }
    
    /**
     * 收到消息
     *
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            //message合法性check
            JSONObject msgJson = JSON.parseObject(message);
            if (msgJson != null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(KanjiaMsgHandler.ADDRESS_KEY_MQ_MSG, this.address);
                jsonObject.put(KanjiaMsgHandler.TYPE_KEY_MQ_MSG, "barrage");
                jsonObject.put(KanjiaMsgHandler.BODY_KEY_MQ_MSG, msgJson);
                //发送mq消息,msg格式：{_address:'kanjia.1234567', type:'barrage', body:{nickname:,photoImg:,placeholder:}}
                webSocketRabbitMqBridge.sendRabbitMqMsg(this.address, jsonObject.toJSONString());
                log.info("server receive msg:address=" + this.address);
            } else {
                log.error("server receive msg=" + message + "，address=" + this.address);
            }
        } catch (Exception e) {
            log.error("处理接收到的信息异常：" + e.toString() + "，msg=" + message + "，address=" + this.address);
        }
    }
    
    /**
     * 关闭连接
     *
     * @param session
     * @param closeReason
     */
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        CopyOnWriteArrayList<WebSocketEndpoint> webSocketList = webSocketMap.get(this.address);
        if (webSocketList != null) {
            webSocketList.remove(this);
            if (webSocketList.isEmpty()) {
                webSocketMap.remove(this.address);
            }
            subOnlineCount();
        }
        log.info("onClose: address=" + this.address + ",id=" + session.getId() + ",reason=" + closeReason.getReasonPhrase());
    }
    
    /**
     * 连接出错
     *
     * @param t
     */
    @OnError
    public void onError(Throwable t) {
        log.error("websocket onError:" + t.toString());
    }
    
    /* publish <br/>
     * 广播消息 <br/>
     *
     */
    public static void publish(String address, String message) {
        CopyOnWriteArrayList<WebSocketEndpoint> webSocketList = webSocketMap.get(address);
        if (webSocketList == null) {
            return;
        }
        for (WebSocketEndpoint webSocket : webSocketList) {
            try {
                //发送消息
                webSocket.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                //输出log，继续下一个webSocket的msg发送
                log.error("发送消息失败：原因=" + e.toString() + ",id=" + webSocket.session.getId() + ",msg=" + message);
            }
        }
    }
    
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }
    
    public static synchronized void addOnlineCount() {
        WebSocketEndpoint.onlineCount++;
    }
    
    public static synchronized void subOnlineCount() {
        WebSocketEndpoint.onlineCount--;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.session == null) ? 0 : this.session.getId().hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof WebSocketEndpoint)) {
            return false;
        }
        WebSocketEndpoint other = (WebSocketEndpoint) obj;
        if (this.session == null) {
            if (other.session != null) {
                return false;
            }
        } else return this.session.getId().equals(other.session.getId());
        return true;
    }
}
