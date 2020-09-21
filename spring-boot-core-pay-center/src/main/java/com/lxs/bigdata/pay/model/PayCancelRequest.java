package com.lxs.bigdata.pay.model;

import com.lxs.bigdata.pay.enums.BestPayTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 支付关闭或取消
 *
 * @author lxs
 */
@Data
@Builder
@ApiModel(value = "PayCancelRequest", description = "支付关闭或取消")
public class PayCancelRequest {

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
     * 支付方式.
     */
    @ApiModelProperty(value = "payTypeEnum",name = "支付方式",notes = "支付方式")
    private BestPayTypeEnum payTypeEnum;
}
