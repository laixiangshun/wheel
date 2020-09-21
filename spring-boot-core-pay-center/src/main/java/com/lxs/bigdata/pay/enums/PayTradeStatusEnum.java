package com.lxs.bigdata.pay.enums;

import lombok.Getter;

/**
 * 支付结果枚举
 *
 * @author lxs
 */
@Getter
public enum PayTradeStatusEnum {

    /**
     * 成功
     */
    SUCCESS("0", "成功"),
    /**
     * 失败
     */
    FAIL("1", "失败");

    private String code;

    private String msg;

    PayTradeStatusEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
