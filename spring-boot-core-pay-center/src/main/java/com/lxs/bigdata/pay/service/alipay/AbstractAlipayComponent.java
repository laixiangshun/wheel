package com.lxs.bigdata.pay.service.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.RefundRoyaltyResult;
import com.alipay.api.request.AlipayTradeCancelRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.lxs.bigdata.pay.constants.AlipayConstants;
import com.lxs.bigdata.pay.enums.BestPayResultEnum;
import com.lxs.bigdata.pay.enums.PayRefundStatusEnum;
import com.lxs.bigdata.pay.enums.PayTradeStatusEnum;
import com.lxs.bigdata.pay.exception.AliPayException;
import com.lxs.bigdata.pay.exception.BestPayException;
import com.lxs.bigdata.pay.model.*;
import com.lxs.bigdata.pay.model.alipay.AlipayCancelRequest;
import com.lxs.bigdata.pay.model.alipay.AlipayQueryRequest;
import com.lxs.bigdata.pay.model.alipay.AlipayRefundQueryRequest;
import com.lxs.bigdata.pay.model.alipay.AlipayRefundRequest;
import com.lxs.bigdata.pay.service.AbstractComponent;
import com.lxs.bigdata.pay.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import static com.lxs.bigdata.pay.constants.AlipayConstants.RESPONSE_CODE_SUCCESS;
import static com.lxs.bigdata.pay.constants.AlipayConstants.RETRY_FLAY;

/**
 * 支付宝支付公共父类
 *
 * @author lxs
 */
@Slf4j
public abstract class AbstractAlipayComponent extends AbstractComponent {

    /**
     * 检测支付宝操作返回结果
     */
    boolean checkResponseCode(AlipayResponse alipayResponse) {
        String code = alipayResponse.getCode();
        if (!RESPONSE_CODE_SUCCESS.equals(code)) {
            throw new BestPayException(BestPayResultEnum.ALIPAY_TRADE_STATUS_IS_NOT_SUCCESS);
        }
        return true;
    }

    /**
     * 构造支付宝退款需要的业务参数
     */
    public AlipayRefundRequest buildRefundParam(RefundRequest request) {
        AlipayRefundRequest refundParam = new AlipayRefundRequest();
        refundParam.setTradeNo(request.getTradeNo());
        refundParam.setOutTradeNo(request.getOrderId());
        BigDecimal refundAmount = new BigDecimal(request.getOrderAmount());
        refundParam.setRefundAmount(refundAmount);
        refundParam.setRefundReason(request.getRefundReason());
        if (StringUtils.isNotBlank(request.getOutRefundNo())) {
            refundParam.setOutRequestNo(request.getOutRefundNo());
        }
        return refundParam;
    }

    /**
     * 构造支付宝查询交易需要的业务参数
     */
    public AlipayQueryRequest buildQueryParam(PayQueryRequest request) {
        AlipayQueryRequest refundParam = new AlipayQueryRequest();
        refundParam.setTradeNo(request.getTradeNo());
        refundParam.setOutTradeNo(request.getOutTradeNo());
        return refundParam;
    }

    /**
     * 构造支付宝取消交易需要的业务参数
     */
    public AlipayCancelRequest buildCancelParam(PayCancelRequest request) {
        AlipayCancelRequest refundParam = new AlipayCancelRequest();
        refundParam.setTradeNo(request.getTradeNo());
        refundParam.setOutTradeNo(request.getOutTradeNo());
        return refundParam;
    }

    /**
     * 构造支付宝取消交易需要的业务参数
     */
    public AlipayRefundQueryRequest buildRefundQueryParam(PayRefundQueryRequest request) {
        AlipayRefundQueryRequest refundParam = new AlipayRefundQueryRequest();
        refundParam.setTradeNo(request.getTradeNo());
        refundParam.setOutTradeNo(request.getOutTradeNo());
        if (StringUtils.isBlank(request.getOutRefundNo())) {
            //请求退款接口时，传入的退款请求号，如果在退款请求时未传入，则该值为创建交易时的外部交易号
            refundParam.setOutRefundNo(request.getOutTradeNo());
        }
        return refundParam;
    }

    /**
     * 支付宝统一退款
     */
    public RefundResponse refund(AlipayClient alipayClient, RefundRequest request) throws AlipayApiException {
        AlipayTradeRefundRequest refundRequest = new AlipayTradeRefundRequest();
        //填充业务参数
        Map<String, Object> bizParam = this.buildRefundParam(request).getBizParam();
        String json = JsonUtil.toJson(bizParam);
        refundRequest.setBizContent(json);
        AlipayTradeRefundResponse response = alipayClient.execute(refundRequest);
        RefundResponse.RefundResponseBuilder builder = RefundResponse.builder();
        boolean responseCode = checkResponseCode(response);
        if (responseCode) {
            BigDecimal refundAmount = new BigDecimal(response.getRefundFee()).setScale(2, RoundingMode.HALF_UP);
            builder.orderAmount(refundAmount.doubleValue());
        }
        builder.orderId(response.getOutTradeNo());
        builder.outTradeNo(response.getTradeNo());
        return builder.build();
    }

