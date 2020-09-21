package com.lxs.bigdata.es.exception;

import com.lxs.bigdata.es.common.ResultCode;

public class SearchQueryBuildException extends ESException {

    public SearchQueryBuildException(String message) {
        super(String.valueOf(ResultCode.FAILED.getCode()),message);
    }

    public SearchQueryBuildException(Throwable throwable) {
        super(throwable);
    }
}
