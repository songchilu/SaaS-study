package com.yaya.controller;

import com.yaya.annotation.LogCollect;
import com.yaya.annotation.RepeatSubmit;
import com.yaya.model.Result;
import com.yaya.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@Tag(name = "认证管理")
@RestController
public class AuthController {

    @Resource
    private AuthService authService;


    /**
     * 验证码
     */
    @Operation(summary = "验证码生成")
    @LogCollect(module = "认证管理-验证码生成")
    @RepeatSubmit(expireTime = 1,message = "验证码生成的太频繁")
    @PostMapping(value = "/captchaImage")
    public Result<Map<String,Object>> captchaImage() throws IOException {
        return Result.ok(authService.captchaImage());
    }

    @LogCollect(module = "认证管理-登录",logRequest = true,logResponse = true)
    @RepeatSubmit(expireTime = 3,message = "登录已提交,请勿重复操作")
    @Operation(summary = "登录")
    @Parameters(value = {
            @Parameter(name = "username",description = "账号",required = true),
            @Parameter(name = "password",description = "密码",required = true),
            @Parameter(name = "captcha",description = "验证码",required = true),
            @Parameter(name = "uuid",description = "验证码接口返回的uuid",required = true)
    })
    @PostMapping(value = "/login")
    public Result<Map<String,Object>> login(@RequestParam(value = "username") String username,
                        @RequestParam(value = "password") String password,
                        @RequestParam(value = "captcha") String captcha,
                        @RequestParam(value = "uuid") String uuid){
        Map<String, Object> map = authService.login(username, password, captcha, uuid);
        return  Result.ok(map);
    }


    @LogCollect(module = "认证管理-验证token",logResponse = true)
    @Operation(summary = "验证token")
    @PostMapping(value = "/checkToken")
    public Result<Object> checkToken(){
        return Result.ok();
    }

    /**
     * 注销
     * 注意: 注销其实可以不自己实现,直接使用SpringSecurity内置的即可
     *      写一个空方法的原因方便前端调用和查找,也方便后来者理解
     *      注销逻辑在 com.yue.security.LogoutStatusSuccessHandler 类中 由SpringSecurity实现
     */
    @Operation(summary = "注销")
    @PostMapping(value = "/logout")
    public Result<Object> logout(){
        return Result.ok();
    }

}
