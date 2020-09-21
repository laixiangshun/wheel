package com.lxs.bigdata.pay.exception;


import com.lxs.bigdata.pay.enums.BestPayResultEnum;

/**
 * 微信统一支付异常
 *
 * @author lxs
 */
public class WxPayException extends RuntimeException {

    private Integer code;

    public WxPayException(BestPayResultEnum resultEnum) {
        super(resultEnum.getMsg());
        code = resultEnum.getCode();
    }

    public WxPayException(String msg) {
        super(msg);
        this.code = BestPayResultEnum.WXPAY_TRADE_ERROR.getCode();
    }

    public Integer getCode() {
        return code;
    }
}
