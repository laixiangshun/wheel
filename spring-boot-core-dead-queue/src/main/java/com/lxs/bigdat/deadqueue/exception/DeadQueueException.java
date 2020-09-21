package com.lxs.bigdat.deadqueue.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 私信队列异常
 */
@Getter
@Setter
public abstract class DeadQueueException extends RuntimeException {
    private String code;
    private String msg;  //友好提示
    private String errorMsg;  //错误提示

    public DeadQueueException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public DeadQueueException(String code, String msg, String errorMsg) {
        this.code = code;
        this.msg = msg;
        this.errorMsg = errorMsg;
    }

}
