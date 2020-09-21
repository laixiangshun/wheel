package com.lxs.websocket.bridge;

import com.lxs.websocket.websocket.WebSocketEndpoint;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * websocket和rabbitmq消息过渡的桥梁
 *
 * @author lxs
 */
@Component
public class WebSocketRabbitMqBridge {
    
    //websocket和rabbitmq传递消息的exchange
    @Value("${websocket.rabbitmq.exchange.topic}")
    private String websocket_rabbitmq_exchange_topic;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    /**
     * sendRabbitMqMsg <br/>
     * 将websocket消息转发到rabbitmq上，进行服务间传播 <br/>
     *
     * @param routingKey
     * @param object
     */
    public void sendRabbitMqMsg(String routingKey, final Object object) {
        rabbitTemplate.convertAndSend(websocket_rabbitmq_exchange_topic, routingKey, object);
    }
    
    
    /**
     * publish <br/>
     * 广播websocket消息 <br/>
     *
     * @param address
     * @param msg
     */
    public void publishWebSocketMsg(String address, String msg) {
        WebSocketEndpoint.publish(address, msg);
    }
    
}
