package com.yaya.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.entity.SysLog;
import com.yaya.model.resp.SysUserUvPvResp;

import java.util.List;
import java.util.Map;

public interface SysLogService {

    /**
     * 日志分页
     * @param page 页
     * @param requestUrl   请求地址
     * @param username      用户账号
     * @param ip      ip地址
     * @return  分页
     */
    IPage<SysLog> getSysLogPage(Page<SysLog> page, Integer logType,String requestUrl,String username,String ip,String trackId);

    /**
     * 今日访客
     * 今天比昨天多几个
     */
    Map<String,Object> getTodayVisitor();

    /**
     * 今日浏览量
     * 今日比昨天多几个
     */
    Map<String,Object> getTodayPageViews();

    /**
     * 图表
     */
    List<SysUserUvPvResp> getChartSysUserUvPv();
}
