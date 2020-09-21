package com.lxs.bigdata.pay.exception;


import com.lxs.bigdata.pay.enums.BestPayResultEnum;

/**
 * 统一支付异常
 *
 * @author lxs
 */
public class BestPayException extends RuntimeException {

    private Integer code;

    public BestPayException(BestPayResultEnum resultEnum) {
        super(resultEnum.getMsg());
        code = resultEnum.getCode();
    }

    public Integer getCode() {
        return code;
    }
}
