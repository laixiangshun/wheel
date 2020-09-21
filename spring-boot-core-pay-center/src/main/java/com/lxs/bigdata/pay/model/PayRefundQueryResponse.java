package com.lxs.bigdata.pay.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 查询退款返回 response
 *
 * @author lxs
 */
@Data
@Builder
@ApiModel(value = "PayRefundQueryResponse", description = "查询退款返回")
public class PayRefundQueryResponse {

    /**
     * 商户订单号
     * 与 trade_no 必填一个
     */
    @ApiModelProperty(value = "outTradeNo", name = "商户订单号", notes = "商户订单号")
    private String outTradeNo;

    /**
     * 支付平台交易号
     * 与 out_trade_no 必填一个
     */
    @ApiModelProperty(value = "tradeNo", name = "支付平台交易号", notes = "支付平台交易号")
    private String tradeNo;

    /**
     * 商户退款单号
     */
    @ApiModelProperty(value = "outRefundNo", name = "商户退款单号", notes = "商户退款单号")
    private String outRefundNo;

    /**
     * 退款单号
     * 微信支持，申请退款接口有返回
     */
    @ApiModelProperty(value = "refundId", name = "退款单号", notes = "退款单号")
    private String refundId;

    /**
     * 退款状态
     */
    @ApiModelProperty(value = "refundStatus", name = "退款状态", notes = "退款状态")
    private String refundStatus;

    /**
     * 退款金额
     */
    @ApiModelProperty(value = "refundAmount", name = "退款金额", notes = "退款金额")
    private BigDecimal refundAmount;
}
