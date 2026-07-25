package com.yaya.security;

import cn.hutool.json.JSONUtil;
import com.yaya.model.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 登录成功,但是访问某些权限较高的资源时，权限不足的处理器
 */
@Component
public class LoginUnAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        Result result = Result.error("权限不足,请重新授权。");
        //将消息json化
        String json = JSONUtil.toJsonStr(result);
        //送到客户端
        response.getWriter().print(json);
    }
}
