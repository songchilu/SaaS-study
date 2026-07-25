package com.yaya.model.form;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 表单操作的实体类
 * 岗位添加+更新装配岗位提交数据的类
 */
@Data
public class SysPostForm {

    @Schema(description = "岗位ID")
    private Long postId;

    @Schema(description = "岗位名称")
    @NotBlank(message = "岗位名称不能为空")
    private String postName;

    @Schema(description = "岗位编号")
    @NotBlank(message = "岗位编号不能为空")
    private String postCode;

    @Schema(description = "部门ID(模拟租户)")
    @NotNull(message = "部门ID不能为空")
    private Long deptId;

    @Schema(description = "状态(1-正常 0-停用)")
    @NotNull(message = "部门状态不能为空")
    private Integer status;
}
