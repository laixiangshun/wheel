package com.lxs.bigdata.pay.model;

import com.lxs.bigdata.pay.enums.BestPayTypeEnum;
import lombok.Data;

/**
 * 支付时请求参数
 */
@Data
public class PayRequest {

    /**
     * 支付方式.
     */
    private BestPayTypeEnum payTypeEnum;

    /**
     * 订单号.
     */
    private String orderId;

    /**
     * 订单金额.
     */
    private Double orderAmount;

    /**
     * 订单名字.
     */
    private String orderName;

    /**
     * 微信openid, 仅微信支付时需要
     */
    private String openid;

    /**
     * 客户端访问Ip  外部H5支付时必传，需要真实Ip
     */
    private String spbillCreateIp;

    /**
     * 支付宝：商户门店编号
     * 在支付宝选择扫码付时传递
     */
    private String storeId;

    /**
     * 支付宝交易超时时间
     * 在支付宝选择扫码付时传递，默认
     */
    private String timeoutExpress;
}
