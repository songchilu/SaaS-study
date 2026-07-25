package com.yaya.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.annotation.LogCollect;
import com.yaya.annotation.RepeatSubmit;
import com.yaya.entity.SysPost;
import com.yaya.model.Result;
import com.yaya.model.form.SysPostForm;
import com.yaya.service.SysPostService;
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

@Tag(name = "岗位管理")
@RestController
public class SysPostController {
    @Resource
    private SysPostService sysPostService;

    @LogCollect(module = "岗位管理-添加岗位",logRequest = true)
    @Operation(summary = "添加岗位")
    @RepeatSubmit(message = "网络延时,请勿重复点击添加")
    @PostMapping(value = "/addSysPost")
    public Result<Object> addSysPost(@RequestBody SysPostForm sysPostForm) {
        sysPostService.addSysPost(sysPostForm);
        return Result.ok();
    }

    @LogCollect(module = "岗位管理-删除岗位",logRequest = true)
    @Operation(summary = "删除岗位")
    @RepeatSubmit(message = "网络延时,请勿重复点击删除")
    @PostMapping(value = "/deleteSysPost")
    public Result<Object> deleteSysPost(@RequestBody List<Long> postIds) {
        sysPostService.deleteSysPost(postIds);
        return Result.ok();
    }

    @LogCollect(module = "岗位管理-更新岗位",logRequest = true)
    @Operation(summary = "更新岗位")
    @RepeatSubmit(message = "网络延时,请勿重复点击更新")
    @PostMapping(value = "/updateSysPost")
    public Result<Object> updateSysPost(@RequestBody SysPostForm sysPostForm) {
        sysPostService.updateSysPost(sysPostForm);
        return Result.ok();
    }

    @Operation(summary = "岗位列表")
    @PostMapping(value = "/getSysPostList")
    public Result<List<SysPost>> getSysPostList() {
        return Result.ok(sysPostService.getSysPostList());
    }

    @Operation(summary = "岗位分页")
    @Parameters(value = {
            @Parameter(name = "pageNo",description = "当前页",required = true),
            @Parameter(name = "pageSize",description = "当前页",required = true),
            @Parameter(name = "deptId",description = "部门ID(租户ID)"),
            @Parameter(name = "postName",description = "岗位名称")
    })
    @PostMapping(value = "/getSysPostPage")
    public Result<IPage<SysPost>> getSysPostPage(@RequestParam(value = "pageNo") Integer pageNo,
                                                 @RequestParam(value = "pageSize") Integer pageSize,
                                                 @RequestParam(value = "deptId",required = false) Long deptId,
                                                 @RequestParam(value = "postName",required = false) String postName){
        return Result.ok(sysPostService.getSysPostPage(new Page<>(pageNo,pageSize),postName,deptId));
    }
}
