package com.yaya.security;

import cn.hutool.core.collection.CollectionUtil;
import com.yaya.entity.SysRoleDept;
import com.yaya.entity.SysUser;
import lombok.Data;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * SpringSecurity规定在SpringSecurity上下文中传递用户信息的接口
 */
@Data
public class LoginUserDetails implements UserDetails {

    private SysUser sysUser;                //用户信息
    private List<String> roleCodes;         //角色编号列表
    private List<SysRoleDept> roleDeptList; //角色下自定义数据权限 - 部门ID列表

    public LoginUserDetails(SysUser sysUser, List<String> roleCodes,List<SysRoleDept> roleDeptList) {
        this.sysUser = sysUser;
        this.roleCodes = roleCodes;
        this.roleDeptList = roleDeptList;
    }

    /**
     * 当前用户下的权限
     * 1. 粗粒度授权  角色授权
     * 2. 细粒度授权  权限授权
     * 例如:
     *  1. 一个用户拥有 ceo 权限 可以使用哪些功能           角色控制
     *  2. 一个用户拥有 ceo 权限 只拥有某一个资源的查看权限   权限控制
     * 注意:
     *  SpringSecurity 将角色和权限的授权放在了一个集合中,在RBAC中从属于两张表
     *  官方为了区分角色和权限,规定了角色必须以ROLE_开发,这样在一个集合中就能区分哪些数据是角色哪些数据是权限
     *  授权集合不能为null,当前用户如果没有授权(角色+权限),那么给一个空的集合对象,不能给null对象
     * @return 角色+权限集合
     */
    @Override @NullMarked
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        //角色：粗粒度权限，以ROLE_开头区分permission
        if(CollectionUtil.isNotEmpty(roleCodes)){
            for (String roleCode : roleCodes) {
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_"+roleCode));
            }
        }
        return grantedAuthorities;
    }

    /**
     * 获取密码
     * @return 密码
     */
    @Override
    public String getPassword() {
        return sysUser.getPassword();
    }

    /**
     * 获取账户名
     * 注意: 用户名其实是账户名
     * 账户名可以是手机号,邮箱号,或者就是用户名 只要唯一即可,不要被框架命名所困扰
     * @return 账户
     */
    @Override @NullMarked
    public String getUsername() {
        return sysUser.getUsername();
    }

    /**
     * 判断账户是否过期
     * 此方法在SpringSecurity5.x版本时需要重写,在6.x版本可以根据需求进行重写,详情请看UserDetails接口
     * @return true:未过期 false:过期
     */
    @Override
    public boolean isAccountNonExpired() {
        //获取过期时间
        LocalDateTime expiredTime = sysUser.getExpiredTime();
        //当前时间
        LocalDateTime now = LocalDateTime.now();
        return !expiredTime.isBefore(now); // 是否过期
    }

    /**
     * 账户是否可用  1:可用 0:不可用
     * 此方法在SpringSecurity5.x版本时需要重写,在6.x版本可以根据需求进行重写,详情请看UserDetails接口
     */
    @Override
    public boolean isEnabled() {
        return sysUser.getIsEnabled() == 1;
    }
}
