package com.yaya.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告表 - sys_notice
 */
@Data
@TableName(value = "`sys_notice`")
public class SysNotice implements Serializable {

    @Schema(description = "公告ID")
    @TableId(value = "notice_id",type = IdType.AUTO)
    private Long noticeId;

    @Schema(description = "公告标题")
    @TableField(value = "notice_title")
    private String noticeTitle;

    @Schema(description = "公告内容")
    @TableField(value = "notice_content")
    private String noticeContent;

    @Schema(description = "公告级别: 0-普通, 1-重要, 2-紧急")
    @TableField(value = "notice_level")
    private Integer noticeLevel;

    @Schema(description = "公告类型")
    @TableField(value = "notice_type_id")
    private Long noticeTypeId;

    @Schema(description = "部门ID")
    @TableField(value = "dept_id")
    private Long deptId;

    @Schema(description = "是否置顶(0-否, 1-是)")
    @TableField(value = "is_top")
    private Integer isTop;

    @Schema(description = "创建人ID")
    @TableField(value = "create_id")
    private Long createId;

    @Schema(description = "更新人ID")
    @TableField(value = "update_id")
    private Long updateId;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    /**
     * 公告类型信息
     */
    @Schema(description = "公告类型信息")
    @TableField(exist = false)
    private SysNoticeType sysNoticeType;

    /**
     * 发布部门
     */
    @Schema(description = "发布部门")
    @TableField(exist = false)
    private SysDepartment sysDepartment;

    /**
     * 公告的阅读状态
     */
    @Schema(description = "公告的阅读状态")
    @TableField(exist = false)
    private SysNoticeUser  sysNoticeUser;
}
