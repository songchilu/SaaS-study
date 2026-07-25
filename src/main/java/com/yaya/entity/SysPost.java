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
 * 岗位表 - sys_post
 */
@Data
@TableName(value = "sys_post")
public class SysPost implements Serializable {
    @Schema(description = "岗位ID")
    @TableId(value = "post_id",type = IdType.AUTO)
    private Long postId;

    @Schema(description = "岗位名称")
    @TableField(value = "post_name")
    private String postName;

    @Schema(description = "岗位编号")
    @TableField(value = "post_code")
    private String postCode;

    @Schema(description = "部门ID(模拟租户ID)")
    @TableField(value = "dept_id")
    private Long deptId;

    @Schema(description = "状态(1-正常 0-停用)")
    @TableField(value = "`status`")
    private Integer status;

    @Schema(description = "逻辑删除标识(1-已删除 0-未删除)")
    @TableField(value = "is_deleted")
    private Integer isDeleted;

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

    @Schema(description = "租户名称")
    @TableField(exist = false)
    private String deptName;
}
