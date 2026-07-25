package com.yaya.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.model.resp.SysNoticeResp;

import java.util.List;

public interface SysNoticeUserService {
    /**
     * 消息发送给指定部门的用户
     * @param noticeId 消息ID
     * @param deptIds  部门ID列表
     */
    void addSysNoticeUser(Long noticeId,List<Long> deptIds);

    /**
     * 阅读消息状态
     * @param noticeId 消息ID
     */
    void readSysNoticeUser(Long noticeId);

    /**
     * 消息部门回显
     */
    List<Long> getDeptIdsByNoticeId(Long noticeId);

    /**
     * 我的消息分页
     */
    IPage<SysNoticeResp> getMySysNoticePage(Page<SysNoticeResp> page,String noticeTitle,Integer isRead);

    /**
     * @param noticeId 消息ID
     * @return  我的消息详情
     */
    SysNoticeResp getMySysNoticeByNoticeId(Long noticeId);
}
