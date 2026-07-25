package com.yaya.controller;

import com.yaya.annotation.LogCollect;
import com.yaya.annotation.RepeatSubmit;
import com.yaya.entity.SysDepartment;
import com.yaya.model.Result;
import com.yaya.model.form.SysDepartmentForm;
import com.yaya.service.SysDepartmentService;
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

@Tag(name = "部门管理")
@RestController
public class SysDepartmentController {

    @Resource
    private SysDepartmentService sysDepartmentService;


    @LogCollect(module = "部门管理-添加部门",logRequest = true)
    @RepeatSubmit()//避免重复提交,默认值2秒
    @Operation(summary = "添加部门")
    @PostMapping(value = "/addSysDepartment")
    public Result<Object> addSysDepartment(@RequestBody SysDepartmentForm sysDepartmentForm) {
        sysDepartmentService.addSysDepartment(sysDepartmentForm);
        return Result.ok();
    }


    @LogCollect(module = "部门管理-删除部门",logRequest = true)
    @RepeatSubmit()//避免重复提交,默认值2秒
    @Operation(summary = "删除部门")
    @PostMapping(value = "/deleteSysDepartment")
    public Result<Object> deleteSysDepartment(@RequestBody List<Long> departmentIds) {
        sysDepartmentService.deleteSysDepartment(departmentIds);
        return Result.ok();
    }
    
    @LogCollect(module = "部门管理-更新部门",logRequest = true)
    @RepeatSubmit()//避免重复提交,默认值2秒
    @Operation(summary = "更新部门")
    @PostMapping(value = "/updateSysDepartment")
    public Result<Object> updateSysDepartment(@RequestBody SysDepartmentForm sysDepartmentForm) {
        sysDepartmentService.updateSysDepartment(sysDepartmentForm);
        return Result.ok();
    }

    @Operation(summary = "部门列表(部门树)")
    @Parameters(value = {
            @Parameter(name = "status",description = "状态(1-正常 0-停用)"),
            @Parameter(name = "deptName",description = "部门名称")
    })
    @PostMapping(value = "/getSysDepartmentTree")
    public Result<List<SysDepartment>> getSysDepartmentTree(@RequestParam(value = "deptName",required = false) String deptName, @RequestParam(value = "status",required = false) Integer status) {
        return Result.ok(sysDepartmentService.getSysDepartmentTree(deptName,status));
    }

    @Operation(summary = "一级部门列表(模拟租户列表)")
    @PostMapping(value = "/getOneLevelSysDepartment")
    public Result<List<SysDepartment>> getOneLevelSysDepartment() {
        return Result.ok(sysDepartmentService.getOneLevelSysDepartment());
    }

}
