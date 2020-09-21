package com.lxs.websocket.mq.receiver;

import com.lxs.websocket.mq.handler.KanjiaMsgHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * mq消息消费者
 * 创建一个含有uuid的队列并监听，通过异步任务处理消息
 *
 * @author lxs
 */
@Slf4j
@Component
@RabbitListener(bindings = {@QueueBinding(value = @Queue(value = "${websocket.rabbitmq.queue.kanjia.name}", autoDelete = "true"),
        exchange = @Exchange(value = "${websocket.rabbitmq.exchange.topic}", type = ExchangeTypes.TOPIC),
        key = "${websocket.rabbitmq.queue.kanjia.routing-key}")})
public class RabbitMqKanJiaMsgReceiver {
    
    @Autowired
    private KanjiaMsgHandler kanjiaMsgHandler;
    
    /*processMsg<br/>
     *处理mq消息，消息格式： <br/>
     *
     *
     *@return void
     */
    @RabbitHandler
    public void processMsg(String msg) {
        kanjiaMsgHandler.handler(msg);
    }
    
}
