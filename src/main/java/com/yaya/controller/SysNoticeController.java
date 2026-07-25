package com.yaya.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.annotation.LogCollect;
import com.yaya.annotation.RepeatSubmit;
import com.yaya.entity.SysNotice;
import com.yaya.model.Result;
import com.yaya.service.SysNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "公告管理")
@RestController
public class SysNoticeController {

    @Resource
    private SysNoticeService sysNoticeService;

    @LogCollect(module = "公告管理-发布公告",logRequest = true)
    @Operation(summary = "发布公告")
    @RepeatSubmit(message = "网络延时,请勿重复点击添加")
    @PostMapping(value = "/addSysNotice")
    public Result<Object> addSysNotice(@RequestBody SysNotice sysNotice) {
        sysNoticeService.addSysNotice(sysNotice);
        return Result.ok();
    }

    @LogCollect(module = "公告管理-删除公告",logRequest = true)
    @Operation(summary = "删除公告")
    @RepeatSubmit(message = "网络延时,请勿重复点击删除")
    @PostMapping(value = "/deleteSysNotice")
    public Result<Object> deleteSysNotice(@Parameter(name = "noticeId",description = "消息ID") @RequestParam(value = "noticeId") Long noticeId) {
        sysNoticeService.deleteSysNotice(noticeId);
        return Result.ok();
    }

    @Operation(summary = "公告详情")
    @PostMapping(value = "/getSysNoticeById")
    public Result<SysNotice> getSysNoticeById(@Parameter(name = "noticeId",description = "消息ID") @RequestParam(value = "noticeId") Long noticeId) {
        return Result.ok(sysNoticeService.getSysNoticeById(noticeId));
    }


    @LogCollect(module = "公告管理-更新公告",logRequest = true)
    @Operation(summary = "更新公告")
    @RepeatSubmit(message = "网络延时,请勿重复点击更新")
    @PostMapping(value = "/updateSysNotice")
    public Result<Object> updateSysNotice(@RequestBody SysNotice sysNotice) {
        sysNoticeService.updateSysNotice(sysNotice);
        return Result.ok();
    }

    @Operation(summary = "公告分页")
    @Parameters(value = {
            @Parameter(name = "pageNo",description = "当前页",required = true),
            @Parameter(name = "pageSize",description = "当前页",required = true),
            @Parameter(name = "noticeTitle",description = "消息标题")
    })
    @PostMapping(value = "/getSysNoticePage")
    public Result<IPage<SysNotice>> getSysNoticePage(@RequestParam(value = "pageNo") Integer pageNo,
                                                     @RequestParam(value = "pageSize") Integer pageSize,
                                                     @RequestParam(value = "noticeTitle",required = false) String noticeTitle ){
        return Result.ok(sysNoticeService.getSysNoticePage(new Page<>(pageNo,pageSize),noticeTitle));
    }

}
