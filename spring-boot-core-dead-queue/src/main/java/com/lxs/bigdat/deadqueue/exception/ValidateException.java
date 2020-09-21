package com.lxs.bigdat.deadqueue.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 参数校验异常
 */
@Getter
@Setter
public class ValidateException extends DeadQueueException{
    public ValidateException(String code, String msg) {
        super(code, msg);
    }

    public ValidateException(String code, String msg, String errorMsg) {
        super(code, msg, errorMsg);
    }
}
