package com.yaya.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色菜单关联表 - sys_role_menu
 */
@Data
@TableName(value = "sys_role_menu")
public class SysRoleMenu implements Serializable {

    @Schema(description = "角色ID")
    @MppMultiId
    @TableField(value = "role_id")
    private Long roleId;

    @Schema(description = "菜单ID")
    @MppMultiId
    @TableField(value = "menu_id")
    private Long menuId;
}
