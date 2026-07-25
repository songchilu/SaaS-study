package com.yaya.security;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.yaya.config.YaYaConfig;
import com.yaya.util.JwtUtils;
import com.yaya.util.RedisClient;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * 校验和解析token的过滤器（统一token处理的过滤器）
 */
@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Resource
    private RedisClient redisClient;
    @Resource
    private YaYaConfig yaYaConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            //获取token
            String token = request.getHeader("Authorization");
            if(StringUtils.hasText(token)){
                //获取token中的负载
                String username = JwtUtils.getClaim(token,"username").toString();
                String key="login_user:"+username;
                String json = redisClient.get(key);
                if(StringUtils.hasText(json)){
                    LoginUserDetails userDetails = JSONUtil.toBean(json, LoginUserDetails.class);
                    if(Objects.nonNull(userDetails)){
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }else {
                        SecurityContextHolder.getContext().setAuthentication(null);
                    }
                    /*
                     * token续期
                     * 1. token不是长期有效的
                     * 2. 系统在使用过程中不能让token直接失效
                     * 3. 在要过期的1小时以内,进行续期
                     */
                    try {
                        //token的过期时间
                        DateTime expirationDate = JwtUtils.getExpirationDate(token);
                        //当前时间
                        DateTime now = DateTime.now();
                        if (expirationDate != null && expirationDate.isAfter(now)) { //过期时间存在,并且还未过期
                            // 计算当前时间与过期时间相差的小时数
                            long betweenMinute = DateUtil.between(now, expirationDate, DateUnit.MINUTE);
                            // 差值小于等于 1 小时，触发续期
                            if (betweenMinute <= 60) {
                                log.info("用户 [{}] 的 Token 即将在一小时内过期，触发自动续期机制...", username);
                                // 重新组装 Claims 载荷（这里包含你的业务需要，比如 username）
                                Map<String, Object> claims = Map.of("username", username);
                                // 重新生成新 Token
                                String newToken = JwtUtils.createToken(claims, yaYaConfig.getTokenTimeout());
                                // 将新 Token 塞入特定的 Response Header
                                response.setHeader("Authorization-Refresh", newToken);
                                // 关键：暴露自定义请求头，允许前端跨域读取
                                response.setHeader("Access-Control-Expose-Headers", "Authorization-Refresh");
                            }
                        }
                    }catch (Exception e){
                        // 续期失败不应该阻断用户的本次正常请求，打印警告日志即可
                        log.warn("Token自动续期失败，但不影响本次请求: {}", e.getMessage());
                    }

                }
            }
            //放行,后面交给Spring Security 框架
            filterChain.doFilter(request,response);
        }catch (RuntimeException e){
            log.error("doFilterInternal",e);
            throw new RuntimeException(e);
        }
    }
}
