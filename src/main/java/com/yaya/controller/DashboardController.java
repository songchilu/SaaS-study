package com.yaya.controller;

import com.yaya.model.Result;
import com.yaya.model.resp.SysUserUvPvResp;
import com.yaya.service.OnlineUserService;
import com.yaya.service.SysLogService;
import com.yaya.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "首页管理")
@RestController
public class DashboardController {

    @Resource
    private OnlineUserService onlineUserService;
    @Resource
    private SysUserService  sysUserService;
    @Resource
    private SysLogService sysLogService;


    @Operation(summary = "是否在线-心跳检查")
    @PostMapping(value = "/heartbeat")
    public Result<Object> heartbeat(){
        onlineUserService.heartbeat();
        return Result.ok();
    }

    @Operation(summary = "在线人数获取")
    @PostMapping(value = "/getOnlineUserCount")
    public Result<Integer> getOnlineUserCount(){
        return Result.ok(onlineUserService.getOnlineUserCount());
    }

    @Operation(summary = "今日访客(UV)")
    @PostMapping(value = "/getTodayVisitor")
    public Result<Map<String,Object>> getTodayVisitor(){
        return Result.ok(sysLogService.getTodayVisitor());
    }

    @Operation(summary = "今日浏览量(PV)")
    @PostMapping(value = "/getTodayPageViews")
    public Result<Map<String,Object>> getTodayPageViews(){
        return Result.ok(sysLogService.getTodayPageViews());
    }

    @Operation(summary = "系统用户数")
    @PostMapping(value = "/getSysUserCount")
    public Result<Long> getSysUserCount(){
        return Result.ok(sysUserService.getSysUserCount());
    }

    @Operation(summary = "访问量|浏览量图表")
    @PostMapping(value = "/getChartSysUserUvPv")
    public Result<List<SysUserUvPvResp>> getChartSysUserUvPv(){
        return Result.ok(sysLogService.getChartSysUserUvPv());
    }
}
