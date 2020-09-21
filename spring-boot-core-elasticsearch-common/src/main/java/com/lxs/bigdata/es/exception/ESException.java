package com.lxs.bigdata.es.exception;

import com.lxs.bigdata.es.common.ResultCode;

import java.util.UUID;

public class ESException extends RuntimeException {

    private String code;
    //友好提示
    private String msg;
    //错误提示
    private String errorMsg;
    //追踪id
    private String traceId;

    public ESException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
        this.traceId = generateTraceId();
    }

    public ESException(Throwable throwable) {
        super(throwable);
        this.code = String.valueOf(ResultCode.FAILED.getCode());
        this.msg = ResultCode.FAILED.getMessage();
        this.traceId = generateTraceId();
    }

    public ESException(String msg, Throwable throwable) {
        super(throwable);
        this.code = String.valueOf(ResultCode.FAILED.getCode());
        this.msg = msg;
        this.traceId = generateTraceId();
    }

    public ESException(String code, String msg, String errorMsg) {
        super(errorMsg);
        this.code = code;
        this.msg = msg;
        this.errorMsg = errorMsg;
        this.traceId = generateTraceId();
    }

    public ESException(String code, String msg, String errorMsg, Throwable throwable) {
        super(errorMsg, throwable);
        this.code = code;
        this.msg = msg;
        this.errorMsg = errorMsg;
        this.traceId = generateTraceId();
    }

    public ESException() {
        this.traceId = generateTraceId();
    }

    private String generateTraceId() {
        return UUID.randomUUID().toString();
    }
}
