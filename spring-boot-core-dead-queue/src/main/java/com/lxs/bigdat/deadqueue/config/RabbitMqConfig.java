package com.lxs.bigdat.deadqueue.config;

import com.lxs.bigdat.deadqueue.constant.QueueEnum;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitMQ 配置类
 *
 * @author lxs
 */
@Configuration
public class RabbitMqConfig {

    /**
     * 订单消息实际消费队列所绑定的交换机
     */
    @Bean(name = "orderDirect")
    @Qualifier(value = "orderTtlDirect")
    public DirectExchange orderDirect() {
        Exchange exchange = ExchangeBuilder.directExchange(QueueEnum.QUEUE_ORDER_CANCEL.getExchange())
                .durable(true)
                .build();
        return (DirectExchange) exchange;
    }

    /**
     * 订单延迟队列队列所绑定的交换机
     */
    @Bean(name = "orderTtlDirect")
    @Qualifier(value = "orderTtlDirect")
    public DirectExchange orderTtlDirect() {
        Exchange exchange = ExchangeBuilder.directExchange(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getExchange())
                .durable(true)
                .delayed()
                .build();
        return (DirectExchange) exchange;
    }

    /**
     * 订单实际消费队列
     */
    @Bean(name = "orderQueue")
    @Qualifier(value = "orderQueue")
    public Queue orderQueue() {
        return new Queue(QueueEnum.QUEUE_ORDER_CANCEL.getName());
    }

    /**
     * 订单延迟队列（死信队列）
     * 到期后转发的交换机
     * 到期后转发的路由键
     */
    @Bean(name = "orderTtlQueue")
    @Qualifier(value = "orderTtlQueue")
    public Queue orderTtlQueue() {
        return QueueBuilder.durable(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getName())
                .withArgument("x-dead-letter-exchange", QueueEnum.QUEUE_ORDER_CANCEL.getExchange()) //到期后转发的交换机
                .withArgument("x-dead-letter-routing-key", QueueEnum.QUEUE_ORDER_CANCEL.getRouteKey()) //到期后转发的路由键
                .build();
    }

    /**
     * 将订单队列绑定到交换机
     */
    @Bean(name = "orderBinding")
    @Qualifier(value = "orderBinding")
    public Binding orderBinding(@Qualifier(value = "orderDirect") DirectExchange orderDirect,
                                @Qualifier(value = "orderQueue") Queue orderQueue) {
        return BindingBuilder.bind(orderQueue)
                .to(orderDirect)
                .with(QueueEnum.QUEUE_ORDER_CANCEL.getRouteKey());
    }

    /**
     * 将订单延迟队列绑定到交换机
     */
    @Bean(name = "orderTtlBinding")
    @Qualifier(value = "orderTtlBinding")
    public Binding orderTtlBinding(@Qualifier(value = "orderTtlDirect") DirectExchange orderTtlDirect,
                                   @Qualifier(value = "orderTtlQueue") Queue orderTtlQueue) {
        return BindingBuilder.bind(orderTtlQueue)
                .to(orderTtlDirect)
                .with(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getRouteKey());
    }
}
