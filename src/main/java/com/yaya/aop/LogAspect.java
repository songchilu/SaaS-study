package com.yaya.aop;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONUtil;
import com.jthinking.common.util.ip.IPInfo;
import com.jthinking.common.util.ip.IPInfoUtils;
import com.yaya.annotation.LogCollect;
import com.yaya.entity.SysLog;
import com.yaya.mapper.SysLogMapper;
import com.yaya.util.SecurityUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 统一日志处理
 * 日志收集切面管理类
 * 1. 收集的日志是在控制器层带有@LogCollect注解的方法，没有被@LogCollect注解标记的函数不会被收集
 */
@Order(1) //优先级
@Component
@Slf4j
@Aspect
public class LogAspect {

    @Resource
    private SysLogMapper sysLogMapper;

    /**
     * 环绕通知
     */
    @Around(value = "@annotation(com.yaya.annotation.LogCollect)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        //当前时间戳
        long startTime = System.currentTimeMillis();
        SysLog sysLog = new SysLog();
        // 1. 获取 HTTP 请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogCollect annotation = method.getAnnotation(LogCollect.class);
        String module = annotation.module();
        //模块名称
        sysLog.setBusinessName(module);
        //请求地址
        HttpServletRequest request = attributes.getRequest();
        String requestURI = request.getRequestURI();
        if(requestURI.contains("/login")){
            sysLog.setLogType(1);
        }else {
            sysLog.setLogType(2);
        }
        sysLog.setRequestUrl(requestURI);//请求地址
        Long userId = SecurityUtils.getUserId();
        Long deptId = SecurityUtils.getDeptId();
        if(userId!=null){
            sysLog.setCreateId(userId);
        }
        if(deptId!=null){
            sysLog.setDeptId(deptId);
        }
        //判断是否记录请求参数
        boolean logRequest = annotation.logRequest();
        //判断是否记录响应参数
        boolean logResponse = annotation.logResponse();
        //获取请求参数
        Object[] args = joinPoint.getArgs();
        String requestJsonStr = ArrayUtils.isNotEmpty(args) ? JSONUtil.toJsonStr(args) : null;
        if(logRequest){
            sysLog.setRequestParams(requestJsonStr);
        }
        //操作人IP地址
        //String ip = request.getRemoteAddr();
        String ip = request.getHeader("X-Real-IP");
        if(StringUtils.isEmpty(ip)){
            ip = request.getHeader("X-Forwarded-For");
            if(StringUtils.isEmpty(ip)){
                ip = request.getRemoteAddr();
            }
        }
        sysLog.setIp(ip);
        //IP对应的城市信息
        IPInfo ipInfo = IPInfoUtils.getIpInfo(ip);
        String address = ipInfo.getAddress();
        sysLog.setAddress(address);
        //客户端浏览器
        String browser = getBrowser(request);
        sysLog.setBrowser(browser);
        //日志链路追踪
        sysLog.setTrackId(MDC.get("trackId"));

        Object proceed = null;
        try {
            //日志打印
            log.info("params:{}",requestJsonStr);//请求参数
            log.info("ip:{}",ip);//客户端IP
            log.info("address:{}",address);//客户端地址
            log.info("browser:{}",browser);//客户端浏览器
            proceed = joinPoint.proceed();
            log.info("response:{}",proceed);//返回值
            log.info("----------------AOP------------------");
            if(proceed!=null && logResponse){
                String responseJsonStr = JSONUtil.toJsonStr(proceed);
                sysLog.setResponseResult(responseJsonStr);
            }
        }catch (Exception e){
            log.error("aop日志记录发生异常:",e);
            sysLog.setErrorMsg(e.getMessage());//异常消息
            sysLog.setStatus(0);//请求状态
            throw e;
        }finally {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            sysLog.setExecutionTime(executionTime);// 耗时（毫秒）
            sysLogMapper.insert(sysLog);
        }

        return proceed;
    }


    /**
     * 从请求头中解析浏览器品牌和版本
     * @return 例如: "Chrome 122.0.0.0" 或 "Safari 17.2"
     */
    public static String getBrowser(HttpServletRequest request) {
        if (request == null) {
            return "Unknown";
        }

        // 1. 获取 User-Agent 请求头
        String uaString = request.getHeader("User-Agent");
        if (!StringUtils.hasText(uaString)) {
            return "Unknown";
        }
        try {
            // 2. 使用 Hutool 解析 User-Agent
            UserAgent ua = UserAgentUtil.parse(uaString);

            // 3. 获取浏览器名称和版本
            String browserName = ua.getBrowser().getName(); // 例如: ChromeW
            if ("Unknown".equalsIgnoreCase(browserName)) {
                return "Unknown Browser";
            }
            return browserName;
        } catch (Exception e) {
            log.error("日志-浏览器解析失败:",e);
            return "Parse Error";
        }
    }
}
