package com.yaya.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 自定义异常
 */
@Setter
@Getter
public class GlobalCommonException extends RuntimeException{

    private int code=0; //状态码 0:成功 -1:系统异常

    public GlobalCommonException(int code, String message) {
        super(message);
        this.code = code;
    }

    public GlobalCommonException(String message) {
        super(message);
        this.code = -1;
    }
}
