package com.yaya.security;

import cn.hutool.json.JSONUtil;
import com.yaya.model.Result;
import com.yaya.util.JwtUtils;
import com.yaya.util.RedisClient;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * 注销成功处理器
 */
@Component
public class LogoutStatusSuccessHandler implements LogoutSuccessHandler {
    @Resource
    private RedisClient redisClient;
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException{
        String token = request.getHeader("Authorization");
        //判断token是否存在
        if(StringUtils.hasText(token)){
            //获取token中的负载
            String username = JwtUtils.getClaim(token,"username").toString();
            //从redis中删除
            String key="login_user:"+username;
            redisClient.del(key);
        }
        //返回给客户端注销成功的提示
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        Result result = Result.ok("注销成功");
        String json = JSONUtil.toJsonStr(result);
        response.getWriter().print(json);
    }
}
