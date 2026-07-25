package com.yaya.handler;

import com.yaya.exception.GlobalCommonException;
import com.yaya.model.Result;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Objects;

/**
 * 统一异常处理
 */
@Slf4j
@Hidden
@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;


    /**
     * 统一异常处理方法
     */
    @ExceptionHandler(value = {Exception.class})
    public Result commonProcessException(Exception e){
        log.error("统一异常处理器:",e);
        if(e instanceof GlobalCommonException ex){
            return Result.error(ex.getCode(),ex.getMessage());
        }else if (e instanceof AuthorizationDeniedException ex) {
            return Result.error(ex.getMessage());
        } else if (e instanceof MissingServletRequestParameterException) {
            return Result.error(e.getMessage());
        } else if (e instanceof SQLIntegrityConstraintViolationException) {
            return Result.error(e.getMessage());
        } else if (e instanceof DuplicateKeyException) {
            return Result.error(Objects.requireNonNull(((DuplicateKeyException) e).getRootCause()).getMessage());
        }
        return Result.error("系统异常,请联系管理员"+e.getMessage());
    }

    /**
     * 大文件上传异常处理
     */
    @ExceptionHandler(value = {MaxUploadSizeExceededException.class, SizeLimitExceededException.class})
    public Result maxFileSizeUploadException(MaxUploadSizeExceededException e){
        log.error("文件过大异常",e);
        return Result.error("文件过大,请采用分片上传接口,此接口支持的最大文件:["+maxFileSize+"]");
    }

    /**
     * 资源找不到的异常处理
     */
    @ExceptionHandler(value = {NoResourceFoundException.class})
    public Result maxFileSizeUploadException(NoResourceFoundException e){
        log.error("资源找不到的异常",e);
        return Result.error("资源找不到:"+e.getMessage());
    }

}
