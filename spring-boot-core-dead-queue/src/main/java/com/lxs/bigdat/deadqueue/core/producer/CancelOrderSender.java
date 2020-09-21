package com.lxs.bigdat.deadqueue.core.producer;

import com.lxs.bigdat.deadqueue.constant.QueueEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 往订单延迟队列发送消息
 */
@Component
@Slf4j
public class CancelOrderSender {

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 发送消息
     */
    public void sendMessage(Long orderId, long delayTimes) {
        MessagePostProcessor messagePostProcessor = message -> {
            //给消息设置延迟毫秒值
            message.getMessageProperties().setExpiration(String.valueOf(delayTimes));
            MessageProperties messageProperties = message.getMessageProperties();
            String receivedExchange = messageProperties.getReceivedExchange();
            String consumerQueue = messageProperties.getConsumerQueue();
            String receivedRoutingKey = messageProperties.getReceivedRoutingKey();
            String messageId = messageProperties.getMessageId();
            log.info("消息：{}，发送到交换机：{}，路由信息：{}，接收队列：{}", messageId, receivedExchange, receivedRoutingKey, consumerQueue);
            return message;
        };
        amqpTemplate.convertAndSend(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getExchange(), QueueEnum.QUEUE_TTL_ORDER_CANCEL.getRouteKey(), orderId, messagePostProcessor);
        log.info("send delay message orderId:{}", orderId);
    }
}
