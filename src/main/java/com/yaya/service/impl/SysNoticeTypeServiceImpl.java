package com.yaya.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.entity.SysNotice;
import com.yaya.entity.SysNoticeType;
import com.yaya.entity.SysUser;
import com.yaya.exception.GlobalCommonException;
import com.yaya.mapper.SysDepartmentMapper;
import com.yaya.mapper.SysNoticeMapper;
import com.yaya.mapper.SysNoticeTypeMapper;
import com.yaya.mapper.SysUserMapper;
import com.yaya.service.SysNoticeTypeService;
import com.yaya.util.SecurityUtils;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class SysNoticeTypeServiceImpl implements SysNoticeTypeService {

    @Resource
    private SysNoticeTypeMapper sysNoticeTypeMapper;
    @Resource
    private SysNoticeMapper sysNoticeMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysDepartmentMapper sysDepartmentMapper;

    @Override
    public void addSysNoticeType(SysNoticeType sysNoticeType) {
        sysNoticeType.setUpdateId(SecurityUtils.getUserId());
        sysNoticeType.setCreateId(SecurityUtils.getUserId());
        sysNoticeType.setDeptId(SecurityUtils.getDeptId());
        sysNoticeTypeMapper.insert(sysNoticeType);
    }

    @Override
    public void deleteSysNoticeType(Long noticeTypeId) {
        //判断是否有消息引用,如果没有引用可以删除
        List<SysNotice> sysNotices = sysNoticeMapper.selectList(new LambdaQueryWrapper<SysNotice>()
                .eq(SysNotice::getNoticeTypeId, noticeTypeId)
        );
        if(CollectionUtils.isEmpty(sysNotices)){
            //删除
            sysNoticeTypeMapper.deleteById(noticeTypeId);
        }else {
            throw new GlobalCommonException("类型被引用,不能删除");
        }
    }

    @Override
    public void updateSysNoticeType(SysNoticeType sysNoticeType) {
        sysNoticeType.setUpdateId(SecurityUtils.getUserId());
        sysNoticeTypeMapper.updateById(sysNoticeType);
    }

    @Override
    public IPage<SysNoticeType> getSysNoticeTypePage(Page<SysNoticeType> page, String noticeTypeName) {
        Boolean b = SecurityUtils.isRootOrAdminOrOperation();
        Page<SysNoticeType> sysNoticeTypePage = sysNoticeTypeMapper.selectPage(page, new LambdaQueryWrapper<SysNoticeType>()
                .like(StringUtils.isNotEmpty(noticeTypeName), SysNoticeType::getNoticeTypeName, noticeTypeName)
        );
        if(sysNoticeTypePage!=null && CollectionUtils.isNotEmpty(sysNoticeTypePage.getRecords())){
            sysNoticeTypePage.getRecords().forEach(sysNoticeType -> {
                Long createId = sysNoticeType.getCreateId();
                SysUser sysUser = sysUserMapper.selectById(createId);
                sysNoticeType.setCreateUser(sysUser);
            });
        }
        return sysNoticeTypePage;
    }

    @Override
    public List<SysNoticeType> getSysNoticeTypeList() {
        return sysNoticeTypeMapper.selectList(new LambdaQueryWrapper<>());
    }
}
