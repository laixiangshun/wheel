package com.lxs.bigdata.pay.enums;

/**
 * @author lxs
 */
public enum BestPayResultEnum {

    /**
     * 未知异常
     */
    UNKNOW_ERROR(-1, "未知异常"),

    /**
     * 成功
     */
    SUCCESS(0, "成功"),
    /**
     * 参数错误
     */
    PARAM_ERROR(1, "参数错误"),
    /**
     * 配置错误, 请检查是否漏了配置项
     */
    CONFIG_ERROR(2, "配置错误, 请检查是否漏了配置项"),
    /**
     * 支付宝web端支付验证签名】验证notifyId失败
     */
    ALIPAY_NOTIFY_ID_VERIFY_FAIL(10, "【支付宝web端支付验证签名】验证notifyId失败"),
    /**
     * 支付宝web端支付同步返回验证签名】验证签名失败
     */
    ALIPAY_ASYNC_SIGN_VERIFY_FAIL(11, "【支付宝web端支付同步返回验证签名】验证签名失败"),
    /**
     * 同步返回签名失败
     */
    SYNC_SIGN_VERIFY_FAIL(12, "同步返回签名失败"),
    /**
     * 异步返回签名失败
     */
    ASYNC_SIGN_VERIFY_FAIL(13, "异步返回签名失败"),
    /**
     * 错误的支付方式
     */
    PAY_TYPE_ERROR(14, "错误的支付方式"),
    /**
     * 支付宝交易状态不是成功
     */
    ALIPAY_TRADE_STATUS_IS_NOT_SUCCESS(15, "支付宝交易状态不是成功"),
    /**
     * 付宝返回的时间格式不对
     */
    ALIPAY_TIME_FORMAT_ERROR(16, "支付宝返回的时间格式不对"),
    /**
     * 微信支付错误
     */
    WXPAY_TRADE_ERROR(16, "微信支付错误"),
    /**
     * 微信退款错误
     */
    WXPAY_REFUND_ERROR(17, "微信退款错误"),

    /**
     * 支付宝支付查询错误
     */
    ALIPAY_QUERY_ERROR(18, "支付宝支付查询错误"),
    /**
     * 支付宝支付取消错误
     */
    ALIPAY_CANCEL_ERROR(19, "支付宝支付取消错误"),

    /**
     * 退款查询错误
     */
    REFUND_QUERY_ERROR(20, "退款查询错误"),

    /**
     * 支付宝异步通知签名算法类型不匹配
     */
    ALIPAY_SIGN_TYPE_ERROR(21,"支付宝异步通知签名算法类型不匹配"),
    ;

    private Integer code;

    private String msg;

    BestPayResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
