package com.lxs.bigdata.pay.model.alipay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付查询request
 *
 * @author lxs
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "PayQueryRequest", description = "支付交易查询请求")
public class AlipayQueryRequest {

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

}
