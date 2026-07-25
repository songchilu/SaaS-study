package com.yaya.service;

import com.yaya.entity.SysMenu;

import java.util.List;

/**
 * 菜单-业务逻辑层
 */
public interface SysMenuService {

    /**
     * 添加菜单
     * @param sysMenu 菜单信息
     */
    void addMenu(SysMenu sysMenu);

    /**
     * 删除菜单
     * @param menuId 菜单ID
     */
    void deleteMenu(Long menuId);

    /**
     * 更新菜单
     * @param sysMenu 菜单信息
     */
    void updateMenu(SysMenu sysMenu);


    /**
     * 菜单树 - 管理页面
     * @param menuTitle 菜单名称
     * @param status  状态(1-正常 0-停用)
     * @return 菜单树
     */
    List<SysMenu> getMenuTree(String menuTitle,Integer status);


    /**
     * 菜单树 - 左侧菜单栏显示 - 获取用户授权的菜单树
     * @param menuTitle 菜单名称
     * @return 授权的菜单树
     */
    List<SysMenu> getAuthMenuTree(String menuTitle);
}
