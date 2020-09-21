package com.lxs.bigdata.es.exception;

import com.lxs.bigdata.es.common.ResultCode;

public class SearchResultBuildException extends ESException {

    public SearchResultBuildException(String message) {
        super(String.valueOf(ResultCode.FAILED.getCode()), message);
    }

    public SearchResultBuildException(Throwable throwable) {
        super(throwable);
    }
}
