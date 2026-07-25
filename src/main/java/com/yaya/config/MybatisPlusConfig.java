package com.yaya.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.yaya.interceptor.YaYaDataPermissionInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 插件配置
 */
@MapperScan(value = {"com.yaya.mapper"})
@Configuration
public class MybatisPlusConfig {

    /**
     * 注册分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 创建数据权限插件
        DataPermissionInterceptor dataPermissionInterceptor = new DataPermissionInterceptor(new YaYaDataPermissionInterceptor());
        // 添加到拦截器链
        interceptor.addInnerInterceptor(dataPermissionInterceptor);
        //添加分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

}
