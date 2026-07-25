package com.yaya.service.impl;

import com.yaya.entity.SysDepartment;
import com.yaya.mapper.SysDepartmentMapper;
import com.yaya.service.OnlineUserService;
import com.yaya.util.RedisClient;
import com.yaya.util.SecurityUtils;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class OnlineUserServiceImpl implements OnlineUserService {

    @Resource
    private SysDepartmentMapper sysDepartmentMapper;
    @Resource
    private RedisClient redisClient;
    // 在线心跳key前缀
    public static final String ONLINE_HEARTBEAT = "online:heartbeat:dept:%s:user:%s";
    // 心跳过期时间 60秒
    public static final long HEARTBEAT_TTL_SECONDS = 60L;



    @Override
    public void heartbeat() {
        //当前用户部门
        Long deptId = SecurityUtils.getDeptId();
        //System.out.println("deptId:"+deptId);
        //当前用户租户
        SysDepartment department = sysDepartmentMapper.getTopDepartmentByDeptId(deptId);
        //租户ID
        Long tenantId = department.getDeptId();
        //System.out.println("tenantId:"+tenantId);
        //构建key
        String key = String.format(ONLINE_HEARTBEAT,tenantId,SecurityUtils.getUserId());
        redisClient.set(key,"online",HEARTBEAT_TTL_SECONDS);//60秒过期
    }

    @Override
    public Integer getOnlineUserCount() {
        //当前用户部门
        Long deptId = SecurityUtils.getDeptId();
        //当前用户租户
        SysDepartment department = sysDepartmentMapper.getTopDepartmentByDeptId(deptId);
        //租户ID
        Long tenantId = department.getDeptId();

        //如果为平台管理那么为全部,其它为租户
        Boolean b = SecurityUtils.isRootOrAdminOrOperation();
        if(b){
            //全部数据
            String heartbeat = "online:heartbeat:dept:*";
            List<String> keys = redisClient.findKeys(heartbeat);
            return CollectionUtils.isEmpty(keys)?0:keys.size();
        }else {
            //基于租户
            String dept = String.format(ONLINE_HEARTBEAT, tenantId, "*");
            List<String> keys = redisClient.findKeys(dept);
            return CollectionUtils.isEmpty(keys)?0:keys.size();
        }
    }
}
