package com.lxs.bigdata.pay.model.wxpay.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * 微信沙盒返回
 *
 * @author lxs
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Root(name = "xml", strict = false)
public class WxPaySandboxKeyResponse extends WxPayResponse {

//    @Element(name = "return_code")
//    private String returnCode;
//
//    @Element(name = "return_msg", required = false)
//    private String returnMsg;

    @Element(name = "mch_id", required = false)
    private String mchId;

    @Element(name = "sandbox_signkey", required = false)
    private String sandboxSignkey;
}
