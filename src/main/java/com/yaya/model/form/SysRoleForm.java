package com.yaya.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 表单操作的实体类
 * 角色添加+更新装配角色提交数据的类
 */
@Data
public class SysRoleForm {

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "角色名称")
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @Schema(description = "角色编号")
    @NotBlank(message = "角色编号不能为空")
    private String roleCode;

    @Schema(description = "部门ID(模拟租户ID),当前角色从属于哪个租户")
    @NotNull(message = "所属租户不能为空")
    private Long deptId;

    @Schema(description = "角色类型(0-普通角色 1-管理角色)")
    @NotNull(message = "角色类型不能为空")
    private Integer roleType;

    @Schema(description = "数据权限(1-所有数据 2-部门及子部门数据 3-本部门数据 4-本人数据 5-自定义部门数据)")
    @NotNull(message = "数据权限不能为空")
    private Integer dataScope;

    @Schema(description = "状态(1-正常 0-停用)")
    @NotNull(message = "角色状态不能为空")
    private Integer status;

    @Schema(description = "如果数据权限dataScope为5,此属性为部门权限ID列表,格式 1,2,3")
    private String deptIds;
}
