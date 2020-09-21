package com.lxs.bigdata.pay.model;

import com.lxs.bigdata.pay.enums.BestPayTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 支付查询request
 *
 * @author lxs
 */
@Data
@Builder
@ApiModel(value = "PayQueryRequest", description = "支付交易查询请求")
public class PayQueryRequest {

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
    @ApiModelProperty(value = "payTypeEnum",name = "支付方式",notes = "支付方式")
    private BestPayTypeEnum payTypeEnum;
}
