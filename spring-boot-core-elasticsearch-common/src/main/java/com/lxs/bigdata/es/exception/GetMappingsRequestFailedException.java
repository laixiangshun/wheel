package com.lxs.bigdata.es.exception;

public class GetMappingsRequestFailedException extends ESException {

    public GetMappingsRequestFailedException(String indexName, Throwable cause) {
        super(String.format("Indices '%s' failed", indexName), cause);
    }
}