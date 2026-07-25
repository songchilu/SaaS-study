package com.yaya.model.form;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 表单操作的实体类
 * 用户添加+更新装配用户提交数据的类
 */
@Data
public class SysUserForm {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "用户名(登录账号)")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    @TableField(value = "email")
    private String email;

    @Schema(description = "性别(1-男,0-女,2-保密)")
    private Integer sex;

    @Schema(description = "头像地址")
    private String avatar;

    @Schema(description = "过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime expiredTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "账号是否可用(1-可用 0-不可用)")
    private Integer isEnabled;

    @Schema(description = "岗位IDs,前端传递过来的格式为 1001,2001,3001 ")
    private String postIds;
}
