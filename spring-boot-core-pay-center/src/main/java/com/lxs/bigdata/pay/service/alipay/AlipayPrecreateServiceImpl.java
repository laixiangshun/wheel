package com.lxs.bigdata.pay.service.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.lxs.bigdata.pay.config.AlipayConfig;
import com.lxs.bigdata.pay.config.SignType;
import com.lxs.bigdata.pay.constants.AlipayConstants;
import com.lxs.bigdata.pay.enums.BestPayTypeEnum;
import com.lxs.bigdata.pay.model.*;
import com.lxs.bigdata.pay.model.alipay.AlipayBizRequest;
import com.lxs.bigdata.pay.service.BestPayService;
import com.lxs.bigdata.pay.utils.JsonUtil;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;


/**
 * 支付宝当面付扫码支付
 *
 * @author lxs
 */
@Slf4j
@Builder
public class AlipayPrecreateServiceImpl extends AbstractAlipayComponent implements BestPayService {

    private AlipayConfig alipayConfig;

    private AlipaySignatureComponent signature;

    public AlipayPrecreateServiceImpl(AlipayConfig alipayConfig, AlipaySignatureComponent signature) {
        Objects.requireNonNull(alipayConfig, "aliPayConfig is null.");
        this.alipayConfig = alipayConfig;
        Objects.requireNonNull(signature, "signature is null.");
        this.signature = signature;
    }

    @Override
    protected String getPayTypeCode() {
        return BestPayTypeEnum.ALIPAY_PRECREATE.getCode();
    }

    @Override
    public PayResponse pay(PayRequest request) throws AlipayApiException {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConstants.ALIPAY_GATEWAY_OPEN,
                alipayConfig.getAppId(), alipayConfig.getAppRSAPrivateKey(),
                "json", AlipayConstants.CHARSET, alipayConfig.getAlipayRSAPublicKey(),
                alipayConfig.getSignType().toString());
        //创建API对应的request类
        AlipayTradePrecreateRequest tradePrecreateRequest = new AlipayTradePrecreateRequest();
        //填充业务参数
        Map<String, String> bizParam = this.buildParam(request).getBizParam();
        String json = JsonUtil.toJson(bizParam);
        tradePrecreateRequest.setBizContent(json);
        AlipayTradePrecreateResponse response = alipayClient.execute(tradePrecreateRequest);
        PayResponse payResponse = PayResponse.builder()
                .qrCode(response.getQrCode())
                .build();
        payResponse.setOutTradeNo(response.getOutTradeNo());
        return payResponse;
    }

    /**
     * 构造支付宝需要的业务参数
     */
    private AlipayBizRequest buildParam(PayRequest request) {
        AlipayBizRequest alipayBizParam = new AlipayBizRequest();
        alipayBizParam.setSubject(request.getOrderName());
        alipayBizParam.setOutTradeNo(request.getOrderId());
        alipayBizParam.setTotalAmount(String.valueOf(request.getOrderAmount()));
        alipayBizParam.setBody(request.getOrderName());
        alipayBizParam.setStoreId(request.getStoreId());
        //订单允许的最晚付款时间
        String timeoutExpress = StringUtils.isBlank(request.getTimeoutExpress()) ? AlipayConstants.PRECREATE_TIMEOUT : request.getTimeoutExpress();
        alipayBizParam.setTimeoutExpress(timeoutExpress);
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
    public PayResponse asyncNotify(String notifyData) {
        return null;
    }


    @Override
    public PayQueryResponse payQuery(PayQueryRequest request) throws AlipayApiException {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConstants.ALIPAY_GATEWAY_OPEN,
                alipayConfig.getAppId(), alipayConfig.getAppRSAPrivateKey(),
                "json", AlipayConstants.CHARSET, alipayConfig.getAlipayRSAPublicKey(),
                alipayConfig.getSignType().toString());
        return this.payQuery(alipayClient, request);
    }

    @Override
    public PayCancelResponse payCancel(PayCancelRequest request) throws AlipayApiException {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConstants.ALIPAY_GATEWAY_OPEN,
                alipayConfig.getAppId(), alipayConfig.getAppRSAPrivateKey(),
                "json", AlipayConstants.CHARSET, alipayConfig.getAlipayRSAPublicKey(),
                alipayConfig.getSignType().toString());
        return this.payCancel(alipayClient, request);
    }

    @Override
    public PayRefundQueryResponse payRefundQuery(PayRefundQueryRequest refundQueryRequest) throws Exception {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConstants.ALIPAY_GATEWAY_OPEN,
                alipayConfig.getAppId(), alipayConfig.getAppRSAPrivateKey(),
                "json", AlipayConstants.CHARSET, alipayConfig.getAlipayRSAPublicKey(),
                alipayConfig.getSignType().toString());
        return this.payRefundQuery(alipayClient, refundQueryRequest);
    }


    @Override
    public RefundResponse refund(RefundRequest request) throws Exception {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConstants.ALIPAY_GATEWAY_OPEN,
                alipayConfig.getAppId(), alipayConfig.getAppRSAPrivateKey(),
                "json", AlipayConstants.CHARSET, alipayConfig.getAlipayRSAPublicKey(),
                alipayConfig.getSignType().toString());
        return this.refund(alipayClient, request);
    }
}
