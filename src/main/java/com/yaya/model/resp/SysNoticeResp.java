package com.yaya.model.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 封装我的公告
 */
@Data
public class SysNoticeResp {

    @Schema(description = "公告ID")
    private Long noticeId;

    @Schema(description = "公告标题")
    private String noticeTitle;

    @Schema(description = "公告级别: 0-普通, 1-重要, 2-紧急")
    private Integer noticeLevel;

    @Schema(description = "公告类型")
    private String noticeTypeName;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "是否置顶(0-否, 1-是)")
    private Integer isTop;

    @Schema(description = "读取状态(0-未读, 1-已读)")
    private Integer isRead;

    @Schema(description = "公告内容")
    private String noticeContent;

    @Schema(description = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Schema(description = "阅读时间")
    private LocalDateTime readTime;
}
