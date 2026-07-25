package com.yaya.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 自定义配置文件
 * 作用: 封装配置文件(application-*.yml)中的配置信息
 */
@Component
@Data
@ConfigurationProperties(prefix = "yaya") //配置文件中的配置前缀
public class YaYaConfig {
    /**
     * 文件上传到服务器的地址
     */
    private String localUrl;
    /**
     * 应用日志的保存路径
     */
    private String logUrl;
    /**
     * 验证码过期时间(单位秒)
     */
    private Long captchaTimeout;
    /**
     * 验证码生成数量
     */
    private Integer captchaLength;
    /**
     * 验证码干扰线数量
     */
    private Integer captchaCircleCount;
    /**
     * 验证码宽度
     */
    private Integer captchaWidth;
    /**
     * 验证码高度
     */
    private Integer captchaHeight;
    /**
     * 访问令牌过期时间(单位分钟) 默认30分钟
     */
    private Integer tokenTimeout=30;
}
