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
 * 部门表 - sys_department
 */
@Data
@TableName(value = "sys_department")
public class SysDepartment implements Serializable {

    @Schema(description = "部门ID")
    @TableId(value = "dept_id",type = IdType.AUTO)
    private Long deptId;

    @Schema(description = "部门名称")
    @TableField(value = "dept_name")
    private String deptName;

    @Schema(description = "部门编号")
    @TableField(value = "dept_code")
    private String deptCode;

    @Schema(description = "部门LOGO")
    @TableField(value = "dept_logo")
    private String deptLogo;

    @Schema(description = "父部门ID")
    @TableField(value = "parent_id")
    private Long parentId;

    @Schema(description = "祖宗部门ID,以逗号分割")
    @TableField(value = "tree_path")
    private String treePath;

    @Schema(description = "序号,用于排序")
    @TableField(value = "`sort`")
    private Integer sort;

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

    //节点是否展开,此属性专门为Layui框架用户管理页，部门导航专用
    @Schema(description = "节点是否展开")
    @TableField(exist = false)
    private Boolean spread=true;

    /**
     * 子部门
     */
    @Schema(description = "子部门")
    @TableField(exist = false)
    private List<SysDepartment> children;
}
