package com.lxs.bigdata.pay.exception;


import com.alipay.api.AlipayApiException;
import com.lxs.bigdata.pay.enums.BestPayResultEnum;

/**
 * 支付宝统一支付异常
 *
 * @author lxs
 */
public class AliPayException extends AlipayApiException {

    private Integer code;

    public AliPayException(BestPayResultEnum resultEnum) {
        super(resultEnum.getMsg());
        code = resultEnum.getCode();
    }

    public Integer getCode() {
        return code;
    }
}
