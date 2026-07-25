package com.yaya.security;

import jakarta.annotation.Resource;
import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SpringSecurity核心配置
 */
@Configuration
@EnableMethodSecurity //开启注解
public class SecurityConfig {

    @Resource
    private LoginUnAuthenticationEntryPointHandler loginUnAuthenticationEntryPointHandler;
    @Resource
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Resource
    private LogoutStatusSuccessHandler logoutStatusSuccessHandler;
    @Resource
    private LoginUnAccessDeniedHandler loginUnAccessDeniedHandler;

    /**
     * SpringSecurity过滤器
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /*
         * SpringSecurity6.x后改动较大,下面是6.x的配置,SpringSecurity7.x会将SpringSecurity5.x中的过时的移除,所以SpringSecurity5.x在6.x中还可以用，但是在7.x中就不能用了.
         */
        http.csrf(AbstractHttpConfigurer::disable);//防止跨站请求伪造
        http.sessionManagement(t->t.sessionCreationPolicy(SessionCreationPolicy.STATELESS));//取消session
        http.authorizeHttpRequests(t->t.requestMatchers(
                "/login",
                "/captchaImage",
                "/actuator/health",//健康检查
                "/actuator/health/liveness",//健康检查 - 存活探针
                "/actuator/health/readiness",//健康检查 - 就绪探针
                "/favicon.ico",
                "/file/**",
                "/webjars/**",
                "/v3/api-docs/**",
                "/doc.html"
                )
                .permitAll().dispatcherTypeMatchers(DispatcherType.ASYNC) //配置 AI 模型 Stream 流式对话
                .permitAll().anyRequest().authenticated()); //配置拦截规则


        //在用户名密码校验过滤器前添加自定义的token统一过滤器,将token解析完后方形到UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        //注册自定义匿名访问私有资源处理器
        http.exceptionHandling(t->t.authenticationEntryPoint(loginUnAuthenticationEntryPointHandler));
        //注册自定义权限不足处理器
        http.exceptionHandling(t->t.accessDeniedHandler(loginUnAccessDeniedHandler));
        //将自定义注册成功的处理器注册给SpringSecurity
        http.logout(t->t.logoutSuccessHandler(logoutStatusSuccessHandler));
        return http.build();
    }

    /**
     * 配置加密工具
     */
    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
