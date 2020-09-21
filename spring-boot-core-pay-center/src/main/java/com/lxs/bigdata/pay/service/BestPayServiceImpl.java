package com.lxs.bigdata.pay.service;

import com.lxs.bigdata.pay.config.AlipayConfig;
import com.lxs.bigdata.pay.config.SignType;
import com.lxs.bigdata.pay.config.WxPayH5Config;
import com.lxs.bigdata.pay.constants.AlipayConstants;
import com.lxs.bigdata.pay.enums.BestPayResultEnum;
import com.lxs.bigdata.pay.enums.BestPayTypeEnum;
import com.lxs.bigdata.pay.exception.BestPayException;
import com.lxs.bigdata.pay.model.*;
import com.lxs.bigdata.pay.service.alipay.AlipaySignatureComponent;
import com.lxs.bigdata.pay.service.weixin.WxPayServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

public class BestPayServiceImpl implements BestPayService {

    private WxPayH5Config wxPayH5Config;

    private AlipayConfig alipayConfig;

    private AlipaySignatureComponent signature;

    public void setWxPayH5Config(WxPayH5Config wxPayH5Config) {
        this.wxPayH5Config = wxPayH5Config;
    }

    public BestPayServiceImpl(AlipayConfig alipayConfig, AlipaySignatureComponent signature) {
        this.alipayConfig = alipayConfig;
        this.signature = signature;
    }

    @Override
    public PayResponse pay(PayRequest request) {
        //微信h5支付
        WxPayServiceImpl wxPayService = new WxPayServiceImpl();
        wxPayService.setWxPayH5Config(this.wxPayH5Config);

        return wxPayService.pay(request);
    }

    /**
     * 同步返回
     *
     */
    @Override
    public PayResponse syncNotify(HttpServletRequest request) {
        return null;
    }

    @Override
    public boolean verify(Map<String, String> toBeVerifiedParamMap, SignType signType, String sign) {
        return false;
    }

    /**
     * 异步回调
     *
     */
    @Override
    public PayResponse asyncNotify(String notifyData) {

        //微信h5支付
        WxPayServiceImpl wxPayService = new WxPayServiceImpl();
        wxPayService.setWxPayH5Config(this.wxPayH5Config);

        return wxPayService.asyncNotify(notifyData);
    }

    /**
     * 判断是什么支付类型(从同步回调中获取参数)
     *
     */
    private BestPayTypeEnum payType(HttpServletRequest request) throws UnsupportedEncodingException {
        //先判断是微信还是支付宝 是否是xml
        //支付宝同步还是异步
        if (request.getParameter("notify_type") == null) {
            //支付宝同步
            if (request.getParameter("exterface") != null && request.getParameter("exterface").equals("create_direct_pay_by_user")) {
                return BestPayTypeEnum.ALIPAY_PC;
            }
            if (request.getParameter("method") != null && request.getParameter("method").equals("alipay.trade.wap.pay.return")) {
                return BestPayTypeEnum.ALIPAY_WAP;
            }
        } else {
            //支付宝异步(发起支付时使用这个参数标识支付方式)
            String payType = request.getParameter("passback_params");
            payType= URLDecoder.decode(payType, AlipayConstants.CHARSET);
            return BestPayTypeEnum.getByCode(payType);
        }

        throw new BestPayException(BestPayResultEnum.PAY_TYPE_ERROR);
    }

    @Override
    public RefundResponse refund(RefundRequest request) {
        //微信h5支付
        WxPayServiceImpl wxPayService = new WxPayServiceImpl();
        wxPayService.setWxPayH5Config(this.wxPayH5Config);
        return wxPayService.refund(request);
    }

    @Override
    public PayQueryResponse payQuery(PayQueryRequest request) throws Exception {
        return null;
    }

    @Override
    public PayCancelResponse payCancel(PayCancelRequest request) throws Exception {
        return null;
    }

    @Override
    public PayRefundQueryResponse payRefundQuery(PayRefundQueryRequest refundQueryRequest) throws Exception {
        return null;
    }
}