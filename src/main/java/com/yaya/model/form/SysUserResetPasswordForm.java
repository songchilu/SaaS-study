package com.yaya.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 表单操作的实体类
 * 批量重置密码
 */
@Data
public class SysUserResetPasswordForm {

    @Schema(description = "用户ID")
    private List<Long> userIds;

    @Schema(description = "要重置的密码")
    private String resetPassword;
}
