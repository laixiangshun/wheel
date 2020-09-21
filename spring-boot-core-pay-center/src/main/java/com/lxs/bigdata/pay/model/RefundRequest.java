package com.lxs.bigdata.pay.model;

import com.lxs.bigdata.pay.enums.BestPayTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 支付时请求参数
 */
@Data
@ApiModel(value = "RefundRequest", description = "退款请求")
public class RefundRequest {

    /**
     * 支付方式.
     */
    @ApiModelProperty(value = "payTypeEnum", name = "支付方式", notes = "支付方式")
    private BestPayTypeEnum payTypeEnum;

    /**
     * 商户订单号.
     */
    @ApiModelProperty(value = "orderId", name = "商户订单号", notes = "商户订单号")
    private String orderId;

    /**
     * 支付平台交易号，和商户订单号不能同时为空
     */
    @ApiModelProperty(value = "tradeNo", name = "支付平台交易号", notes = "支付平台交易号")
    private String tradeNo;

    /**
     * 订单金额.
     * 小数点后两位
     */
    @ApiModelProperty(value = "orderAmount", name = "订单金额", notes = "订单金额")
    private Double orderAmount;

    /**
     * 退款原因
     */
    @ApiModelProperty(value = "refundReason", name = "退款原因", notes = "退款原因")
    private String refundReason;

    /**
     * 商户退款单号
     */
    @ApiModelProperty(value = "outRefundNo", name = "商户退款单号", notes = "商户退款单号")
    private String outRefundNo;
}
