package com.yaya.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色部门关联表(自定义数据权限) - sys_role_dept
 */
@Data
@TableName(value = "sys_role_dept")
public class SysRoleDept implements Serializable {
    @Schema(description = "角色ID")
    @MppMultiId
    @TableField(value = "role_id")
    private Long roleId;

    @Schema(description = "部门ID")
    @MppMultiId
    @TableField(value = "dept_id")
    private Long deptId;
}
