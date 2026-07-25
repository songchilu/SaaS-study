package com.yaya.controller;

import com.yaya.annotation.LogCollect;
import com.yaya.annotation.RepeatSubmit;
import com.yaya.entity.SysMenu;
import com.yaya.model.Result;
import com.yaya.service.SysMenuService;
import com.yaya.service.SysRoleMenuService;
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

@Tag(name = "菜单管理")
@RestController
public class SysMenuController {

    @Resource
    private SysMenuService sysMenuService;
    @Resource
    private SysRoleMenuService sysRoleMenuService;


    @LogCollect(module = "菜单管理-添加菜单",logRequest = true)
    @Operation(summary = "添加菜单")
    @RepeatSubmit(message = "网络延时,请勿重复点击添加")
    @PostMapping(value = "/addMenu")
    public Result<Object> addMenu(@RequestBody SysMenu sysMenu){
        sysMenuService.addMenu(sysMenu);
        return Result.ok();
    }

    @LogCollect(module = "菜单管理-删除菜单",logRequest = true)
    @Operation(summary = "删除菜单")
    @RepeatSubmit(message = "网络延时,请勿重复点击删除")
    @PostMapping(value = "/deleteMenu")
    public Result<Object> deleteMenu(@Parameter(name = "menuId",description = "菜单ID",required = true) @RequestParam(value = "menuId") Long menuId){
        sysMenuService.deleteMenu(menuId);
        return Result.ok();
    }

    @LogCollect(module = "菜单管理-更新菜单",logRequest = true)
    @Operation(summary = "更新菜单")
    @RepeatSubmit(message = "网络延时,请勿重复点击更新")
    @PostMapping(value = "/updateMenu")
    public Result<Object> updateMenu(@RequestBody SysMenu sysMenu){
        sysMenuService.updateMenu(sysMenu);
        return Result.ok();
    }

    @Operation(summary = "菜单树-管理页面")
    @Parameters(value = {
            @Parameter(name = "menuTitle",description = "菜单名称"),
            @Parameter(name = "status",description = "状态(1-正常 0-停用)")
    })
    @PostMapping(value = "/getMenuTree")
    public Result<List<SysMenu>> getMenuTree(@RequestParam(value = "menuTitle",required = false) String menuTitle,@RequestParam(value = "status",required = false) Integer status){
        return Result.ok(sysMenuService.getMenuTree(menuTitle, status));
    }


    @LogCollect(module = "菜单管理-菜单授权",logRequest = true)
    @RepeatSubmit(message = "网络延时,请勿重复点击授权")
    @Operation(summary = "菜单授权")
    @Parameters(value = {
        @Parameter(name = "roleId",description = "角色ID",required = true),
        @Parameter(name = "menuIds",description = "菜单ID列表 格式为 1,2,3,4,...")
    })
    @PostMapping(value = "/addOrUpdateAuthMenu")
    public Result<Object> addOrUpdateAuthMenu(@RequestParam(value = "roleId") Long roleId,@RequestParam(value = "menuIds",required = false) List<Long> menuIds){
        sysRoleMenuService.addOrUpdateAuthMenu(roleId, menuIds);
        return Result.ok();
    }

    @Operation(summary = "菜单树-左侧菜单栏显示-获取用户授权的菜单树")
    @Parameters(value = {
        @Parameter(name = "menuTitle",description = "菜单名称")
    })
    @PostMapping(value = "/getAuthMenuTree")
    public Result<List<SysMenu>> getAuthMenuTree(@RequestParam(value = "menuTitle",required = false) String menuTitle){
        return Result.ok(sysMenuService.getAuthMenuTree(menuTitle));
    }

    @Operation(summary = "已授权菜单ID列表")
    @PostMapping(value = "/getAuthMenuIdsByRoleId")
    public Result<List<Long>> getAuthMenuIds(@Parameter(name = "roleId",description = "角色ID") @RequestParam(value = "roleId") Long roleId){
        return Result.ok(sysRoleMenuService.getAuthMenuIds(roleId));
    }
}
