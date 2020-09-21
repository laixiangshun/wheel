package com.lxs.bigdata.es.exception;

import com.lxs.bigdata.es.common.ResultCode;

public class KafkaConsumerException extends ESException {

    public KafkaConsumerException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public KafkaConsumerException(Throwable throwable) {
        super(ResultCode.FAILED.getMessage(), throwable);
    }
}
