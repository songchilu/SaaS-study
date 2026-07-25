package com.yaya.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.entity.SysRole;
import org.apache.ibatis.annotations.Param;

/**
 * 角色持久化层
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 角色分页
     * @param page          分页
     * @param deptId        部门ID(租户ID)
     * @param roleName      角色名称
     * @param roleType      角色类型(0-普通角色 1-管理角色)
     * @param status        状态(1-正常 0-停用)
     * @return              角色分页
     */
    Page<SysRole> getSysRolePage(Page<SysRole> page,@Param("deptId") Long deptId,@Param("roleName") String roleName,@Param("roleType") Integer roleType,@Param("status") Integer status);

}
