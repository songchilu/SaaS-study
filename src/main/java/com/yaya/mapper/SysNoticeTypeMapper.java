package com.yaya.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.yaya.annotation.DataPermission;
import com.yaya.entity.SysNoticeType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消息类型-持久化层
 */
public interface SysNoticeTypeMapper extends BaseMapper<SysNoticeType> {
    @DataPermission
    @Override
    List<SysNoticeType> selectList(IPage<SysNoticeType> page,@Param(Constants.WRAPPER) Wrapper<SysNoticeType> queryWrapper);
}
