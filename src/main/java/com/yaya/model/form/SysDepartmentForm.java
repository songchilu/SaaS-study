package com.yaya.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
/**
 * 表单操作的实体类
 * 部门添加+更新装配部门提交数据的类
 */
@Data
public class SysDepartmentForm {

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "部门名称")
    @NotBlank(message = "部门名称不能为空")
    private String deptName;

    @Schema(description = "部门编号")
    @NotBlank(message = "部门编号不能为空")
    private String deptCode;

    @Schema(description = "父部门ID")
    private Long parentId;

    @Schema(description = "序号,用于排序")
    @NotNull(message = "序号不能为空")
    private Integer sort;

    @Schema(description = "状态(1-正常 0-停用)")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
