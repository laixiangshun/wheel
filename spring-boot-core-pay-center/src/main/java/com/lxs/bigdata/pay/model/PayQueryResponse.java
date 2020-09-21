package com.lxs.bigdata.pay.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询支付交易返回response
 *
 * @author lxs
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "PayQueryResponse", description = "查询支付交易返回response")
public class PayQueryResponse {

    /**
     * 支付平台交易号
     */
    @ApiModelProperty(value = "tradeNo", name = "支付平台交易号", notes = "支付平台交易号")
    private String tradeNo;

    /**
     * 支付时传入的商户订单号
     */
    @ApiModelProperty(value = "outTradeNo", name = "支付时传入的商户订单号", notes = "支付时传入的商户订单号")
    private String outTradeNo;

    /**
     * 交易当前状态
     */
    @ApiModelProperty(value = "tradeStatus", name = "交易当前状态", notes = "交易当前状态")
    private String tradeStatus;
}
