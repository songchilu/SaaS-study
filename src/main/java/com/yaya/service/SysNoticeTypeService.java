package com.yaya.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.entity.SysNoticeType;

import java.util.List;

/**
 * 消息类型-业务逻辑层
 */
public interface SysNoticeTypeService {

    /**
     * 添加类型
     */
    void addSysNoticeType(SysNoticeType sysNoticeType);
    /**
     * 删除类型
     */
    void deleteSysNoticeType(Long noticeTypeId);
    /**
     * 更新类型
     */
    void updateSysNoticeType(SysNoticeType sysNoticeType);
    /**
     * 查询类型
     */
    IPage<SysNoticeType> getSysNoticeTypePage(Page<SysNoticeType> page, String noticeTypeName);

    /**
     * 列表
     */
    List<SysNoticeType>  getSysNoticeTypeList();
}
