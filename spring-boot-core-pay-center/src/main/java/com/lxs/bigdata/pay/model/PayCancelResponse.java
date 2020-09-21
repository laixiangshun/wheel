package com.lxs.bigdata.pay.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 支付关闭或取消返回
 *
 * @author lxs
 */
@Data
@Builder
@ApiModel(value = "PayCancelResponse", description = "支付关闭或取消返回")
public class PayCancelResponse {

    /**
     * 商户订单号
     * 微信支付时必传
     */
    @ApiModelProperty(value = "outTradeNo", name = "商户订单号", notes = "商户订单号")
    private String outTradeNo;

    /**
     * 交易号
     * 支付宝交易可传
     */
    @ApiModelProperty(value = "tradeNo", name = "交易号", notes = "交易号")
    private String tradeNo;

    /**
     * 返回结果状态
     */
    @ApiModelProperty(value = "resultStatus", name = "返回结果状态", notes = "返回结果状态")
    private String resultStatus;
}
