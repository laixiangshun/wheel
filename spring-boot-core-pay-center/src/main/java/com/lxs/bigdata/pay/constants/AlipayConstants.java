/**
 * Copyright (c) 2015, 59store. All rights reserved.
 */
package com.lxs.bigdata.pay.constants;

/**
 * 支付宝常量.
 *
 * @author lxs
 */
public interface AlipayConstants {

    /**
     * 请求处理成功.
     */
    String SUCCESS = "success";

    /**
     * 请求处理失败.
     */
    String FAIL = "fail";

    /**
     * 支付宝新网关. 合作伙伴和无线产品都用这个 见https://b.alipay.com/order/pidAndKey.htm
     */
    String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do";
//    String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do";

    /**
     * 支付宝网关. 开放平台 见https://b.alipay.com/order/pidAndKey.htm
     */
    String ALIPAY_GATEWAY_OPEN = "https://openapi.alipay.com/gateway.do";

    /**
     * 支付宝返回码 - 成功.
     */
    String RESPONSE_CODE_SUCCESS = "10000";

    /**
     * 支付宝消息验证地址.
     */
    String ALIPAY_VERIFY_URL = "https://mapi.alipay.com/gateway.do?service=notify_verify&";

    String CHARSET = "UTF-8";

    /**
     * 设置支付宝默认交易超时时间
     */
    String PRECREATE_TIMEOUT = "120m";

    /**
     * 是否需要重试
     */
    String RETRY_FLAY = "Y";

    /**
     * 退款查询状态
     */
    String REFUND_SUCCESS = "REFUND_SUCCESS";
}
