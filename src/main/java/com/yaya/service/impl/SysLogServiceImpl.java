package com.yaya.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.entity.SysDepartment;
import com.yaya.entity.SysLog;
import com.yaya.mapper.SysDepartmentMapper;
import com.yaya.mapper.SysLogMapper;
import com.yaya.model.resp.SysUserUvPvResp;
import com.yaya.service.SysLogService;
import com.yaya.util.DesensitizeUtils;
import com.yaya.util.SecurityUtils;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class SysLogServiceImpl implements SysLogService {

    @Resource
    private SysLogMapper sysLogMapper;
    @Resource
    private SysDepartmentMapper sysDepartmentMapper;

    @Override
    public IPage<SysLog> getSysLogPage(Page<SysLog> page,Integer logType,String requestUrl, String username,String ip,String trackId) {
        Page<SysLog> sysLogPage = sysLogMapper.getSysLogPage(page, logType, requestUrl, username, ip,trackId);
        if(sysLogPage!=null && CollectionUtils.isNotEmpty(sysLogPage.getRecords())) {
            sysLogPage.getRecords().forEach(sysLog -> {
                String ip_ = sysLog.getIp();
                if(StringUtils.isNotBlank(ip_)) {
                    //脱敏
                    String s = DesensitizeUtils.desensitizeIp(ip_);
                    sysLog.setIp(s);
                }
                //账号密码脱敏
                Integer logType_ = sysLog.getLogType();
                if(logType_==1) {//登录日志
                    String requestParams = sysLog.getRequestParams();
                    if(StringUtils.isNotBlank(requestParams)) {
                        //反序列化成json
                        List<String> params = JSONUtil.toList(requestParams, String.class);
                        if(CollectionUtils.isNotEmpty(params)) {
                            //将第一个参数和第二个参数脱敏
                            if(params.size() >= 2) {
                                String username_ = params.get(0);
                                if(StringUtils.isNotBlank(username_)) {
                                    String s = DesensitizeUtils.desensitizeStr(username_);
                                    params.set(0, s);
                                }
                                String pwd_ = params.get(1);
                                if(StringUtils.isNotBlank(pwd_)) {
                                    String s = DesensitizeUtils.desensitizeStr(pwd_);
                                    params.set(1, s);
                                }
                            }
                            //序列化
                            String jsonStr = JSONUtil.toJsonStr(params);
                            sysLog.setRequestParams(jsonStr);
                        }
                    }
                }
            });
        }
        return sysLogPage;
    }

    @Override
    public Map<String, Object> getTodayVisitor() {
        Map<String, Object> map = new HashMap<>();
        //今天
        LocalDateTime now = LocalDateTime.now();//当前时间
        LocalDateTime start = LocalDateTime.of(now.getYear(),now.getMonth(),now.getDayOfMonth(),0,0,0); //今天的开始时间

        //昨天
        LocalDateTime beforeDay = now.minusDays(1L);
        LocalDateTime beforeStart = LocalDateTime.of(beforeDay.getYear(),beforeDay.getMonth(),beforeDay.getDayOfMonth(),0,0,0);

        Boolean b = SecurityUtils.isRootOrAdminOrOperation();
        if(b){
            //当天
            long count_today = sysLogMapper.getVisitor(start, now, null);
            long count_before = sysLogMapper.getVisitor(beforeStart, start, null);

            //当天浏览量
            map.put("visitorCount", count_today);
            //增量
            map.put("visitorDiff", (count_today-count_before));
        }else {
            //当前用户部门ID
            Long deptId = SecurityUtils.getDeptId();
            SysDepartment department = sysDepartmentMapper.getTopDepartmentByDeptId(deptId);
            //租户ID
            Long tenantId = department.getDeptId();

            //当天
            long count_today = sysLogMapper.getVisitor(start, now, tenantId);
            long count_before = sysLogMapper.getVisitor(beforeStart, start, tenantId);

            //当天浏览量
            map.put("visitorCount", count_today);
            //增量
            map.put("visitorDiff", (count_today-count_before));
        }
        return map;
    }

    @Override
    public Map<String, Object> getTodayPageViews() {
        Map<String, Object> map = new HashMap<>();
        Boolean b = SecurityUtils.isRootOrAdminOrOperation();

        //今天
        LocalDateTime now = LocalDateTime.now();//当前时间
        LocalDateTime start = LocalDateTime.of(now.getYear(),now.getMonth(),now.getDayOfMonth(),0,0,0); //今天的开始时间

        //昨天
        LocalDateTime beforeDay = now.minusDays(1L);
        LocalDateTime beforeStart = LocalDateTime.of(beforeDay.getYear(),beforeDay.getMonth(),beforeDay.getDayOfMonth(),0,0,0);

        if(b){

            //当天的PV量
            Long count_today = sysLogMapper.selectCount(new LambdaQueryWrapper<SysLog>()
                    .ge(SysLog::getOperTime, start)
                    .le(SysLog::getOperTime, now)
            );
            //昨天pv量
            Long count_before = sysLogMapper.selectCount(new LambdaQueryWrapper<SysLog>()
                    .ge(SysLog::getOperTime, beforeStart)
                    .lt(SysLog::getOperTime, start)
            );
            //当天浏览量
            map.put("pageViewCount", count_today);
            //增量
            map.put("pageViewDiff", (count_today-count_before));
        }else {
            //当前用户部门ID
            Long deptId = SecurityUtils.getDeptId();
            SysDepartment department = sysDepartmentMapper.getTopDepartmentByDeptId(deptId);
            //租户ID
            Long tenantId = department.getDeptId();

            //当天的PV量
            Long count_today = sysLogMapper.getPageViews(start,now,tenantId);
            Long count_before = sysLogMapper.getPageViews(beforeStart,start,tenantId);

            //当天浏览量
            map.put("pageViewCount", count_today);
            //增量
            map.put("pageViewDiff", (count_today-count_before));
        }
        return map;
    }

    @Override
    public List<SysUserUvPvResp> getChartSysUserUvPv() {
        Boolean b = SecurityUtils.isRootOrAdminOrOperation();
        //平台管理员查看全部数据
        if(b){
            List<SysUserUvPvResp> chartSysUserUvPv = sysLogMapper.getChartSysUserUvPv(null);
            chartSysUserUvPv.forEach(sysUserUvPvResp -> {
                String statDate = sysUserUvPvResp.getStatDate();
                //时间格式化  yyyy-MM-dd 格式化成 MM/dd
                LocalDate date = LocalDate.parse(statDate);
                String result = date.format(DateTimeFormatter.ofPattern("MM/dd"));
                sysUserUvPvResp.setStatDate(result);
            });
            return chartSysUserUvPv;
        }else {
            //各个租户用户,只能看见自己租户的数据
            Long deptId = SecurityUtils.getDeptId();
            SysDepartment department = sysDepartmentMapper.getTopDepartmentByDeptId(deptId);
            Long topDeptId = department.getDeptId();
            List<SysUserUvPvResp> chartSysUserUvPv = sysLogMapper.getChartSysUserUvPv(topDeptId);
            chartSysUserUvPv.forEach(sysUserUvPvResp -> {
                String statDate = sysUserUvPvResp.getStatDate();
                //时间格式化  yyyy-MM-dd 格式化成 MM/dd
                LocalDate date = LocalDate.parse(statDate);
                String result = date.format(DateTimeFormatter.ofPattern("MM/dd"));
                sysUserUvPvResp.setStatDate(result);
            });
            return chartSysUserUvPv;
        }
    }
}
