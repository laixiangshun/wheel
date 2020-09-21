package com.lxs.websocket.mq.handler;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lxs.websocket.bridge.WebSocketRabbitMqBridge;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 异步处理mq中消息
 *
 * @author lxs
 */
@Component
@Slf4j
public class KanjiaMsgHandler {
    
    /**
     * mq消息中的address key，代表websocket的地址
     */
    public static final String ADDRESS_KEY_MQ_MSG = "_address";
    
    /**
     * mq消息中的type
     */
    public static final String TYPE_KEY_MQ_MSG = "_type";
    
    /**
     * mq消息中的内容body
     */
    public static final String BODY_KEY_MQ_MSG = "_body";
    
    @Autowired
    private WebSocketRabbitMqBridge webSocketRabbitMqBridge;
    
    @Async("taskExecutor")
    public void handler(String msg) {
        if (StrUtil.isBlank(msg)) {
            //空msg
            return;
        }
        //msg格式：{_address:'kanjia.1234567', type:'barrage', body:{nickname:,photoImg:,placeholder:}}
        try {
            JSONObject msgJson = JSON.parseObject(msg);
            String address = msgJson.getString(ADDRESS_KEY_MQ_MSG);
            webSocketRabbitMqBridge.publishWebSocketMsg(address, msgJson.getString(BODY_KEY_MQ_MSG));
            log.info("server send msg:address=" + address);
        } catch (Exception e) {
            log.error("消息处理异常：" + e.toString() + ",msg=" + msg);
        }
    }
}
