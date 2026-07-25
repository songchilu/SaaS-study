package com.yaya.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yaya.entity.SysMenu;
import com.yaya.entity.SysRole;
import com.yaya.entity.SysRoleMenu;
import com.yaya.exception.GlobalCommonException;
import com.yaya.mapper.SysMenuMapper;
import com.yaya.mapper.SysRoleMapper;
import com.yaya.mapper.SysRoleMenuMapper;
import com.yaya.service.SysMenuService;
import com.yaya.util.SecurityUtils;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Resource
    private SysMenuMapper sysMenuMapper;
    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;

    @Override
    public void addMenu(SysMenu sysMenu) {
        //菜单只有平台管理员可以添加
        Boolean b = SecurityUtils.isRootOrAdminOrOperation();
        if(!b){
            throw new GlobalCommonException("非平台管理员,不能创建菜单");
        }
        Long parentId = sysMenu.getParentId();
        Integer menuType = sysMenu.getMenuType();
        String menuTitle = sysMenu.getMenuTitle();
        if(StringUtils.isEmpty(menuTitle)){
            throw new GlobalCommonException("菜单名称不能为空");
        }
        if(menuType==null){
            throw new GlobalCommonException("菜单类型不能为空");
        }
        //按钮不能是一级菜单
        if((parentId==null || parentId==0) && menuType==3){
            throw new GlobalCommonException("一级菜单不能是按钮");
        }
        /*
         * 1. 按钮下不能添加任何类型菜单
         * 2. 链接下不能添加目录
         */
        if(parentId!=null && parentId!=0){
            SysMenu sysMenu_parent = sysMenuMapper.selectById(parentId);
            if(sysMenu_parent!=null){
                Integer menuType_ = sysMenu_parent.getMenuType();
                if(menuType_==3){
                    throw new GlobalCommonException("按钮菜单下,不能创建任何菜单");
                }
                if(menuType_==2){//上一层为链接
                    if(menuType==1){//当前新建为目录
                        throw new GlobalCommonException("链接菜单下不能创建目录");
                    }
                }
            }
        }
        sysMenu.setCreateId(SecurityUtils.getUserId());//创建人
        sysMenu.setUpdateId(SecurityUtils.getUserId());//更新人
        sysMenuMapper.insert(sysMenu);
    }

    @Override
    public void deleteMenu(Long menuId) {
        Boolean b = SecurityUtils.isRootOrAdminOrOperation();
        //判断是不是平台管理员
        if (!b) {
            throw new GlobalCommonException("非平台管理员,没有操作菜单的权限");
        }
        //判断是否存在子菜单
        Long sub_count = sysMenuMapper.selectCount(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, menuId));
        if(sub_count!=null && sub_count>0){
            throw new GlobalCommonException("存在有效子菜单,不能删除");
        }
        //判断是否被授权过,并且授权的角色还活着
        List<SysRoleMenu> sysRoleMenus = sysRoleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getMenuId, menuId));
        if(CollectionUtils.isNotEmpty(sysRoleMenus)){
            List<Long> roleIds = sysRoleMenus.stream().map(SysRoleMenu::getRoleId).distinct().toList();
            Long count = sysRoleMapper.selectCount(new LambdaQueryWrapper<SysRole>().in(SysRole::getRoleId, roleIds).eq(SysRole::getIsDeleted, 0));
            if(count!=null && count>0){
                throw new GlobalCommonException("当前菜单已被授权,不能删除");
            }
        }
        //删除菜单
        sysMenuMapper.deleteById(menuId);
        //删除菜单授权
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getMenuId, menuId));
    }

    @Override
    public void updateMenu(SysMenu sysMenu) {
        //菜单只有平台管理员可以更新
        Boolean b = SecurityUtils.isRootOrAdminOrOperation();
        if(!b){
            throw new GlobalCommonException("非平台管理员,不能更新菜单");
        }
        sysMenu.setUpdateId(SecurityUtils.getUserId());//更新人ID

        //更新菜单时,不能修改类型
        Integer menuType = sysMenu.getMenuType();//要修改的类型
        Long menuId = sysMenu.getMenuId();
        SysMenu sysMenu_ = sysMenuMapper.selectById(menuId);
        if(sysMenu_==null){
            throw new GlobalCommonException("修改的菜单不存在");
        }
        Integer menuType_ = sysMenu_.getMenuType();
        if(!menuType_.equals(menuType)){
            throw new GlobalCommonException("不允许修改菜单类型");
        }
        sysMenuMapper.updateById(sysMenu);
    }

    @Override
    public List<SysMenu> getMenuTree(String menuTitle, Integer status) {
        return sysMenuMapper.getSysMenuTree(menuTitle, status);
    }

    @Override
    public List<SysMenu> getAuthMenuTree(String menuTitle) {
        Boolean b = SecurityUtils.isRoot();//超级管理员,能看见所有菜单
        if(b){
            return sysMenuMapper.getSysMenuTreeExcludeButton(menuTitle);
        }else {
            //除了超级管理员之外的所有角色都是授权获取
            return sysMenuMapper.getAuthSysMenuTreeExcludeButton(SecurityUtils.getRoleId());
        }
    }
}
