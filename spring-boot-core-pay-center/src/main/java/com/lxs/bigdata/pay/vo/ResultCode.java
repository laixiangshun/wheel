package com.lxs.bigdata.pay.vo;

/**
 * 枚举了一些常用API操作码
 *
 * @author lxs
 */
public enum ResultCode implements IErrorCode {
    /**
     * 操作成功
     */
    SUCCESS(0, "操作成功"),
    /**
     * 操作失败
     */
    FAILED(1, "操作失败"),

    /**
     * Elasticsearch操作出错
     */
    ES_FAILED(2, "搜索服务操作出错"),
    /**
     * 操作kafka出错
     */
    KAFKA_FAILED(3, "kafka操作出错"),
    /**
     * 参数检验失败
     */
    VALIDATE_FAILED(4, "参数检验失败"),
    /**
     * 暂未登录或token已经过期
     */
    UNAUTHORIZED(5, "暂未登录或token已经过期"),
    /**
     * 没有相关权限
     */
    FORBIDDEN(6, "没有相关权限");

    private long code;

    private String message;

    ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public long getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
