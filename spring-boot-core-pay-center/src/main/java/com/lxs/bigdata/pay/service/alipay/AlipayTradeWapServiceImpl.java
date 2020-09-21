package com.lxs.bigdata.pay.service.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.lxs.bigdata.pay.config.AlipayConfig;
import com.lxs.bigdata.pay.config.SignType;
import com.lxs.bigdata.pay.constants.AlipayConstants;
import com.lxs.bigdata.pay.enums.BestPayResultEnum;
import com.lxs.bigdata.pay.enums.BestPayTypeEnum;
import com.lxs.bigdata.pay.exception.AliPayException;
import com.lxs.bigdata.pay.model.*;
import com.lxs.bigdata.pay.model.alipay.AlipayBizRequest;
import com.lxs.bigdata.pay.service.BestPayService;
import com.lxs.bigdata.pay.utils.JsonUtil;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.lxs.bigdata.pay.constants.AlipayConstants.CHARSET;


/**
 * 支付宝手机网站支付
 *
 * @author lxs
 */
@Slf4j
@Builder
public class AlipayTradeWapServiceImpl extends AbstractAlipayComponent implements BestPayService {

    private AlipayConfig alipayConfig;

    private AlipaySignatureComponent signature;

    public AlipayTradeWapServiceImpl(AlipayConfig alipayConfig, AlipaySignatureComponent signature) {
        Objects.requireNonNull(alipayConfig, "alipayConfig is null.");
        this.alipayConfig = alipayConfig;
        Objects.requireNonNull(signature, "signature is null.");
        this.signature = signature;
    }

    @Override
    protected String getPayTypeCode() {
        return BestPayTypeEnum.ALIPAY_WAP.getCode();
    }

    @Override
    public PayResponse pay(PayRequest request) throws Exception {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConstants.ALIPAY_GATEWAY_OPEN,
                alipayConfig.getAppId(), alipayConfig.getAppRSAPrivateKey(),
                "json", CHARSET, alipayConfig.getAlipayRSAPublicKey(),
                alipayConfig.getSignType().toString());
        //创建API对应的request
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
        //在公共参数中设置回跳和通知地址
        alipayRequest.setReturnUrl(alipayConfig.getReturnUrl());
        alipayRequest.setNotifyUrl(alipayConfig.getNotifyUrl());
        //填充业务参数
        String json = JsonUtil.toJson(this.buildParam(request).getBizParam());
        alipayRequest.setBizContent(json);
        AlipayTradeWapPayResponse response = alipayClient.pageExecute(alipayRequest);
        //调用SDK生成表单
        String form = response.getBody();
        PayResponse payResponse = PayResponse.builder()
                .form(form)
                .build();
        payResponse.setOrderId(response.getOutTradeNo());
        payResponse.setOutTradeNo(response.getTradeNo());
        String totalAmount = response.getTotalAmount();
        BigDecimal amount = new BigDecimal(totalAmount).setScale(2, RoundingMode.HALF_UP);
        payResponse.setOrderAmount(amount.doubleValue());
        return payResponse;
    }

    /**
     * 构造支付宝需要的业务参数
     */
    private AlipayBizRequest buildParam(PayRequest request) throws UnsupportedEncodingException {
        AlipayBizRequest alipayBizParam = new AlipayBizRequest();
        alipayBizParam.setSubject(request.getOrderName());
        alipayBizParam.setOutTradeNo(request.getOrderId());
        alipayBizParam.setTotalAmount(String.valueOf(request.getOrderAmount()));
        alipayBizParam.setProductCode("QUICK_WAP_PAY");
        String passBackParam = URLEncoder.encode(request.getPayTypeEnum().getCode(), CHARSET);
        alipayBizParam.setPassbackParams(passBackParam);
        return alipayBizParam;
    }

    @Override
    public boolean verify(Map<String, String> toBeVerifiedParamMap, SignType signType, String sign) {
        return signature.verify(toBeVerifiedParamMap, signType, sign);
    }

    @Override
    public PayResponse syncNotify(HttpServletRequest request) {
        return null;
    }

    @Override
    public PayResponse asyncNotify(String notifyData) throws AlipayApiException {
        Map<String, String> paramMap = JsonUtil.toObject(notifyData, HashMap.class);
        String type = this.alipayConfig.getSignType().toString();
        String signType = paramMap.get("sign_type");
        if (!type.equals(signType)) {
            throw new AliPayException(BestPayResultEnum.ALIPAY_SIGN_TYPE_ERROR);
        }
        //调用SDK验证签名
        boolean signVerified = AlipaySignature.rsaCheckV2(paramMap, alipayConfig.getAlipayRSAPublicKey(), CHARSET, signType);
        if (signVerified) {
            // TODO 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，校验成功后在response中返回success并继续商户自身业务处理，
            //  校验失败返回failure
        } else {
            // TODO 验签失败则记录异常日志，并在response中返回failure.
        }
        return null;
    }

    @Override
    public RefundResponse refund(RefundRequest request) throws AlipayApiException {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConstants.ALIPAY_GATEWAY_OPEN,
                alipayConfig.getAppId(), alipayConfig.getAppRSAPrivateKey(),
                "json", CHARSET, alipayConfig.getAlipayRSAPublicKey(),
                alipayConfig.getSignType().toString());
        return this.refund(alipayClient, request);
    }

    @Override
    public PayQueryResponse payQuery(PayQueryRequest request) throws AlipayApiException {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConstants.ALIPAY_GATEWAY_OPEN,
                alipayConfig.getAppId(), alipayConfig.getAppRSAPrivateKey(),
                "json", CHARSET, alipayConfig.getAlipayRSAPublicKey(),
                alipayConfig.getSignType().toString());
        return this.payQuery(alipayClient, request);
    }

    @Override
    public PayCancelResponse payCancel(PayCancelRequest request) throws AlipayApiException {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConstants.ALIPAY_GATEWAY_OPEN,
                alipayConfig.getAppId(), alipayConfig.getAppRSAPrivateKey(),
                "json", CHARSET, alipayConfig.getAlipayRSAPublicKey(),
                alipayConfig.getSignType().toString());
        return this.payCancel(alipayClient, request);
    }

    @Override
    public PayRefundQueryResponse payRefundQuery(PayRefundQueryRequest refundQueryRequest) throws Exception {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConstants.ALIPAY_GATEWAY_OPEN,
                alipayConfig.getAppId(), alipayConfig.getAppRSAPrivateKey(),
                "json", CHARSET, alipayConfig.getAlipayRSAPublicKey(),
                alipayConfig.getSignType().toString());
        return this.payRefundQuery(alipayClient, refundQueryRequest);
    }
}
