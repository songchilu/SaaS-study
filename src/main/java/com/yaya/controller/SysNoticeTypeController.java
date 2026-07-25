package com.yaya.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.annotation.LogCollect;
import com.yaya.annotation.RepeatSubmit;
import com.yaya.entity.SysNoticeType;
import com.yaya.model.Result;
import com.yaya.service.SysNoticeTypeService;
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

@Tag(name = "公告类型管理")
@RestController
public class SysNoticeTypeController {

    @Resource
    private SysNoticeTypeService sysNoticeTypeService;


    @LogCollect(module = "公告类型管理-添加公告类型",logRequest = true)
    @Operation(summary = "添加公告类型")
    @RepeatSubmit(message = "网络延时,请勿重复点击添加")
    @PostMapping(value = "/addSysNoticeType")
    public Result<Object> addSysNoticeType(@RequestBody SysNoticeType sysNoticeType) {
        sysNoticeTypeService.addSysNoticeType(sysNoticeType);
        return Result.ok();
    }

    @LogCollect(module = "公告类型管理-删除公告类型",logRequest = true)
    @Operation(summary = "删除公告类型")
    @RepeatSubmit(message = "网络延时,请勿重复点击删除")
    @PostMapping(value = "/deleteSysNoticeType")
    public Result<Object> deleteSysNoticeType(@Parameter(name = "noticeTypeId",description = "消息类型ID") @RequestParam(value = "noticeTypeId") Long noticeTypeId) {
        sysNoticeTypeService.deleteSysNoticeType(noticeTypeId);
        return Result.ok();
    }

    @LogCollect(module = "公告类型管理-更新公告类型",logRequest = true)
    @Operation(summary = "更新公告类型")
    @RepeatSubmit(message = "网络延时,请勿重复点击更新")
    @PostMapping(value = "/updateSysNoticeType")
    public Result<Object> updateSysNoticeType(@RequestBody SysNoticeType sysNoticeType) {
        sysNoticeTypeService.updateSysNoticeType(sysNoticeType);
        return Result.ok();
    }

    @Operation(summary = "公告类型列表")
    @PostMapping(value = "/getSysNoticeTypeList")
    public Result<List<SysNoticeType>> getSysNoticeTypeList() {
        return Result.ok(sysNoticeTypeService.getSysNoticeTypeList());
    }

    @Operation(summary = "公告类型分页")
    @Parameters(value = {
            @Parameter(name = "pageNo",description = "当前页",required = true),
            @Parameter(name = "pageSize",description = "当前页",required = true),
            @Parameter(name = "noticeTypeName",description = "消息类型名称")
    })
    @PostMapping(value = "/getSysNoticeTypePage")
    public Result<IPage<SysNoticeType>> getSysNoticeTypePage(@RequestParam(value = "pageNo") Integer pageNo,
                                                             @RequestParam(value = "pageSize") Integer pageSize,
                                                             @RequestParam(value = "noticeTypeName",required = false) String noticeTypeName ){
        return Result.ok(sysNoticeTypeService.getSysNoticeTypePage(new Page<>(pageNo,pageSize),noticeTypeName));
    }
}
