package com.yaya.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yaya.entity.SysMenu;
import com.yaya.entity.SysRole;
import com.yaya.entity.SysRoleMenu;
import com.yaya.exception.GlobalCommonException;
import com.yaya.mapper.SysMenuMapper;
import com.yaya.mapper.SysRoleMapper;
import com.yaya.mapper.SysRoleMenuMapper;
import com.yaya.service.SysRoleMenuService;
import com.yaya.util.SecurityUtils;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class SysRoleMenuServiceImpl implements SysRoleMenuService {

    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;
    @Resource
    private SysMenuMapper sysMenuMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public void addOrUpdateAuthMenu(Long roleId, List<Long> menuIds) {
        //判断操作人是不是平台管理员
        Boolean b = SecurityUtils.isRootOrAdminOrOperation();
        if(!b){
            throw new GlobalCommonException("非平台管理,不能授权");
        }
        //判断当前角色是否是超级管理员root,超级管理员不需要授权
        SysRole sysRole = sysRoleMapper.selectById(roleId);
        if (sysRole != null) {
            String roleCode = sysRole.getRoleCode();
            if("root".equalsIgnoreCase(roleCode)){
                throw new GlobalCommonException("超级管理员不需要授权");
            }
        }

        //当前用户角色ID
        Long roleId_ = SecurityUtils.getRoleId();
        if(roleId_.equals(roleId)){
            throw new GlobalCommonException("不能给相同角色用户授权");
        }

        //判断当前角色是否已经授权过,如果已经授权过,将历史授权全部删除,重新更新授权,如果没有授权过,直接授权
        Long count = sysRoleMenuMapper.selectCount(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        if (count!=null && count > 0) {
            //清空当前角色的历史授权
            sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        }
        //重新授权
        if(CollectionUtils.isNotEmpty(menuIds)){
            menuIds.forEach(menuId->{
                //判断菜单是否存在
                SysMenu sysMenu = sysMenuMapper.selectById(menuId);
                if(sysMenu==null){
                    throw new GlobalCommonException("授权的菜单不存在,请确认菜单是否已被删除");
                }
                SysRoleMenu sysRoleMenu = new SysRoleMenu();
                sysRoleMenu.setMenuId(menuId);
                sysRoleMenu.setRoleId(roleId);
                sysRoleMenuMapper.insert(sysRoleMenu);
            });
        }
    }

    @Override
    public List<Long> getAuthMenuIds(Long roleId) {
        //前端授权页面回显已授权的菜单ID,要求菜单ID为菜单的根节点ID
        //获取当前角色下已授权的所有菜单ID
        List<SysRoleMenu> sysRoleMenus = sysRoleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        List<Long> authMenuIds = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(sysRoleMenus)){
            //已授权的菜单ID列表
            List<Long> menuIds = sysRoleMenus.stream().map(SysRoleMenu::getMenuId).distinct().toList();
            if(CollectionUtils.isNotEmpty(menuIds)){
                //一个一个判断，是不是根节点,将根节点的菜单ID返回
                menuIds.forEach(menuId->{
                    //查询当前菜单是否存在子节点,如果不存在子节点说明他自己就是最终的节点
                    List<SysMenu> sysMenus = sysMenuMapper.selectList(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, menuId));
                    if(CollectionUtils.isEmpty(sysMenus)){
                        authMenuIds.add(menuId);
                    }
                });
            }
        }
        return authMenuIds;
    }
}
