package com.yaya.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 密钥管理表 - key_management
 */
@Data
@TableName(value = "key_management")
public class KeyManagement {

    @Schema(description = "密钥ID")
    @TableId(value = "key_id",type = IdType.AUTO)
    private Long keyId;

    @Schema(description = "部门ID")
    @TableField(value = "dept_id")
    private Long deptId;

    @Schema(description = "公钥内容")
    @TableField(value = "public_key_content")
    private String publicKeyContent;

    @Schema(description = "私钥内容")
    @TableField(value = "private_key_content")
    private String privateKeyContent;

    @Schema(description = "密钥创建时间")
    @TableField(value = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime updateTime;

    @Schema(description = "创建人ID")
    @TableField(value = "create_id")
    private Long createId;

    @Schema(description = "更新人ID")
    @TableField(value = "update_id")
    private Long updateId;

    @Schema(description = "备注信息")
    @TableField(value = "remark")
    private String remark;

    @Schema(description = "部门名称")
    @TableField(exist = false)
    private String deptName;

    @Schema(description = "创建人")
    @TableField(exist = false)
    private String createName;
}
