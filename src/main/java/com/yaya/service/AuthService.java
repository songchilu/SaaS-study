package com.yaya.service;

import java.io.IOException;
import java.util.Map;

/**
 * 用户认证业务逻辑层
 */
public interface AuthService {

    /**
     * 验证码生成
     * @return 验证码信息
     */
    Map<String,Object> captchaImage() throws IOException;

    /**
     * 用户名密码登录
     * @param username  用户名
     * @param password  密码
     * @param captcha   验证码
     * @param uuid      获取验证码的key
     * @return 登录成功返回信息
     */
    Map<String,Object> login(String username,String password,String captcha,String uuid);

}
