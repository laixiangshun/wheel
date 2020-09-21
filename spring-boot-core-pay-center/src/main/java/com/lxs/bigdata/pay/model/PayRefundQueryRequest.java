package com.lxs.bigdata.pay.model;

import com.lxs.bigdata.pay.enums.BestPayTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 查询退款请求 request
 *
 * @author lxs
 */
@Data
@Builder
@ApiModel(value = "PayRefundQueryRequest", description = "查询退款请求")
public class PayRefundQueryRequest {

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
     * 支付方式.
     */
    @ApiModelProperty(value = "payTypeEnum", name = "支付方式", notes = "支付方式")
    private BestPayTypeEnum payTypeEnum;

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
}
