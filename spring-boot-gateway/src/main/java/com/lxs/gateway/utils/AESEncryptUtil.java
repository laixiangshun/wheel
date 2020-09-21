package com.lxs.gateway.utils;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;

/**
 * AES可逆加密封装
 *
 * @author : lujing
 * @since :  2019/9/11 16:26
 */

public class AESEncryptUtil {
    
    
    /**
     * 加密使用的key
     */
    private final static String key = "Ill7sIl9snT6UPXm";
    
    
    /**
     * 加密字符串
     *
     * @param content
     * @return
     */
    public static String encrypt(String content) {
        // 构建
        AES aes = SecureUtil.aes(key.getBytes());
        return aes.encryptHex(content);
    }
    
    
    /**
     * 解密字符串
     *
     * @param encryptHex
     * @return
     */
    public static String decryptStr(String encryptHex) {
        // 构建
        AES aes = SecureUtil.aes(key.getBytes());
        return aes.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);
    }
    
    /**
     * 加密字符串
     *
     * @param content
     * @return
     */
    public static String encrypt(String key, String content) {
        // 构建
        AES aes = SecureUtil.aes(key.getBytes());
        byte[] encrypt = aes.encrypt(content);
        return Base64Encoder.encode(encrypt);
    }
    
    
    /**
     * 解密字符串
     *
     * @param encryptHex
     * @return
     */
    public static String decryptStr(String key, String encryptHex) {
        // 构建
        AES aes = SecureUtil.aes(key.getBytes());
        byte[] contentBytes = Base64Decoder.decode(encryptHex);
        byte[] decrypt = aes.decrypt(contentBytes);
        return StrUtil.str(decrypt, CharsetUtil.CHARSET_UTF_8);
    }
    
    
    public static void main(String[] args) {
        String content = "f84aa793613f28eb215c6685bf2dddf0";
        String encrypt = AESEncryptUtil.encrypt(content);
        System.out.println(encrypt);
        String decryptStr = AESEncryptUtil.decryptStr(encrypt);
        System.out.println(decryptStr);
        String str = decryptStr(SecureUtil.md5("83cbe5f4678e066957988133fc9a769b"),"N4Rr0zSTbdVxvIlhbbYDBK0n6brTBEuyU1TH811dcz20vjARIoQb76E2wvcko2dZAs8+u4lnmj7+f80xeY+bYnEOK5llGvmum2aHKmFdrUgWTBBXsoyPtV0YArcUVXnx0xogkjiVeRPqugSsbhwY1pMAt3NsQjv26uJIhJ4AC94=");
        System.out.println(str);
    }
}
