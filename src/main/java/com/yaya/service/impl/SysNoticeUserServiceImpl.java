package com.yaya.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.entity.SysNoticeUser;
import com.yaya.entity.SysUser;
import com.yaya.mapper.SysNoticeUserMapper;
import com.yaya.mapper.SysUserMapper;
import com.yaya.model.resp.SysNoticeResp;
import com.yaya.service.SysNoticeUserService;
import com.yaya.util.SecurityUtils;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Service
public class SysNoticeUserServiceImpl implements SysNoticeUserService {

    @Resource
    private SysNoticeUserMapper sysNoticeUserMapper;
    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public void addSysNoticeUser(Long noticeId, List<Long> deptIds) {
        //删除原来
        sysNoticeUserMapper.delete(new LambdaQueryWrapper<SysNoticeUser>()
                .eq(SysNoticeUser::getNoticeId, noticeId)
        );
        //重新插入
        if(CollectionUtils.isNotEmpty(deptIds)){
            deptIds.forEach(deptId->{
                List<SysUser> sysUsers = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getDeptId, deptId)
                        .eq(SysUser::getIsDeleted, 0)
                );
                if(CollectionUtils.isNotEmpty(sysUsers)){
                    List<Long> userIds = sysUsers.stream().map(SysUser::getUserId).distinct().toList();
                    userIds.forEach(userId->{
                        SysNoticeUser sysNoticeUser = new SysNoticeUser();
                        sysNoticeUser.setUserId(userId);
                        sysNoticeUser.setDeptId(deptId);
                        sysNoticeUser.setNoticeId(noticeId);
                        sysNoticeUserMapper.insert(sysNoticeUser);
                    });
                }
            });
        }
    }

    @Override
    public void readSysNoticeUser(Long noticeId) {
        Long userId = SecurityUtils.getUserId();
        //查询当前用户的公告
        SysNoticeUser sysNoticeUser = sysNoticeUserMapper.selectOne(new LambdaQueryWrapper<SysNoticeUser>()
                .eq(SysNoticeUser::getNoticeId, noticeId)
                .eq(SysNoticeUser::getUserId, userId)
        );
        //当前用户阅读公告
        if(sysNoticeUser != null){
            sysNoticeUser.setIsRead(1);
            sysNoticeUser.setReadTime(LocalDateTime.now());
            sysNoticeUserMapper.update(sysNoticeUser,new LambdaQueryWrapper<SysNoticeUser>()
                    .eq(SysNoticeUser::getNoticeId, noticeId)
                    .eq(SysNoticeUser::getUserId, userId)
            );
        }
    }

    @Override
    public List<Long> getDeptIdsByNoticeId(Long noticeId) {
        List<SysNoticeUser> sysNoticeUsers = sysNoticeUserMapper.selectList(new LambdaQueryWrapper<SysNoticeUser>().eq(SysNoticeUser::getNoticeId, noticeId));
        if(CollectionUtils.isNotEmpty(sysNoticeUsers)){
            return sysNoticeUsers.stream().map(SysNoticeUser::getDeptId).distinct().toList();
        }
        return List.of();
    }

    @Override
    public IPage<SysNoticeResp> getMySysNoticePage(Page<SysNoticeResp> page,String noticeTitle,Integer isRead) {
        return sysNoticeUserMapper.getMySysNoticePage(page,SecurityUtils.getUserId(),noticeTitle,isRead);
    }

    @Override
    public SysNoticeResp getMySysNoticeByNoticeId(Long noticeId) {
        Long userId = SecurityUtils.getUserId();
        return sysNoticeUserMapper.getMySysNoticeByNoticeId(noticeId,userId);
    }
}
