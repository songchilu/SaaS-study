package com.yaya.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.entity.SysLog;
import com.yaya.model.resp.SysUserUvPvResp;
import org.apache.ibatis.annotations.Param;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 日志久化层
 */
public interface SysLogMapper extends BaseMapper<SysLog> {

    /**
     * 添加 - 异步
     * @param entity 日志
     * @return 影响行数
     */
    @Async
    @Override
    int insert(SysLog entity);

    /**
     * 日志分页
     * @param page 页
     * @param requestUrl    请求地址
     * @param username      用户账号
     * @param ip            ip
     * @param logType      日志类型 1:登陆日志 2:其它操作日志
     * @return  分页
     */
    Page<SysLog> getSysLogPage(Page<SysLog> page,@Param("logType") Integer logType,@Param("requestUrl")  String requestUrl,@Param("username") String username,@Param("ip") String ip,@Param("trackId") String trackId);

    /**
     * 基于时间的浏览量
     */
    long getPageViews(@Param("startTime") LocalDateTime startTime,@Param("endTime") LocalDateTime endTime,@Param("deptId") Long deptId);

    /**
     * 今日访客
     */
    long getVisitor(@Param("startTime") LocalDateTime startTime,@Param("endTime") LocalDateTime endTime,@Param("deptId") Long deptId);


    /**
     * 访问量+浏览量： 基于租户
     */
    List<SysUserUvPvResp> getChartSysUserUvPv(@Param("deptId") Long deptId);
}
