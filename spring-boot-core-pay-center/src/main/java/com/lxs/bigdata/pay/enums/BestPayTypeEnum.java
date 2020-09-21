package com.lxs.bigdata.pay.enums;


import com.lxs.bigdata.pay.exception.BestPayException;

/**
 * 支付方式
 *
 * @author lxs
 */
public enum BestPayTypeEnum {

    /**
     * 支付宝app
     */
    ALIPAY_APP("alipay_app", "alipay", "支付宝app"),

    /**
     * 支付宝pc
     */
    ALIPAY_PC("alipay_pc", "alipay", "支付宝pc"),

    /**
     * 支付宝wap
     */
    ALIPAY_WAP("alipay_wap", "alipay", "支付宝wap"),

    /**
     * 支付宝扫码支付
     */
    ALIPAY_PRECREATE("alipay_precreate", "alipay", "支付宝扫码支付"),

    /**
     * 微信H5支付
     */
    WXPAY_H5("wxpay_h5", "weixin", "微信H5支付"),

    /**
     * 微信公众账号支付
     */
    WXPAY_MWEB("MWEB", "weixin", "微信公众账号支付"),
    ;

    private String code;

    private String type;

    private String name;

    BestPayTypeEnum(String code, String type, String name) {
        this.code = code;
        this.name = name;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public static BestPayTypeEnum getByCode(String code) {
        for (BestPayTypeEnum bestPayTypeEnum : BestPayTypeEnum.values()) {
            if (bestPayTypeEnum.getCode().equals(code)) {
                return bestPayTypeEnum;
            }
        }
        throw new BestPayException(BestPayResultEnum.PAY_TYPE_ERROR);
    }
}
