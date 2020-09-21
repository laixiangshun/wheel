// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.lxs.bigdata.es.core;

import com.lxs.bigdata.es.core.bulk.configuration.BulkProcessorConfiguration;
import com.lxs.bigdata.es.utils.JsonUtilities;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ElasticSearchClient<TEntity> implements IElasticSearchClient<TEntity> {

    private final String indexName;

    private final String typeName;

    private final BulkProcessor bulkProcessor;

    public ElasticSearchClient(final PreBuiltTransportClient client,
                               final String indexName,
                               final BulkProcessorConfiguration bulkProcessorConfiguration,
                               final String typeName) {
        this.indexName = indexName;
        this.bulkProcessor = bulkProcessorConfiguration.build(client);
        this.typeName = typeName;
    }

    @Override
    public void addIndex(TEntity entity) {
        addIndex(Collections.singletonList(entity));
    }

    @Override
    public void addIndex(List<TEntity> entities) {
        addIndex(entities.stream());
    }

    @Override
    public void addIndex(Stream<TEntity> entities) {
        entities
                .map(JsonUtilities::convertJsonToBytes)
                .filter(Optional::isPresent)
                .map(x -> createIndexRequest(x.get()))
                .forEach(bulkProcessor::add);
    }

    public void addIndex(Map<String, TEntity> entities) {
        Map<String, Optional<byte[]>> optionalMap = entities.entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, (e) -> JsonUtilities.convertJsonToBytes(e.getValue())));

        optionalMap.entrySet().stream().filter(entry -> {
            Optional<byte[]> valueOption = entry.getValue();
            return valueOption.isPresent();
        }).map(entry -> {
            String id = entry.getKey();
            Optional<byte[]> optional = entry.getValue();
            if (StringUtils.isNotBlank(id)) {
                return createIndexRequest(optional.get(), id);
            } else {
                return createIndexRequest(optional.get());
            }
        }).forEach(bulkProcessor::add);
    }

    public void updateIndex(Map<String, TEntity> entities) {
        Map<String, Optional<byte[]>> optionalMap = entities.entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, (e) -> JsonUtilities.convertJsonToBytes(e.getValue())));

        optionalMap.entrySet().stream().filter(entry -> {
            Optional<byte[]> valueOption = entry.getValue();
            return valueOption.isPresent();
        }).map(entry -> {
            String id = entry.getKey();
            Optional<byte[]> optional = entry.getValue();
            return createUpdateRequest(optional.get(), id);
        }).forEach(bulkProcessor::add);
    }


    public void deleteIndex(Stream<String> entities) {
        entities.map(this::createDeleteRequest)
                .forEach(bulkProcessor::add);
    }

    private IndexRequest createIndexRequest(byte[] messageBytes) {
        IndexRequest request = new IndexRequest();

        request.index(indexName);
        request.type(typeName);
        request.source(new BytesArray(messageBytes), XContentType.JSON);

        return request;
    }

    private IndexRequest createIndexRequest(byte[] messageBytes, String id) {
        IndexRequest request = new IndexRequest();

        request.index(indexName);
        request.type(typeName);
        request.id(id);
        request.source(new BytesArray(messageBytes), XContentType.JSON);

        return request;
    }

    private UpdateRequest createUpdateRequest(byte[] messageBytes, String id) {
        UpdateRequest request = new UpdateRequest();

        request.index(indexName);
        request.type(typeName);
        request.id(id);
        request.doc(new BytesArray(messageBytes), XContentType.JSON);

        return request;
    }

    private DeleteRequest createDeleteRequest(String id) {
        DeleteRequest request = new DeleteRequest();

        request.index(indexName);
        request.type(typeName);
        request.id(id);

        return request;
    }

    @Override
    public void flush() {
        bulkProcessor.flush();
    }

    @Override
    public synchronized boolean awaitClose(long timeout, TimeUnit unit) throws InterruptedException {
        return bulkProcessor.awaitClose(timeout, unit);
    }

    @Override
    public void close() throws Exception {
        bulkProcessor.close();
    }
}
