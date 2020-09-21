package com.lxs.bigdata.pay.service.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.lxs.bigdata.pay.config.AlipayConfig;
import com.lxs.bigdata.pay.config.SignType;
import com.lxs.bigdata.pay.constants.AlipayConstants;
import com.lxs.bigdata.pay.enums.BestPayTypeEnum;
import com.lxs.bigdata.pay.model.*;
import com.lxs.bigdata.pay.model.alipay.AlipayBizRequest;
import com.lxs.bigdata.pay.service.BestPayService;
import com.lxs.bigdata.pay.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 支付宝Wap端支付
 *
 * @author lxs
 * https://doc.open.alipay.com/doc2/detail.htm?treeId=203&articleId=105463&docType=1
 */
@Slf4j
public class AlipayWapServiceImpl extends AbstractAlipayComponent implements BestPayService {

    private AlipayConfig alipayConfig;
    private AlipaySignatureComponent signature;

    public AlipayWapServiceImpl(AlipayConfig alipayConfig, AlipaySignatureComponent signature) {
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
    public PayResponse pay(PayRequest request) {
        log.info("【支付宝Wap端支付】request={}", JsonUtil.toJson(request));

        /* 1. 封装参数 */
        SortedMap<String, String> commonParamMap = new TreeMap<>();
        commonParamMap.put("app_id", this.alipayConfig.getAppId());
        commonParamMap.put("method", "alipay.trade.wap.pay");
        commonParamMap.put("format", "JSON");
        commonParamMap.put("return_url", this.alipayConfig.getReturnUrl());
        commonParamMap.put("charset", this.alipayConfig.getInputCharset());
        commonParamMap.put("sign_type", this.alipayConfig.getSignType().name());
        commonParamMap.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        commonParamMap.put("version", "1.0");
        commonParamMap.put("notify_url", this.alipayConfig.getNotifyUrl());
        commonParamMap.put("biz_content", JsonUtil.toJson(this.buildParam(request).getBizParam()));

        /* 2. 签名 */
        String sign = this.signature.sign(commonParamMap);
        commonParamMap.put("sign", sign);

        /* 3. 构造支付url */
        String payUrl;
        try {
            payUrl = new URIBuilder(AlipayConstants.ALIPAY_GATEWAY_OPEN).setParameters(
                    commonParamMap.entrySet().stream().filter(e -> {
                        String k = e.getKey();
                        String v = e.getValue();
                        return !(StringUtils.isBlank(k) || StringUtils.isBlank(v));
                    }).map(e -> {
                        String k = e.getKey();
                        String v = e.getValue();
                        return new BasicNameValuePair(k, v);
                    }).collect(Collectors.toList())).toString();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("illegal pay url.", e);
        }

        /* 4. 返回签名结果 */
        URI payUri = URI.create(payUrl);
        PayResponse response = PayResponse.builder().payUri(payUri).build();
        log.info("【支付宝Wap端支付】response={}", JsonUtil.toJson(response));
        return response;
    }

    @Override
    public boolean verify(Map<String, String> toBeVerifiedParamMap, SignType signType, String sign) {
        return this.signature.verify(toBeVerifiedParamMap, signType, sign);
    }

    @Override
    public PayResponse asyncNotify(String notifyData) {
        return null;
    }

    @Override
    public PayResponse syncNotify(HttpServletRequest request) {
        return null;
    }

    /**
     * 构造支付宝需要的业务参数
     */
    private AlipayBizRequest buildParam(PayRequest request) {
        AlipayBizRequest alipayBizParam = new AlipayBizRequest();
        alipayBizParam.setSubject(request.getOrderName());
        alipayBizParam.setOutTradeNo(request.getOrderId());
        alipayBizParam.setTotalAmount(String.valueOf(request.getOrderAmount()));
        alipayBizParam.setProductCode("QUICK_WAP_PAY");
        return alipayBizParam;
    }

    @Override
    public RefundResponse refund(RefundRequest request) throws AlipayApiException {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConstants.ALIPAY_GATEWAY_OPEN,
                alipayConfig.getAppId(), alipayConfig.getAppRSAPrivateKey(),
                "json", AlipayConstants.CHARSET, alipayConfig.getAlipayRSAPublicKey(),
                alipayConfig.getSignType().toString());
        return this.refund(alipayClient, request);
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
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConstants.ALIPAY_GATEWAY_OPEN,
                alipayConfig.getAppId(), alipayConfig.getAppRSAPrivateKey(),
                "json", AlipayConstants.CHARSET, alipayConfig.getAlipayRSAPublicKey(),
                alipayConfig.getSignType().toString());
        return this.payRefundQuery(alipayClient, refundQueryRequest);
    }
}
