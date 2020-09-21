package com.lxs.bigdata.pay.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.http.ssl.SSLContexts;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;

/**
 * 微信支付配置
 *
 * @author lxs
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WxPayH5Config extends PayConfig {

    /**
     * 公众号appId
     */
    private String appId;

    /**
     * 公众号appSecret
     */
    private String appSecret;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 商户密钥
     */
    private String mchKey;

    /**
     * 商户证书路径
     */
    private String keyPath;

    /**
     * 证书内容
     */
    private SSLContext sslContext;

    /**
     * 初始化证书
     */
    public void initsslcontext() {
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(new File(this.keyPath));
        } catch (IOException e) {
            throw new RuntimeException("读取微信商户证书文件出错", e);
        }

        try {
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            char[] partnerId2charArray = mchId.toCharArray();
            keystore.load(inputStream, partnerId2charArray);
            this.sslContext = SSLContexts.custom().loadKeyMaterial(keystore, partnerId2charArray).build();
        } catch (Exception e) {
            throw new RuntimeException("证书文件有问题，请核实！", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
