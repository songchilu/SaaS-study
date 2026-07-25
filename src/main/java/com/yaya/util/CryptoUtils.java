package com.yaya.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

import java.util.HashMap;
import java.util.Map;

/**
 *  RSA加密签名工具类
 *      HuTool：5.8.46
 *  1. 密钥对生成: 注意:每生成一次密钥对,不能将公钥和私钥丢失,否则新生成的私钥,不能解密旧公钥加密的数据,这个东西是一对一的关系
 *  2. 公钥加密
 *  3. 私钥解密
 *  5. Base64编码和解码
 */
public class CryptoUtils {

    /**
     * RSA非对称加密，密钥对生成
     * 生成的密钥对是经过base64编码后的
     */
    public static Map<String,String> createRsaKey(){
        Map<String,String> map = new HashMap<>();
        RSA rsa = new RSA();
        String privateKeyBase64 = rsa.getPrivateKeyBase64();
        String publicKeyBase64 = rsa.getPublicKeyBase64();
        map.put("privateKey",privateKeyBase64);
        map.put("publicKey",publicKeyBase64);
        return  map;
    }

    /**
     * 公钥加密
     *
     * @param data      待加密的明文
     * @param publicKey Base64 编码的公钥
     * @return 加密后的密文（Base64 编码）
     */
    public static String encryptByPublicKey(String data, String publicKey) {
        RSA rsa = new RSA(null, publicKey);
        return rsa.encryptBase64(data, KeyType.PublicKey);
    }

    /**
     * 私钥解密
     *
     * @param encryptData Base64 编码的密文
     * @param privateKey  Base64 编码的私钥
     * @return 解密后的明文
     */
    public static String decryptByPrivateKey(String encryptData, String privateKey) {
        RSA rsa = new RSA(privateKey, null);
        return rsa.decryptStr(encryptData, KeyType.PrivateKey);
    }


    /**
     * Base64编码
     * @param text 要编码的数据
     * @return 编码后的数据
     */
    public static String base64Encode(String text) {
        return Base64.encode(text);
    }

    /**
     * Base64解码
     * @param text 要解码的数据
     * @return 解码后的数据
     */
    public static String base64Decode(String text) {
        return Base64.decodeStr(text);
    }


    static void main() {
        Map<String, String> keys = createRsaKey();
        //公钥
        String publicKey = keys.get("publicKey");
        //私钥
        String privateKey = keys.get("privateKey");
        System.out.println("==============公钥+私钥===============");
        System.out.println("公钥: "+publicKey);
        System.out.println("私钥: "+privateKey);
        System.out.println("==============公钥+私钥===============");

        //待加密的数据
        String data="123456";
        System.out.println("待加密的数据:"+data);
        //公钥加密
        String encryptData = encryptByPublicKey(data, publicKey);
        System.out.println("加密后的数据:"+encryptData);

        //私钥解密
        String decryptData = decryptByPrivateKey(encryptData, privateKey);
        System.out.println("解密后的数据:"+decryptData);
    }
}
