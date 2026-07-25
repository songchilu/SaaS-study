package com.yaya.util;

import com.yaya.entity.SysRole;
import com.yaya.entity.SysRoleDept;
import com.yaya.entity.SysUser;
import com.yaya.exception.GlobalCommonException;
import com.yaya.security.LoginUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Objects;

/**
 * 获取SpringSecurity框架认证后的基本信息
 */
@Slf4j
public class SecurityUtils {

    /**
     * 获取认证信息
     */
    public static Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取当前登录SpringSecurity用户信息
     */
    public static LoginUserDetails getLoginUser(){
        try {
            Authentication authentication = getAuthentication();
            if(authentication!=null && authentication.isAuthenticated()){
                Object principal = authentication.getPrincipal();
                if(Objects.nonNull(principal)){
                    if(principal instanceof LoginUserDetails){
                        return (LoginUserDetails) principal;
                    }
                }
            }
            return null;
        }catch (Exception e){
            log.error("getLoginUser",e);
            throw new GlobalCommonException("未认证,请重新认证");
        }
    }

    /**
     * 获取当前登录用户信息
     */
    public static SysUser getUser(){
        LoginUserDetails user = getLoginUser();
        if(null!=user){
            return user.getSysUser();
        }
        return null;
    }

    /**
     * 获取当前登录用户的ID
     */
    public static Long getUserId(){
        SysUser user = getUser();
        if(null!=user){
            return user.getUserId();
        }
        return null;
    }

    /**
     * 获取部门ID
     */
    public static Long getDeptId(){
        SysUser user = getUser();
        if(null!=user){
            return user.getDeptId();
        }
        return null;
    }

    /**
     * 角色下的自定义部门数据
     */
    public static List<SysRoleDept> getRoleDept(){
        LoginUserDetails loginUser = getLoginUser();
        if(null!=loginUser){
            return loginUser.getRoleDeptList();
        }
        return List.of();
    }

    /**
     * 获取当前登录用户账号
     */
    public static String getUsername(){
        SysUser user = getUser();
        if(null!=user){
            return user.getUsername();
        }
        return null;
    }

    /**
     * 获取角色编号
     */
    public static String getRoleCode(){
        SysUser user = getUser();
        if(null!=user){
            return user.getSysRole().getRoleCode();
        }
        return null;
    }

    /**
     * 获取角色ID
     */
    public static Long getRoleId(){
        SysUser user = getUser();
        if(null!=user){
            return user.getSysRole().getRoleId();
        }
        return null;
    }

    /**
     * 获取角色的数据权限
     * 数据权限(1-所有数据 2-部门及子部门数据 3-本部门数据 4-本人数据 5-自定义部门数据)
     */
    public static Integer getDataScope(){
        SysUser user = getUser();
        if(Objects.nonNull(user)){
            SysRole sysRole = user.getSysRole();
            if(Objects.nonNull(sysRole)){
                return sysRole.getDataScope();
            }
        }
        return null;
    }

    /**
     * 判断是不是平台超级管理员角色
     */
    public static Boolean isRoot(){
        String roleCode = getRoleCode();
        return "root".equalsIgnoreCase(roleCode);
    }

    /**
     * 判断是不是平台系统管理员角色
     */
    public static Boolean isAdmin(){
        String roleCode = getRoleCode();
        return "admin".equalsIgnoreCase(roleCode);
    }

    /**
     * 判断是不是平台运营管理员角色
     */
    public static Boolean isOperation(){
        String roleCode = getRoleCode();
        return "operation".equalsIgnoreCase(roleCode);
    }

    /**
     * 判断是不是平台所有管理员
     */
    public static Boolean isRootOrAdminOrOperation(){
        return isAdmin() || isOperation() || isRoot();
    }
}
