package com.yaya.model.excel;

import cn.hutool.core.annotation.Alias;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用于用户导入
 */
@Data
public class SysUserExcel {

    @Alias(value = "部门编号")
    @Schema(description = "部门编号")
    private String deptCode;

    @Alias(value = "角色编号")
    @Schema(description = "角色编号")
    private String roleCode;

    @Alias(value = "岗位编号")
    @Schema(description = "岗位编号列表,多个用，逗号分割")
    private String postCodes;

    @Alias(value = "用户名(账号)")
    @Schema(description = "用户名(登录账号)")
    private String username;

    @Alias(value = "用户昵称")
    @Schema(description = "用户昵称")
    private String nickname;

    @Alias(value = "手机号")
    @Schema(description = "手机号")
    private String phone;

    @Alias(value = "邮箱")
    @Schema(description = "邮箱")
    private String email;

    @Alias(value = "性别")
    @Schema(description = "性别(1-男,0-女,2-保密)")
    private Integer sex=2;

    @Alias(value = "账号过期时间")
    @Schema(description = "过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime expiredTime;

    @Alias(value = "备注")
    @Schema(description = "备注")
    private String remark;
}
