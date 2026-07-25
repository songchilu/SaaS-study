package com.yaya.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * 生成日志链路ID的过滤器
 */
@Slf4j
@Component
public class TrackIdFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String requestURI = request.getRequestURI();
            // 如果前端传了X-Track-Id，就沿用
            String trackId = request.getHeader("X-Track-Id");
            if (trackId == null || trackId.isEmpty()) {
                //如果前端没传就自己生成
                trackId = UUID.randomUUID().toString();
            }
            MDC.put("trackId", trackId);
            // 返回给调用方
            response.setHeader("X-Track-Id", trackId);
            log.info("----------------filter------------------");
            log.info("请求地址:{}",requestURI);
            filterChain.doFilter(request, response);
        }finally {
            MDC.clear();
        }
    }
}
