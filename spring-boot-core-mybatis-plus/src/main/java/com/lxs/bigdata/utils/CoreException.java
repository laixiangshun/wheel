package com.lxs.bigdata.utils;

public class CoreException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CoreException(String message) {
        super(message);
    }

    public CoreException(Throwable throwable) {
        super(throwable);
    }

    public CoreException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
