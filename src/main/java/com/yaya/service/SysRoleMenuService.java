package com.yaya.service;

import java.util.List;

/**
 * 角色菜单关联 - 业务逻辑层
 */
public interface SysRoleMenuService {

    /**
     * 菜单授权
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     */
    void addOrUpdateAuthMenu(Long roleId, List<Long> menuIds);
    /**
     * 基于角色获取已授权的菜单 - 根菜单 - 用于菜单授权页面回显
     */
    List<Long> getAuthMenuIds(Long roleId);
}
