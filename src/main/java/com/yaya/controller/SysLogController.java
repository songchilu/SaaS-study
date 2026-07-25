package com.yaya.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.entity.SysLog;
import com.yaya.model.Result;
import com.yaya.service.SysLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "日志管理")
@RestController
public class SysLogController {

    @Resource
    private SysLogService sysLogService;


    @Operation(summary = "日志分页")
    @Parameters(value = {
            @Parameter(name = "pageNo",description = "当前页",required = true),
            @Parameter(name = "pageSize",description = "页容量",required = true),
            @Parameter(name = "logType",description = "日志类型(1:登陆日志 2:其它操作日志)"),
            @Parameter(name = "requestUrl",description = "请求地址"),
            @Parameter(name = "username",description = "用户账号"),
            @Parameter(name = "trackId",description = "链路ID")
    })
    @PostMapping(value = "/getSysLogPage")
    public Result<IPage<SysLog>> getSysLogPage(@RequestParam(value = "pageNo") Integer pageNo,
                                               @RequestParam(value = "pageSize") Integer pageSize,
                                               @RequestParam(value = "logType",required = false) Integer logType,
                                               @RequestParam(value = "ip",required = false) String ip,
                                               @RequestParam(value = "requestUrl",required = false) String requestUrl,
                                               @RequestParam(value = "username",required = false) String username,
                                               @RequestParam(value = "trackId",required = false) String trackId){
        return Result.ok(sysLogService.getSysLogPage(new  Page<>(pageNo,pageSize),logType,requestUrl,username,ip,trackId));
    }

}
