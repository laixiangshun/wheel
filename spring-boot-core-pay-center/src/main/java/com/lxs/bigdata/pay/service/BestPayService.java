package com.lxs.bigdata.pay.service;

import com.lxs.bigdata.pay.config.SignType;
import com.lxs.bigdata.pay.model.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 支付相关
 *
 * @author lxs
 */
public interface BestPayService {

    /**
     * 发起支付.
     */
    PayResponse pay(PayRequest request) throws Exception;

    /**
     * 验证支付结果. 包括同步和异步.
     *
     * @param toBeVerifiedParamMap 待验证的支付结果参数.
     * @param signType             签名方式.
     * @param sign                 签名.
     * @return 验证结果.
     */
    boolean verify(Map<String, String> toBeVerifiedParamMap, SignType signType, String sign);

    /**
     * 同步回调
     */
    PayResponse syncNotify(HttpServletRequest request);

    /**
     * 异步回调
     */
    PayResponse asyncNotify(String notifyData) throws Exception;

    /**
     * 退款
     */
    RefundResponse refund(RefundRequest request) throws Exception;

    /**
     * 查询交易状态
     */
    PayQueryResponse payQuery(PayQueryRequest request) throws Exception;

    /**
     * 交易取消或关闭
     */
    PayCancelResponse payCancel(PayCancelRequest request) throws Exception;

    /**
     * 退款查询
     */
    PayRefundQueryResponse payRefundQuery(PayRefundQueryRequest refundQueryRequest) throws Exception;

}
