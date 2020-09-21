// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.lxs.bigdata.es.core.bulk.configuration;

import com.lxs.bigdata.es.core.bulk.listener.LoggingBulkProcessorListener;
import com.lxs.bigdata.es.core.bulk.options.BulkProcessingOptions;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class BulkProcessorConfiguration {

    private BulkProcessingOptions options;

    private BulkProcessor.Listener listener;

    public BulkProcessorConfiguration(BulkProcessingOptions options) {
        this(options, new LoggingBulkProcessorListener());
    }

    public BulkProcessorConfiguration(BulkProcessingOptions options, BulkProcessor.Listener listener) {
        this.options = options;
        this.listener = listener;
    }

    public BulkProcessingOptions getBulkProcessingOptions() {
        return options;
    }

    public BulkProcessor.Listener getBulkProcessorListener() {
        return listener;
    }

    public BulkProcessor build(final PreBuiltTransportClient client) {

        return BulkProcessor.builder(client::bulk, listener)
                .setConcurrentRequests(options.getConcurrentRequests())
                .setBulkActions(options.getBulkActions())
                .setBulkSize(options.getBulkSize())
                .setFlushInterval(options.getFlushInterval())
                .setBackoffPolicy(options.getBackoffPolicy())
                .build();
    }
}