package com.yaya.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.slf4j.MDC;

import java.time.LocalDateTime;

/**
 * 统一响应结果
 */
@Data
public class Result<T> {
    /**
     * 统一响应提示状态码
     */
    @Schema(description = "响应状态码 0:成功 -1:系统异常")
    private int code= 0;
    /**
     * 统一响应提示消息
     */
    @Schema(description = "响应提示消息 0:成功 其它:自定义消息")
    private String msg="成功";
    /**
     * 统一响应结果
     */
    @Schema(description = "响应数据")
    private T data;
    /**
     * 服务器响应时间戳
     */
    @Schema(description = "响应时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * MDC链路追踪码
     */
    @Schema(description = "日志链路追踪码")
    private String trackId = MDC.get("trackId");

    public static <T> Result<T> ok(){
        return new Result<>();
    }

    public static <T> Result<T> ok(T data){
        return new Result<>(data);
    }

    public static <T> Result<T> error(int code,String msg){
        return new Result<>(code,msg);
    }

    public static <T> Result<T> error(String msg){
        return new Result<>(-1,msg);
    }

    public Result() {
    }

    public Result(T data) {
        this.data = data;
    }

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
