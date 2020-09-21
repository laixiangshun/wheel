package com.lxs.bigdata.pay.enums;

import lombok.Getter;

/**
 * 退款状态枚举
 *
 * @author lxs
 */
@Getter
public enum PayRefundStatusEnum {

    /**
     * 成功
     */
    SUCCESS("0", "成功"),
    /**
     * 退款关闭
     */
    REFUNDCLOSE("1", "退款关闭"),
    /**
     * 退款处理中
     */
    PROCESSING("2", "退款处理中"),
    /**
     * 退款异常
     */
    CHANGE("3", "退款异常");

    private String code;

    private String msg;

    PayRefundStatusEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
