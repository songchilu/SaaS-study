package com.yaya.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.annotation.LogCollect;
import com.yaya.entity.KeyManagement;
import com.yaya.model.Result;
import com.yaya.service.KeyManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "密钥管理")
@RestController
public class KeyManagementController {

    @Resource
    private KeyManagementService keyManagementService;

    @LogCollect(module = "密钥管理-创建非对称密钥",logRequest = true)
    @Operation(summary = "创建非对称密钥")
    @Parameters(value = {
            @Parameter(name = "deptId",description = "租户ID"),
            @Parameter(name = "remark",description = "备注")
    })
    @PostMapping(value = "/createKey")
    public Result<Object> createKey(@RequestParam(value = "deptId",required = false) Long deptId,
                                    @RequestParam(value = "remark",required = false) String remark){
        keyManagementService.createKey(deptId,remark);
        return Result.ok();
    }

    @LogCollect(module = "密钥管理-删除密钥",logRequest = true)
    @Operation(summary = "删除密钥")
    @PostMapping(value = "/deleteKeyManagement")
    public Result<Object> deleteKeyManagement(@Parameter(name = "keyId",description = "密钥ID") @RequestParam(value = "keyId",required = false) Long keyId){
        keyManagementService.deleteKeyManagement(keyId);
        return Result.ok();
    }

    @Operation(summary = "密钥分页")
    @Parameters(value = {
            @Parameter(name = "pageNo",description = "当前页",required = true),
            @Parameter(name = "pageSize",description = "当前页",required = true),
            @Parameter(name = "deptName",description = "部门名称")
    })
    @PostMapping(value = "/getKeyManagementPage")
    public Result<IPage<KeyManagement>> getKeyManagementPage(@RequestParam(value = "pageNo") Integer pageNo,
                                                             @RequestParam(value = "pageSize") Integer pageSize,
                                                             @RequestParam(value = "deptName",required = false) String deptName){
        return Result.ok(keyManagementService.getKeyManagementPage(new Page<>(pageNo,pageSize),deptName));
    }
}
