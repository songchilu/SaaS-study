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
 * 日志表 - sys_log
 */
@Data
@TableName(value = "sys_log")
public class SysLog implements Serializable {

    @Schema(description = "日志ID")
    @TableId(value = "log_id",type = IdType.ASSIGN_UUID)
    private String logId;

    @Schema(description = "业务/模块名称（如：用户管理-新增用户）")
    @TableField(value = "business_name")
    private String businessName;

    @Schema(description = "日志类型 1:登陆日志 2:其它操作日志")
    @TableField(value = "log_type")
    private Integer logType;

    @Schema(description = "部门ID")
    @TableField(value = "dept_id")
    private Long deptId;

    @Schema(description = "操作用户ID")
    @TableField(value = "create_id")
    private Long createId;

    @Schema(description = "请求地址")
    @TableField(value = "request_url")
    private String requestUrl;

    @Schema(description = "请求参数（JSON字符串）")
    @TableField(value = "request_params")
    private String requestParams;

    @Schema(description = "响应结果（JSON字符串）")
    @TableField(value = "response_result")
    private String responseResult;

    @Schema(description = "方法执行耗时（毫秒）")
    @TableField(value = "execution_time")
    private Long executionTime;

    @Schema(description = "请求状态(1-成功 0-失败)")
    @TableField(value = "`status`")
    private Integer status=1;//默认成功

    @Schema(description = "错误堆栈信息")
    @TableField(value = "error_msg")
    private String errorMsg;

    @Schema(description = "操作人IP地址")
    @TableField(value = "ip")
    private String ip;

    @Schema(description = "IP对应的地址")
    @TableField(value = "address")
    private String address;

    @Schema(description = "客户端浏览器")
    @TableField(value = "browser")
    private String browser;

    @Schema(description = "日志链路ID")
    @TableField(value = "track_id")
    private String trackId;

    @Schema(description = "操作时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(value = "oper_time")
    private LocalDateTime operTime;

    @Schema(description = "用户名")
    @TableField(exist = false)
    private String username;

    @Schema(description = "用户昵称")
    @TableField(exist = false)
    private String nickname;
}
