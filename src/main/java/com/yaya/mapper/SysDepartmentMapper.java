package com.yaya.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yaya.entity.SysDepartment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门持久化层
 */
public interface SysDepartmentMapper extends BaseMapper<SysDepartment> {

    /**
     * 通过部门ID查询当前部门的最顶层部门信息
     */
    SysDepartment getTopDepartmentByDeptId(@Param("deptId") Long deptId);

    /**
     * 通过部门ID获取当前部门信息以及当前部门的所有后代信息
     */
    List<SysDepartment> getDepartmentAndSubDepartmentList(@Param("deptId") Long deptId,@Param("status") Integer status);

    /**
     * 通过部门ID查询部门名称的链式名称 结构为: 一级名称/二级名称/三级名称/...名称
     */
    String getFullDeptNamePathByDeptId(@Param("deptId") Long deptId);
}
