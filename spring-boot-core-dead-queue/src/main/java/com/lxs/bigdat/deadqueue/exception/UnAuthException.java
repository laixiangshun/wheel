package com.lxs.bigdat.deadqueue.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 登录异常
 */
@Getter
@Setter
public class UnAuthException extends DeadQueueException {
    public UnAuthException(String code, String msg) {
        super(code, msg);
    }

    public UnAuthException(String code, String msg, String errorMsg) {
        super(code, msg, errorMsg);
    }
}
