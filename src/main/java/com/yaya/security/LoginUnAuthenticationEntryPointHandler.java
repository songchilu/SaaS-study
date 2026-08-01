package com.yaya.security;

import cn.hutool.json.JSONUtil;
import com.yaya.model.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 匿名访问私有资源时的处理器
 */
@Component
public class LoginUnAuthenticationEntryPointHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException{
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        Result error = Result.error(401,"用户未登录或登录已过期,请重新登录");
        String json = JSONUtil.toJsonStr(error);
        response.getWriter().print(json);
    }
}
