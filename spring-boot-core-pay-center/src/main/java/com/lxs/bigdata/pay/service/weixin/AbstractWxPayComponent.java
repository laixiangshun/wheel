package com.lxs.bigdata.pay.service.weixin;

import com.lxs.bigdata.pay.constants.WxPayConstants;
import com.lxs.bigdata.pay.enums.BestPayTypeEnum;
import com.lxs.bigdata.pay.exception.WxPayException;
import com.lxs.bigdata.pay.model.wxpay.response.WxPayResponse;
import com.lxs.bigdata.pay.service.AbstractComponent;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import static com.lxs.bigdata.pay.constants.WxPayConstants.JSAPI;
import static com.lxs.bigdata.pay.constants.WxPayConstants.MWEB;

/**
 * 微信支付公共父类
 *
 * @author lxs
 */
@Slf4j
public abstract class AbstractWxPayComponent extends AbstractComponent {

    /**
     * 判断微信支付返回结果
     */
    boolean checkResponseCode(WxPayResponse response) {
        if (!response.getReturnCode().equals(WxPayConstants.SUCCESS)) {
            throw new WxPayException("【微信统一支付】发起支付, returnCode != SUCCESS, returnMsg = " + response.getReturnMsg());
        }
        if (!response.getResultCode().equals(WxPayConstants.SUCCESS)) {
            throw new WxPayException("【微信统一支付】发起支付, resultCode != SUCCESS, err_code = " + response.getErrCode() + " err_code_des=" + response.getErrCodeDes());
        }
        return true;
    }

    X509TrustManager x509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    /**
     * H5支付交易类型选择
     */
    public String switchH5TradeType(BestPayTypeEnum payTypeEnum) {
        String tradeType = JSAPI;
        switch (payTypeEnum) {
            case WXPAY_H5:
                tradeType = JSAPI;
                break;
            case WXPAY_MWEB:
                tradeType = MWEB;
                break;
            default:
                break;
        }
        return tradeType;
    }
}
