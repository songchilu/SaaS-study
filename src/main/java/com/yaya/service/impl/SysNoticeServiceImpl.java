package com.yaya.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.entity.SysDepartment;
import com.yaya.entity.SysNotice;
import com.yaya.entity.SysNoticeType;
import com.yaya.mapper.SysDepartmentMapper;
import com.yaya.mapper.SysNoticeMapper;
import com.yaya.mapper.SysNoticeTypeMapper;
import com.yaya.service.SysNoticeService;
import com.yaya.util.SecurityUtils;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class SysNoticeServiceImpl implements SysNoticeService {

    @Resource
    private SysNoticeMapper sysNoticeMapper;
    @Resource
    private SysNoticeTypeMapper sysNoticeTypeMapper;
    @Resource
    private SysDepartmentMapper sysDepartmentMapper;

    @Override
    public void addSysNotice(SysNotice sysNotice) {
        sysNotice.setDeptId(SecurityUtils.getDeptId());//发布消息部门
        sysNotice.setCreateId(SecurityUtils.getUserId());//发布人
        sysNotice.setUpdateId(SecurityUtils.getUserId());//更新人
        sysNoticeMapper.insert(sysNotice);
    }

    @Override
    public void deleteSysNotice(Long noticeId) {
        sysNoticeMapper.deleteById(noticeId);
    }

    @Override
    public void updateSysNotice(SysNotice sysNotice) {
        sysNotice.setUpdateId(SecurityUtils.getUserId());
        sysNoticeMapper.updateById(sysNotice);
    }

    @Override
    public SysNotice getSysNoticeById(Long noticeId) {
        SysNotice sysNotice = sysNoticeMapper.selectById(noticeId);
        if (sysNotice != null) {
            Long noticeTypeId = sysNotice.getNoticeTypeId();
            SysNoticeType sysNoticeType = sysNoticeTypeMapper.selectById(noticeTypeId);
            sysNotice.setSysNoticeType(sysNoticeType);
            Long deptId = sysNotice.getDeptId();
            SysDepartment sysDepartment = sysDepartmentMapper.selectById(deptId);
            sysNotice.setSysDepartment(sysDepartment);
        }
        return sysNotice;
    }

    @Override
    public IPage<SysNotice> getSysNoticePage(Page<SysNotice> page, String noticeTitle) {
        List<SysNotice> sysNotices = sysNoticeMapper.selectList(page, new LambdaQueryWrapper<SysNotice>()
                .like(StringUtils.isNotEmpty(noticeTitle), SysNotice::getNoticeTitle, noticeTitle));
        if (CollectionUtils.isNotEmpty(sysNotices)) {
            sysNotices.forEach(sysNotice -> {
                Long noticeTypeId = sysNotice.getNoticeTypeId();
                SysNoticeType sysNoticeType = sysNoticeTypeMapper.selectById(noticeTypeId);
                sysNotice.setSysNoticeType(sysNoticeType);
            });
            page.setRecords(sysNotices);
        }
        return page;
    }
}
