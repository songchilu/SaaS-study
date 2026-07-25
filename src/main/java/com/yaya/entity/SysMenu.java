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
 * 菜单表 - sys_menu
 */
@Data
@TableName(value = "`sys_menu`")
public class SysMenu implements Serializable {

    @Schema(description = "菜单ID")
    @TableId(value = "menu_id",type = IdType.AUTO)
    private Long menuId;

    @Schema(description = "菜单标题")
    @TableField(value = "menu_title")
    private String menuTitle;

    @Schema(description = "菜单图标")
    @TableField(value = "menu_icon")
    private String menuIcon;

    @Schema(description = "菜单类型 1:目录2:菜单(链接)3:按钮")
    @TableField(value = "menu_type")
    private Integer menuType;

    @Schema(description = "菜单权限,当menu_type为3时有效,为按钮添加权限标识")
    @TableField(value = "perms")
    private String perms;

    @Schema(description = "菜单跳转地址")
    @TableField(value = "menu_url")
    private String menuUrl;

    @Schema(description = "父部门ID")
    @TableField(value = "parent_id")
    private Long parentId;

    @Schema(description = "排序序号")
    @TableField(value = "`sort`")
    private Integer sort;

    @Schema(description = "状态(1-正常 0-停用)")
    @TableField(value = "`status`")
    private Integer status;

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

    @Schema(description = "子菜单")
    @TableField(exist = false)
    private List<SysMenu> children;

    @Schema(description = "节点是否初始展开 true:是 false:否 给默认值true,为了方便前端进行角色菜单授权使用")
    @TableField(exist = false)
    private Boolean spread=true;
}
