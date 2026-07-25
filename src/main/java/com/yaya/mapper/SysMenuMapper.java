package com.yaya.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yaya.entity.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单 - 持久化层
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {


    /**
     * 菜单树 - 管理页
     */
    List<SysMenu> getSysMenuTree(@Param("menuTitle") String menuTitle,@Param("status") Integer status);

    /**
     * 菜单树 - 管理页 - 子菜单查询
     */
    List<SysMenu> getSubSysMenuTree(@Param("menuId") Long menuId,@Param("status") Integer status);

    /**
     * 菜单树 - 左侧菜单 - 排除按钮
     */
    List<SysMenu> getSysMenuTreeExcludeButton(@Param("menuTitle") String menuTitle);

    /**
     * 菜单树 - 左侧菜单 - 排除按钮
     */
    List<SysMenu> getSubSysMenuTreeExcludeButton(@Param("menuId") Long menuId);

    /**
     * 菜单树 - 左侧菜单 - 排除按钮 - 已授权
     */
    List<SysMenu> getAuthSysMenuTreeExcludeButton(@Param("roleId") Long roleId);

    /**
     * 菜单树 - 左侧菜单 - 排除按钮 - 已授权
     */
    List<SysMenu> getAuthSubSysMenuTreeExcludeButton(@Param("menuId") Long menuId,@Param("roleId") Long roleId);
}
