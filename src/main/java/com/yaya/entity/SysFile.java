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

/**
 * 文件管理表 - sys_file
 */
@Data
@TableName(value = "sys_file")
public class SysFile implements Serializable {

    @Schema(description = "文件ID")
    @TableId(value = "file_id",type = IdType.AUTO)
    private Long fileId;

    @Schema(description = "文件名称")
    @TableField(value = "file_name")
    private String fileName;

    @Schema(description = "部门ID")
    @TableField(value = "dept_id")
    private Long deptId;

    @Schema(description = "操作人ID")
    @TableField(value = "create_id")
    private Long createId;

    @Schema(description = "文件保存到服务器的物理地址")
    @TableField(value = "file_local_url")
    private String fileLocalUrl;

    @Schema(description = "访问文件的服务器地址")
    @TableField(value = "file_server_url")
    private String fileServerUrl;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    @Schema(description = "部门名称")
    @TableField(exist = false)
    private String deptName;

    @Schema(description = "上传人")
    @TableField(exist = false)
    private String nickname;
}
