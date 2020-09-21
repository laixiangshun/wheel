package com.lxs.bigdata.pay.service.alipay;

import com.lxs.bigdata.pay.config.AlipayConfig;
import com.lxs.bigdata.pay.config.SignType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.*;

/**
 * 支付宝Application方式签名
 *
 * @author lxs
 */
@Slf4j
public class AlipaySignatureComponent {

    private AlipayConfig alipayConfig;

    public AlipaySignatureComponent(AlipayConfig alipayConfig) {
        Objects.requireNonNull(alipayConfig, "alipayConfig is null.");
        this.alipayConfig = alipayConfig;
    }

    public String sign(SortedMap<String, String> sortedParamMap) {
        Objects.requireNonNull(sortedParamMap, "sortedParamMap is null.");
        List<String> paramList = new ArrayList<>();
        sortedParamMap.forEach((k, v) -> {
            if (StringUtils.isBlank(k) || "sign".equals(k) || StringUtils.isBlank(v)) {
                return;
            }

            paramList.add(k + "=" + v);
        });
        String param = String.join("&", paramList);
        return signparamwithrsa(param);
    }

    public boolean verify(Map<String, String> toBeVerifiedParamMap, SignType signType, String sign) {
        Objects.requireNonNull(toBeVerifiedParamMap, "to be verified param map is null.");
        if (toBeVerifiedParamMap.isEmpty()) {
            throw new IllegalArgumentException("to be verified param map is empty.");
        }

        Objects.requireNonNull(signType, "sign type is null.");
        if (signType == SignType.MD5) {
            throw new IllegalArgumentException("unsupported sign type: MD5.");
        }

        if (StringUtils.isBlank(sign)) {
            throw new IllegalArgumentException("sign is blank.");
        }

        /* 1. 验签 */
        List<String> paramList = new ArrayList<>();
        toBeVerifiedParamMap.forEach((k, v) -> {
            if (StringUtils.isBlank(k) || "sign_type".equals(k) || "sign".equals(k) || StringUtils.isEmpty(v)) {
                return;
            }

            paramList.add(k + "=" + v);
        });
        Collections.sort(paramList);
        String toBeVerifiedStr = String.join("&", paramList);
        boolean r = verifyparamwithrsa(toBeVerifiedStr, signType, sign);
        if (!r) {
            log.warn("fail to verify sign with sign type {}.", signType.name());
            return false;
        }

        /* 2. 校验notify_id, 同步返回是没有notify_id参数的 */
        String notifyId = toBeVerifiedParamMap.get("notify_id");
        if (!StringUtils.isEmpty(notifyId)) {
            r = this.verifyNotifyId(notifyId);
            if (!r) {
                log.warn("fail to verify notify id.");
                return false;
            }
        }

        return true;
    }


    /**
     * 验证notifyId, 注意notifyId的时效性大约为1分钟
     *
     * @param notifyId 异步通知通知id
     * @return 验证notifyId通过返回true, 否则返回false
     */
    private boolean verifyNotifyId(String notifyId) {
        return false;
    }

    /**
     * RSA算法对参数加签名
     */
    private String signparamwithrsa(String param) {
        String algorithm;
        switch (this.alipayConfig.getSignType()) {
            case RSA:
                algorithm = "SHA1WithRSA";
                break;
            case RSA2:
                algorithm = "SHA256WithRSA";
                break;
            default:
                throw new IllegalStateException("unsupported sign type [" + this.alipayConfig.getSignType() + "].");
        }
        try {
            java.security.Signature sig = java.security.Signature.getInstance(algorithm);
            sig.initSign(this.alipayConfig.getAppRSAPrivateKeyObject());
            sig.update(param.getBytes(this.alipayConfig.getInputCharset()));
            return Base64.getEncoder().encodeToString(sig.sign());
        } catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
            throw new IllegalStateException("sign error.", e);
        }
    }

    /**
     * 验证签名
     */
    private boolean verifyparamwithrsa(String param, SignType signType, String sign) {
        String algorithm;
        switch (signType) {
            case RSA:
                algorithm = "SHA1WithRSA";
                break;
            case RSA2:
                algorithm = "SHA256WithRSA";
                break;
            default:
                throw new IllegalStateException("unsupported sign type [" + this.alipayConfig.getSignType() + "].");
        }
        try {
            java.security.Signature sig = java.security.Signature.getInstance(algorithm);
            sig.initVerify(this.alipayConfig.getAlipayRSAPublicKeyOBject());
            sig.update(param.getBytes("utf-8"));
            return sig.verify(Base64.getDecoder().decode(sign));
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | UnsupportedEncodingException e) {
            throw new IllegalStateException("AliPay verify error.");
        }
    }

}
