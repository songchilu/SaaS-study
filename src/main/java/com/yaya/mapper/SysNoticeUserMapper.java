package com.yaya.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.entity.SysNoticeUser;
import com.yaya.model.resp.SysNoticeResp;
import org.apache.ibatis.annotations.Param;

/**
 * 公告用户关联表 - 持久化层
 */
public interface SysNoticeUserMapper extends BaseMapper<SysNoticeUser> {

    /**
     * 我的消息
     * @param page   分页消息
     * @param isRead 读取状态(0-未读, 1-已读)
     * @return 页数据
     */
    IPage<SysNoticeResp> getMySysNoticePage(Page<SysNoticeResp> page,@Param("userId") Long userId,@Param("noticeTitle") String noticeTitle,@Param("isRead") Integer isRead);

    /**
     * 我的公告详情
     * @param noticeId 公告ID
     * @param userId 用户ID
     * @return 详情
     */
    SysNoticeResp getMySysNoticeByNoticeId(@Param("noticeId")Long noticeId,@Param("userId")Long userId);
}
