package com.yaya;

import com.jthinking.common.util.ip.IPInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@SpringBootApplication
public class YaYaSaaSPlusApplication {


    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(YaYaSaaSPlusApplication.class, args);
        //初始化ip地理信息库
        log.info("IP地理信息数据库组件启动中...");
        IPInfoUtils.init();
        log.info("IP地理信息数据库组件启动完成...");
        log.info("服务器启动成功... 启动时间为:{}",LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        //获取环境变量
        ConfigurableEnvironment environment = context.getEnvironment();
        log.info("日志文件的保存路径为:{}",environment.getProperty("yaya.log-url"));
    }

}
