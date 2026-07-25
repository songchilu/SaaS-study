package com.yaya.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yaya.entity.SysDepartment;
import com.yaya.entity.SysRole;
import com.yaya.entity.SysRoleDept;
import com.yaya.entity.SysUser;
import com.yaya.mapper.SysDepartmentMapper;
import com.yaya.mapper.SysRoleDeptMapper;
import com.yaya.mapper.SysRoleMapper;
import com.yaya.mapper.SysUserMapper;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 从数据库获取用户信息,将查询的用户信息送给SpringSecurity上下文
 * 用户信息包含: 基本用户信息+用户角色信息+角色权限信息  RBAC模式
 */
@Transactional
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private SysDepartmentMapper sysDepartmentMapper;
    @Resource
    private SysRoleDeptMapper sysRoleDeptMapper;



    @Override @NullMarked
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .eq(SysUser::getIsDeleted, 0)
        );
        if(null==user){
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        //获取部门
        SysDepartment department = sysDepartmentMapper.selectById(user.getDeptId());
        if(Objects.nonNull(department)){
            user.setSysDepartment(department);
        }
        //用户的角色编号列表
        List<String> roleCodeList = new ArrayList<>();
        //角色下的 - 自定义部门权限
        List<SysRoleDept> roleDeptList =  new ArrayList<>();
        //获取角色
        SysRole role = sysRoleMapper.selectById(user.getRoleId());
        if(Objects.nonNull(role)){
            user.setSysRole(role); //设置角色
            //添加到角色编号集合中用于授权
            String roleCode = role.getRoleCode();
            roleCodeList.add(roleCode);
            List<SysRoleDept> sysRoleDepts = sysRoleDeptMapper.selectList(new LambdaQueryWrapper<SysRoleDept>().eq(SysRoleDept::getRoleId, user.getRoleId()));
            if(CollectionUtils.isNotEmpty(sysRoleDepts)){
                roleDeptList.addAll(sysRoleDepts);
            }
        }
        return new LoginUserDetails(user,roleCodeList,roleDeptList);
    }
}
