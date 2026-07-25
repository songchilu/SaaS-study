package com.yaya.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.annotation.LogCollect;
import com.yaya.annotation.RepeatSubmit;
import com.yaya.entity.SysRole;
import com.yaya.model.Result;
import com.yaya.model.form.SysRoleForm;
import com.yaya.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "角色管理")
@RestController
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;

    @LogCollect(module = "角色管理-添加角色",logRequest = true)
    @RepeatSubmit(message = "网络延时,请勿重复点击添加")//避免重复提交,默认值2秒
    @Operation(summary = "添加角色")
    @PostMapping(value = "/addSysRole")
    public Result<Object> addSysRole(@RequestBody SysRoleForm sysRoleForm) {
        sysRoleService.addSysRole(sysRoleForm);
        return Result.ok();
    }

    @LogCollect(module = "角色管理-删除角色",logRequest = true)
    @RepeatSubmit(message = "网络延时,请勿重复点击删除")//避免重复提交,默认值2秒
    @Operation(summary = "删除角色")
    @PostMapping(value = "/deleteSysRole")
    public Result<Object> deleteSysRole(@RequestBody List<Long> roleIds) {
        sysRoleService.deleteSysRole(roleIds);
        return Result.ok();
    }

    @LogCollect(module = "角色管理-更新角色",logRequest = true)
    @RepeatSubmit(message = "网络延时,请勿重复点击更新")//避免重复提交,默认值2秒
    @Operation(summary = "更新角色")
    @PostMapping(value = "/updateSysRole")
    public Result<Object> updateSysRole(@RequestBody SysRoleForm sysRoleForm) {
        sysRoleService.updateSysRole(sysRoleForm);
        return Result.ok();
    }

    @Operation(summary = "角色列表")
    @PostMapping(value = "/getSysRoleList")
    public Result<List<SysRole>> getSysRoleList() {
        return Result.ok(sysRoleService.getSysRoleList());
    }

    @Operation(summary = "角色分页")
    @Parameters(value = {
            @Parameter(name = "pageNo",description = "当前页",required = true),
            @Parameter(name = "pageSize",description = "当前页",required = true),
            @Parameter(name = "deptId",description = "部门ID(租户ID)"),
            @Parameter(name = "roleName",description = "角色名称"),
            @Parameter(name = "roleType",description = "角色类型(0-普通角色 1-管理角色)"),
            @Parameter(name = "status",description = "状态(1-正常 0-停用)")
    })
    @PostMapping(value = "/getSysRolePage")
    public Result<IPage<SysRole>> getSysRolePage(@RequestParam(value = "pageNo") Integer pageNo,
                                                 @RequestParam(value = "pageSize") Integer pageSize,
                                                 @RequestParam(value = "deptId",required = false) Long deptId,
                                                 @RequestParam(value = "roleName",required = false) String roleName,
                                                 @RequestParam(value = "roleType",required = false) Integer roleType,
                                                 @RequestParam(value = "status",required = false) Integer status){
        return Result.ok(sysRoleService.getSysRolePage(new Page<>(pageNo,pageSize),deptId,roleName,roleType,status));
    }

    @Operation(summary = "数据权限列表")
    @PostMapping(value = "/getSysRoleDataScope")
    public Result<List<Map<String,Object>>> getSysRoleDataScope(){
        return Result.ok(sysRoleService.getSysRoleDataScope());
    }
}
