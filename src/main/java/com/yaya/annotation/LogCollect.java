package com.yaya.annotation;

import java.lang.annotation.*;

/**
 * 1. 日志收集注解
 * 2. 如果想收集哪个接口的日志信息,就将哪个接口的方法添加上此注解
 * 3. 不添加注解的方法,不会进行日志收集
 * 4. 注解只能修饰方法
 */
@Target(ElementType.METHOD) // 作用在方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时有效
@Documented
public @interface LogCollect {
    /**
     * 模块/业务名称（例如：用户管理-新增用户）
     */
    String module() default "";

    /**
     * 是否记录请求参数 默认不记录
     * @return true:记录 false:不记录
     */
    boolean logRequest() default false;

    /**
     * 是否记录响应结果 默认不记录
     * @return true:记录 false:不记录
     */
    boolean logResponse() default false;
}
