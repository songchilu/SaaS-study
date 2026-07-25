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
 * 角色表 - sys_role
 */
@Data
@TableName(value = "sys_role")
public class SysRole implements Serializable {

    @Schema(description = "角色ID")
    @TableId(value = "role_id",type = IdType.AUTO)
    private Long roleId;

    @Schema(description = "角色名称")
    @TableField(value = "`role_name`")
    private String roleName;

    @Schema(description = "角色编号")
    @TableField(value = "`role_code`")
    private String roleCode;

    @Schema(description = "部门ID")
    @TableField(value = "`dept_id`")
    private Long deptId;

    @Schema(description = "角色类型(0-普通角色 1-管理角色)")
    @TableField(value = "`role_type`")
    private Integer roleType;

    @Schema(description = "数据权限(1-所有数据 2-部门及子部门数据 3-本部门数据 4-本人数据 5-自定义部门数据)")
    @TableField(value = "`data_scope`")
    private Integer dataScope;

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

    /**
     * 角色所属的部门(模拟租户)
     */
    @TableField(exist = false)
    private SysDepartment sysDepartment;

    /**
     * 如果数据权限为自定义部门，此为角色分配的部门权限
     */
    @TableField(exist = false)
    private List<SysRoleDept> sysRoleDeptList;
}
