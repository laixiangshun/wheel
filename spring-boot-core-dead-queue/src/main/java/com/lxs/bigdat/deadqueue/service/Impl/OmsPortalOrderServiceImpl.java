package com.lxs.bigdat.deadqueue.service.Impl;

import com.lxs.bigdat.deadqueue.common.CommonResult;
import com.lxs.bigdat.deadqueue.core.producer.CancelOrderSender;
import com.lxs.bigdat.deadqueue.dto.OrderParam;
import com.lxs.bigdat.deadqueue.service.OmsPortalOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OmsPortalOrderServiceImpl implements OmsPortalOrderService {

    @Autowired
    private CancelOrderSender cancelOrderSender;

    @Override
    public CommonResult generateOrder(OrderParam orderParam) {
        //todo 执行一系类下单操作,返回生成的orderId
        log.info("process generateOrder");
        sendDelayMessageCancelOrder(11L);
        return CommonResult.success(null, "下单成功");
    }

    @Override
    public CommonResult cancelOrder(Long orderId) {
        //todo 执行一系类取消订单操作
        log.info("process cancelOrder orderId: {}", orderId);
        return CommonResult.success(null, "取消订单成功");
    }

    private void sendDelayMessageCancelOrder(Long orderId) {
        //订单未支付有效时间30分钟
        long delayTimes = 30 * 60 * 1000;
        cancelOrderSender.sendMessage(orderId, delayTimes);
    }
}