    /**
     * 支付宝退款查询
     */
    public PayRefundQueryResponse payRefundQuery(AlipayClient alipayClient,PayRefundQueryRequest refundQueryRequest) throws Exception {

        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        AlipayRefundQueryRequest refundQueryParam = this.buildRefundQueryParam(refundQueryRequest);
        request.setBizContent(JsonUtil.toJson(refundQueryParam));
        AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
        if (!checkResponseCode(response)) {
            throw new AliPayException(BestPayResultEnum.REFUND_QUERY_ERROR);
        }
        String refundStatus = response.getRefundStatus();
        List<RefundRoyaltyResult> refundRoyaltys = response.getRefundRoyaltys();
        PayRefundQueryResponse refundQueryResponse = PayRefundQueryResponse.builder()
                .outTradeNo(response.getOutTradeNo())
                .tradeNo(response.getTradeNo())
                .outRefundNo(response.getOutRequestNo())
                .refundId(response.getRefundSettlementId())
                .build();
        if (!CollectionUtils.isEmpty(refundRoyaltys)
                && (StringUtils.isBlank(refundStatus) || AlipayConstants.REFUND_SUCCESS.equalsIgnoreCase(refundStatus))) {
            refundQueryResponse.setRefundStatus(PayRefundStatusEnum.SUCCESS.getCode());
        } else {
            refundQueryResponse.setRefundStatus(PayRefundStatusEnum.PROCESSING.getCode());
        }
        String refundAmount = response.getRefundAmount();
        BigDecimal amount = new BigDecimal(refundAmount).setScale(2, RoundingMode.HALF_UP);
        refundQueryResponse.setRefundAmount(amount);
        return refundQueryResponse;
    }

    public PayQueryResponse payQuery(AlipayClient alipayClient,PayQueryRequest request) throws AlipayApiException {
        //创建API对应的request类
        AlipayTradeQueryRequest queryRequest = new AlipayTradeQueryRequest();
        //设置业务参数
        AlipayQueryRequest queryParam = this.buildQueryParam(request);
        queryRequest.setBizContent(JsonUtil.toJson(queryParam));
        //通过alipayClient调用API，获得对应的response类
        AlipayTradeQueryResponse response = alipayClient.execute(queryRequest);
        if (!this.checkResponseCode(response)) {
            throw new AliPayException(BestPayResultEnum.ALIPAY_QUERY_ERROR);
        }
        String tradeStatus = response.getTradeStatus();

        PayQueryResponse queryResponse = PayQueryResponse.builder()
                .outTradeNo(response.getOutTradeNo())
                .tradeNo(response.getTradeNo())
                .build();
        queryResponse.setTradeStatus(PayTradeStatusEnum.SUCCESS.getCode());
        if (AlipayConstants.FAIL.equalsIgnoreCase(tradeStatus)) {
            queryResponse.setTradeStatus(PayTradeStatusEnum.FAIL.getCode());
        }
        return queryResponse;
    }

    public PayCancelResponse payCancel(AlipayClient alipayClient,PayCancelRequest request) throws AlipayApiException {
        //创建API对应的request类
        AlipayTradeCancelRequest cancelRequest = new AlipayTradeCancelRequest();
        //设置业务参数
        AlipayCancelRequest cancelParam = this.buildCancelParam(request);
        cancelRequest.setBizContent(JsonUtil.toJson(cancelParam));
        //通过alipayClient调用API，获得对应的response类
        AlipayTradeCancelResponse response = alipayClient.execute(cancelRequest);
        if (!this.checkResponseCode(response)) {
            throw new AliPayException(BestPayResultEnum.ALIPAY_QUERY_ERROR);
        }
        String retryFlag = response.getRetryFlag();

        PayCancelResponse cancelResponse = PayCancelResponse.builder()
                .outTradeNo(response.getOutTradeNo())
                .tradeNo(response.getTradeNo())
                .build();
        cancelResponse.setResultStatus(PayTradeStatusEnum.SUCCESS.getCode());
        if (!RETRY_FLAY.equalsIgnoreCase(retryFlag)) {
            cancelResponse.setResultStatus(PayTradeStatusEnum.FAIL.getCode());
        }
        return cancelResponse;
    }
}
