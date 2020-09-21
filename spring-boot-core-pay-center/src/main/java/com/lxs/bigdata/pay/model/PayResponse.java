package com.lxs.bigdata.pay.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

/**
 * 支付时的同步返回参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayResponse {

    private String prePayParams;

    private URI payUri;

    /**
     * 支付宝为开发者生成前台页面请求需要的完整form表单的html（包含自动提交脚本），商户直接将这个表单的String输出到http response中即可
     */
    private String form;

    /**
     * 支付宝扫码付款返回二维码
     */
    private String qrCode;

    /**
     * 以下字段仅在微信h5支付返回.
     */
    private String appId;

    private String timeStamp;

    private String nonceStr;

    @JsonProperty("package")
    private String packAge;

    private String signType;

    private String paySign;

    /**
     * 以下字段在微信异步通知下返回.
     */
    private Double orderAmount;

    private String orderId;

    //第三方支付的流水号
    private String outTradeNo;

    /**
     * 以下支付是h5支付返回
     */
    private String mwebUrl;
}
