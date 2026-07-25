package com.yaya.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户岗位关联表 - sys_user_post
 */
@Data
@TableName(value = "sys_user_post")
public class SysUserPost implements Serializable {
    @Schema(description = "岗位ID")
    @MppMultiId
    @TableField(value = "post_id")
    private Long postId;

    @Schema(description = "用户ID")
    @MppMultiId
    @TableField(value = "user_id")
    private Long userId;
}
