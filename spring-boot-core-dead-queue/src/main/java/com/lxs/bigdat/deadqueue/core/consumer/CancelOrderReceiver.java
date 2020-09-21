package com.lxs.bigdat.deadqueue.core.consumer;

import com.lxs.bigdat.deadqueue.service.OmsPortalOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消费取消订单队列
 */
@Component
@Slf4j
@RabbitListener(queues = "mall.order.cancel")
public class CancelOrderReceiver {

    @Autowired
    private OmsPortalOrderService omsPortalOrderService;

    @RabbitHandler
    public void handle(Long orderId) {
        log.info("receive delay message OrderId:{}", orderId);
        if (orderId != null) {
            omsPortalOrderService.cancelOrder(orderId);
        }
    }
}
