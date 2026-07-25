package com.yaya.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.entity.SysRole;
import com.yaya.model.form.SysRoleForm;

import java.util.List;
import java.util.Map;

/**
 * 角色管理-业务逻辑层
 */
public interface SysRoleService {

    /**
     * 添加角色
     * @param sysRoleForm 角色信息
     */
    void addSysRole(SysRoleForm sysRoleForm);

    /**
     * 删除角色
     * @param roleIds 角色IDs
     */
    void deleteSysRole(List<Long> roleIds);

    /**
     * 更新角色
     * @param sysRoleForm 角色信息
     */
    void updateSysRole(SysRoleForm sysRoleForm);

    /**
     * 角色列表
     * @return  列表
     */
    List<SysRole> getSysRoleList();

    /**
     * @param page          分页配置
     * @param deptId      部门ID(租户ID)
     * @param roleName      角色名称
     * @param roleType      角色类型
     * @param status        角色状态
     * @return 分页
     */
    IPage<SysRole> getSysRolePage(Page<SysRole> page, Long deptId,String roleName,Integer roleType,Integer status);

    /**
     * @return 数据权限列表
     */
    List<Map<String,Object>> getSysRoleDataScope();
}
