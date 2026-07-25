package com.yaya.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.annotation.LogCollect;
import com.yaya.annotation.RepeatSubmit;
import com.yaya.model.Result;
import com.yaya.model.resp.SysNoticeResp;
import com.yaya.service.SysNoticeUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "我的公告管理")
@RestController
public class SysNoticeUserController {
    @Resource
    private SysNoticeUserService sysNoticeUserService;

    @LogCollect(module = "公告管理-公告分配",logRequest = true)
    @Operation(summary = "消息发送给指定部门的用户")
    @Parameters(value = {
            @Parameter(name = "noticeId",description = "公告ID",required=true),
            @Parameter(name = "deptIds",description = "部门ID列表,格式为: 1,2,3,4",required=true)
    })
    @RepeatSubmit(message = "网络延时,请勿重复点击分配")
    @PostMapping(value = "/addSysNoticeUser")
    public Result<Object> addSysNoticeUser(@RequestParam(value = "noticeId") Long noticeId,@RequestParam(value = "deptIds") List<Long> deptIds){
        sysNoticeUserService.addSysNoticeUser(noticeId,deptIds);
        return Result.ok();
    }

    @Operation(summary = "我的公告详情")
    @PostMapping(value = "/getMySysNoticeByNoticeId")
    public Result<SysNoticeResp> getMySysNoticeByNoticeId(@Parameter(name = "noticeId",description = "公告ID",required=true) @RequestParam(value = "noticeId") Long noticeId){
        sysNoticeUserService.readSysNoticeUser(noticeId);
        return Result.ok(sysNoticeUserService.getMySysNoticeByNoticeId(noticeId));
    }

    @Operation(summary = "公告分配的部门ID列表")
    @PostMapping(value = "/getDeptIdsByNoticeId")
    public Result<List<Long>> getDeptIdsByNoticeId(@Parameter(name = "noticeId",description = "公告ID",required=true) @RequestParam(value = "noticeId") Long noticeId){
        return Result.ok(sysNoticeUserService.getDeptIdsByNoticeId(noticeId));
    }

    @Operation(summary = "我的公告列表")
    @Parameters(value = {
        @Parameter(name = "pageNo",description = "当前页",required = true),
        @Parameter(name = "pageSize",description = "页容量",required = true),
        @Parameter(name = "noticeTitle",description = "公告标题"),
        @Parameter(name = "isRead",description = "读取状态(0-未读, 1-已读)")
    })
    @PostMapping(value = "/getMySysNoticePage")
    public Result<IPage<SysNoticeResp>> getMySysNoticePage(@RequestParam(value = "pageNo") Integer pageNo,
                                                           @RequestParam(value = "pageSize") Integer pageSize,
                                                           @RequestParam(value = "noticeTitle",required = false)String noticeTitle,
                                                           @RequestParam(value = "isRead",required = false)Integer isRead){
        return Result.ok(sysNoticeUserService.getMySysNoticePage(new Page<>(pageNo,pageSize),noticeTitle,isRead));
    }



}
