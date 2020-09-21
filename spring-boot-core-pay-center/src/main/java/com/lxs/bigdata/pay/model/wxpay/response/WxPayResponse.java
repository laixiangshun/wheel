package com.lxs.bigdata.pay.model.wxpay.response;

import lombok.Data;
import org.simpleframework.xml.Element;

import java.io.Serializable;

/**
 * 微信支付返回公共父类
 *
 * @author lxs
 */
@Data
public class WxPayResponse implements Serializable {

    private static final long serialVersionUID = 5014379068811962022L;

    @Element(name = "return_code")
    private String returnCode;

    @Element(name = "return_msg", required = false)
    private String returnMsg;

    @Element(name = "result_code", required = false)
    private String resultCode;

    @Element(name = "err_code", required = false)
    private String errCode;

    @Element(name = "err_code_des", required = false)
    private String errCodeDes;

}
