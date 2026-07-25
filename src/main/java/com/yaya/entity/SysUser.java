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
import java.util.List;

/**
 * 用户表 -- sys_user
 */
@Data
@TableName(value = "`sys_user`")
public class SysUser implements Serializable {

    @Schema(description = "用户ID")
    @TableId(value = "user_id",type = IdType.AUTO)
    private Long userId;

    @Schema(description = "部门ID")
    @TableField(value = "dept_id")
    private Long deptId;

    @Schema(description = "角色ID")
    @TableField(value = "role_id")
    private Long roleId;

    @Schema(description = "用户名(登录账号)")
    @TableField(value = "`username`")
    private String username;

    @Schema(description = "密码")
    @TableField(value = "`password`")
    private String password;

    @Schema(description = "昵称")
    @TableField(value = "`nickname`")
    private String nickname;

    @Schema(description = "手机号")
    @TableField(value = "phone")
    private String phone;

    @Schema(description = "邮箱")
    @TableField(value = "email")
    private String email;

    @Schema(description = "性别(1-男,0-女,2-保密)")
    @TableField(value = "sex")
    private Integer sex;

    @Schema(description = "头像地址")
    @TableField(value = "avatar")
    private String avatar;

    @Schema(description = "过期时间")
    @TableField(value = "expired_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime expiredTime;

    @Schema(description = "备注")
    @TableField(value = "remark")
    private String remark;

    @Schema(description = "账号是否被锁定(1-锁定 0-未锁定)")
    @TableField(value = "is_locked")
    private Integer isLocked;

    @Schema(description = "账号是否可用(1-可用 0-不可用)")
    @TableField(value = "is_enabled")
    private Integer isEnabled;


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


    @Schema(description = "部门信息")
    @TableField(exist = false)
    private SysDepartment sysDepartment;

    @Schema(description = "角色信息")
    @TableField(exist = false)
    private SysRole sysRole;


    @Schema(description = "岗位列表")
    @TableField(exist = false)
    private List<SysPost> sysPosts;


    @Schema(description = "创建人")
    @TableField(exist = false)
    private SysUser create;

    @Schema(description = "更新人")
    @TableField(exist = false)
    private SysUser update;


    @Schema(description = "用户的部门全名 从顶层到当前层")
    @TableField(exist = false)
    private String fullNamePath;
}
