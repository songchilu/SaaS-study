package com.yaya.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yaya.config.YaYaConfig;
import com.yaya.entity.SysMenu;
import com.yaya.entity.SysPost;
import com.yaya.entity.SysRoleMenu;
import com.yaya.entity.SysUser;
import com.yaya.entity.SysUserPost;
import com.yaya.exception.GlobalCommonException;
import com.yaya.mapper.SysMenuMapper;
import com.yaya.mapper.SysPostMapper;
import com.yaya.mapper.SysRoleMenuMapper;
import com.yaya.mapper.SysUserPostMapper;
import com.yaya.security.LoginUserDetails;
import com.yaya.service.AuthService;
import com.yaya.util.JwtUtils;
import com.yaya.util.RedisClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FastByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Transactional
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private RedisClient redisClient;
    @Resource
    private YaYaConfig yaYaConfig;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private SysUserPostMapper sysUserPostMapper;
    @Resource
    private SysPostMapper sysPostMapper;
    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;
    @Resource
    private SysMenuMapper sysMenuMapper;

    /**
     * 验证码生成
     */
    @Override
    public Map<String, Object> captchaImage() throws IOException {
        //创建CircleCaptcha对象(使用hutool工具进行验证码生成)
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(Objects.nonNull(yaYaConfig.getCaptchaWidth())?yaYaConfig.getCaptchaWidth():120, Objects.nonNull(yaYaConfig.getCaptchaHeight())?yaYaConfig.getCaptchaHeight():40, Objects.nonNull(yaYaConfig.getCaptchaLength())?yaYaConfig.getCaptchaLength():4, Objects.nonNull(yaYaConfig.getCaptchaCircleCount())?yaYaConfig.getCaptchaCircleCount():5);
        //获取验证码内容
        String code = captcha.getCode();
        /*
         * 将生成的验证码保存到redis数据库中,redis的key自定义,我这里使用hutool工具中的雪花算法生成,你也可以使用其它方式,如 uuid等
         */
        String uuid = IdUtil.getSnowflakeNextIdStr();
        //给redis的key设置一个前缀，方便后续清空验证码
        String key = "captcha:"+uuid;
        //将验证码保存到redis中,以便于后面进行登录校验
        redisClient.set(key,code,yaYaConfig.getCaptchaTimeout());//带过期时间,过期时间在yml配置文件中进行配置
        /*
         * 将生成的验证码图片进行base64处理
         */
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        //生成图片的Base64编码
        ImageIO.write(captcha.getImage(), "png", os);
        byte[] bytes = os.toByteArray();
        //base64加密
        String encode = Base64.encode(bytes);
        Map<String,Object> map = new HashMap<>();
        map.put("img","data:image/png;base64,"+encode);//采用base64加密的方式向前端传递验证码图片
        map.put("uuid",uuid);//给前端(客户端)返回的redis中验证码的key
        map.put("captcha",code);//给前端(客户端)返回的验证码
        return map;
    }

    /**
     * @param username  用户名
     * @param password  密码
     * @param captcha   验证码
     * @param uuid      获取验证码的key
     */
    @Override
    public Map<String, Object> login(String username, String password, String captcha, String uuid) {

        /*
         * 校验验证码
         * uuid: 验证码接口中向redis中保存数据的key值
         * captcha: 用户输如的验证码
         */
        String keyCaptcha = "captcha:"+uuid;
        //从数据库中获取
        String captcha_ = redisClient.get(keyCaptcha);
        if(StringUtils.isEmpty(captcha_)){
            throw new GlobalCommonException("验证码为空");
        }
        //判断生成的验证码和用户输入的验证码是否相同(忽略大小写)
        if(!captcha.equalsIgnoreCase(captcha_)){
            throw new GlobalCommonException("验证码错误");
        }

        /*
         * 验证账户(手机号)和密码
         */
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,password);

        try {
            Authentication authenticate = authenticationManager.authenticate(authenticationToken);
            /*
             * 如果认证成功,SpringSecurity会将用户信息封装到Authentication对象中
             */
            LoginUserDetails principal = (LoginUserDetails) authenticate.getPrincipal();
            if(principal == null){
                throw new GlobalCommonException("认证失败,用户信息为空");
            }
            //基于账户(手机号)生成token
            String token = JwtUtils.createToken(Map.of("username",principal.getUsername()),yaYaConfig.getTokenTimeout());
            //校验token是否生成成功
            if(StringUtils.isEmpty(token)){
                throw new GlobalCommonException("TOKEN生成失败");
            }
            //以token作为key将用户信息json化,保存到redis中
            String json = JSONUtil.toJsonStr(principal);
            //获取token中的负载
            String username_ = JwtUtils.getClaim(token,"username").toString();
            String keyUser = "login_user:"+username_;//使用账号作为key
            redisClient.set(keyUser,json,yaYaConfig.getTokenTimeout());//存储用户信息

            /*
             * 登录成功后,给前端返回信息
             */
            Map<String,Object> map = new HashMap<>();
            map.put("token",token);//令牌
            //用户信息
            SysUser user = principal.getSysUser();
            Map<String,Object> userMap = new HashMap<>();
            userMap.put("userId",user.getUserId());     //用户ID
            userMap.put("avatar",user.getAvatar());     //用户头像
            userMap.put("nickname",user.getNickname()); //用户名称
            if("root".equals(username)){
                userMap.put("super",true);//超级管理员
            }else {
                userMap.put("super",false);
            }
            userMap.put("username",username); //用户账号
            userMap.put("department",user.getSysDepartment());//用户部门信息
            //角色信息
            userMap.put("role",user.getSysRole());  //用户角色信息
            userMap.put("sex",user.getSex());    //用户性别 1-男,0-女,2-保密
            userMap.put("phone",user.getPhone());//用户手机号
            userMap.put("email",user.getEmail());//用户邮箱
            //岗位信息
            List<SysUserPost> sysUserPosts = sysUserPostMapper.selectList(new LambdaQueryWrapper<SysUserPost>().eq(SysUserPost::getUserId, user.getUserId()));
            if(CollectionUtils.isNotEmpty(sysUserPosts)){
                List<SysPost> sysPosts = sysPostMapper.selectByIds(sysUserPosts.stream().map(SysUserPost::getPostId).toList());
                if(CollectionUtils.isNotEmpty(sysPosts)){
                    userMap.put("posts",sysPosts);
                }
            }
            userMap.put("expiredTime",user.getExpiredTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))); //过期时间
            userMap.put("createTime",user.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));   //创建时间
            map.put("user",userMap);
            //权限
            Long roleId = user.getRoleId();
            List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
            if(CollectionUtils.isNotEmpty(roleMenus)){
                //授权的菜单ID列表
                List<Long> menuIds = roleMenus.stream().map(SysRoleMenu::getMenuId).distinct().toList();
                if(CollectionUtils.isNotEmpty(menuIds)){
                    //过滤出只包含按钮的菜单
                    List<SysMenu> sysMenus = sysMenuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                            .in(SysMenu::getMenuId, menuIds)
                            .eq(SysMenu::getStatus, 1)
                            .eq(SysMenu::getMenuType, 3)
                    );
                    //获取按钮菜单的权限
                    map.put("perm",sysMenus.stream().map(SysMenu::getPerms).filter(StringUtils::isNotBlank).distinct().toList());
                }
            }else {
                map.put("perm", Collections.emptyList());
            }
            //权限信息
            return map;
        }catch (RuntimeException e){
            log.error("登陆异常:",e);
            switch (e) {
                case AccountExpiredException ignored -> throw new GlobalCommonException("账号已过期");
                case LockedException ignored -> throw new GlobalCommonException("账号被锁定");
                case CredentialsExpiredException ignored -> throw new GlobalCommonException("账号已过期");
                case DisabledException ignored -> throw new GlobalCommonException("账号不可用");
                case BadCredentialsException ignored -> throw new GlobalCommonException("用户名或密码错误");
                default -> throw new GlobalCommonException(e.getMessage());
            }
        }
    }
}
