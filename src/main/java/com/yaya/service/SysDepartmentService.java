package com.yaya.service;

import com.yaya.entity.SysDepartment;
import com.yaya.model.form.SysDepartmentForm;

import java.util.List;

/**
 * 部门管理-业务逻辑层
 */
public interface SysDepartmentService {

    /**
     * 添加部门
     */
    void addSysDepartment(SysDepartmentForm sysDepartmentForm);
    /**
     * 删除部门
     */
    void deleteSysDepartment(List<Long> departmentIds);
    /**
     * 更新部门
     */
    void updateSysDepartment(SysDepartmentForm sysDepartmentForm);
    /**
     * 一级部门列表(模拟租户列表)
     */
    List<SysDepartment> getOneLevelSysDepartment();
    /**
     * 查询部门树
     */
    List<SysDepartment> getSysDepartmentTree(String deptName,Integer status);
}
