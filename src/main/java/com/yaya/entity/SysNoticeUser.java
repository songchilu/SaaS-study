package com.yaya.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * 公告用户关联表 - sys_notice_user
 */
@Data
@TableName(value = "`sys_notice_user`")
public class SysNoticeUser implements Serializable {
    @Schema(description = "公告ID")
    @MppMultiId
    @TableField(value = "notice_id")
    private Long noticeId;

    @Schema(description = "用户ID")
    @MppMultiId
    @TableField(value = "user_id")
    private Long userId;

    @Schema(description = "部门ID")
    @TableField(value = "dept_id")
    private Long deptId;

    @Schema(description = "读取状态(0-未读, 1-已读)")
    @TableField(value = "is_read")
    private Integer isRead;

    @Schema(description = "阅读时间")
    @TableField(value = "read_time")
    private LocalDateTime readTime;
}
