// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.lxs.bigdata.es.core.bulk.listener;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.common.logging.Loggers;

public class LoggingBulkProcessorListener implements BulkProcessor.Listener {

    private static final Logger log = Loggers.getLogger(LoggingBulkProcessorListener.class, LoggingBulkProcessorListener.class.getName());

    public LoggingBulkProcessorListener() {
    }

    @Override
    public void beforeBulk(long executionId, BulkRequest request) {
        if(log.isDebugEnabled()) {
            log.debug("ExecutionId = {}, Actions = {}, Estimated Size = {}", executionId, request.numberOfActions(), request.estimatedSizeInBytes());
        }
    }

    @Override
    public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
        if(log.isDebugEnabled()) {
            log.debug("ExecutionId = {}, Actions = {}, Estimated Size = {}", executionId, request.numberOfActions(), request.estimatedSizeInBytes());
        }
    }

    @Override
    public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
        if(log.isErrorEnabled()) {
            log.error("ExecutionId = {}, Error = {}", executionId, failure);
        }
    }
}
