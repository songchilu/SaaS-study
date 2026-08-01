package com.yaya.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.yaya.annotation.DataPermission;
import com.yaya.entity.SysNotice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消息-持久化层
 */
public interface SysNoticeMapper extends BaseMapper<SysNotice> {
    @DataPermission
    @Override
    List<SysNotice> selectList(IPage<SysNotice> page,@Param(Constants.WRAPPER) Wrapper<SysNotice> queryWrapper);
}
