package com.yaya.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.entity.SysNotice;

/**
 * 消息-业务逻辑层
 */
public interface SysNoticeService {

    /**
     * 添加
     */
    void addSysNotice(SysNotice sysNotice);
    /**
     * 删除
     */
    void deleteSysNotice(Long noticeId);

    /**
     * 更新
     */
    void updateSysNotice(SysNotice sysNotice);

    /**
     * 详情
     */
    SysNotice getSysNoticeById(Long noticeId);
    /**
     * 查询
     */
    IPage<SysNotice> getSysNoticePage(Page<SysNotice> page, String noticeTitle);
}
