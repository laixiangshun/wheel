package com.lxs.bigdat.deadqueue.service;

import com.lxs.bigdat.deadqueue.common.CommonResult;
import com.lxs.bigdat.deadqueue.dto.OrderParam;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单管理service
 */
public interface OmsPortalOrderService {

    /**
     * 根据提交信息生成订单
     */
    @Transactional
    CommonResult generateOrder(OrderParam orderParam);

    /**
     * 取消单个超时订单
     */
    @Transactional
    CommonResult cancelOrder(Long orderId);
}
