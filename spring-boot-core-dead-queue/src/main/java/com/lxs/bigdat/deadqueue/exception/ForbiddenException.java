package com.lxs.bigdat.deadqueue.exception;

/**
 * 权限异常
 */
public class ForbiddenException extends DeadQueueException {

    public ForbiddenException(String code, String msg) {
        super(code, msg);
    }

    public ForbiddenException(String code, String msg, String errorMsg) {
        super(code, msg, errorMsg);
    }
}
