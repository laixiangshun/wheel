package com.lxs.bigdat.deadqueue.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 系统异常
 */
@Getter
@Setter
public class SystemException extends DeadQueueException {
    public SystemException(String code, String msg) {
        super(code, msg);
    }

    public SystemException(String code, String msg, String errorMsg) {
        super(code, msg, errorMsg);
    }
}
