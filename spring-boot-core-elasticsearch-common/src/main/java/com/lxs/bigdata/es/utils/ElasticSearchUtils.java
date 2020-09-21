// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.lxs.bigdata.es.utils;

import com.lxs.bigdata.es.core.IElasticSearchMapping;
import com.lxs.bigdata.es.exception.CreateIndexFailedException;
import com.lxs.bigdata.es.exception.GetMappingsRequestFailedException;
import com.lxs.bigdata.es.exception.IndicesExistsFailedException;
import com.lxs.bigdata.es.exception.PutMappingFailedException;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.util.Map;

public class ElasticSearchUtils {

    private static final Logger log = Loggers.getLogger(ElasticSearchUtils.class, ElasticSearchUtils.class.getName());

    private ElasticSearchUtils() {

    }

    public static boolean indexExist(PreBuiltTransportClient client, String indexName) {
        try {

            IndicesExistsRequest request = new IndicesExistsRequest(indexName);

            return client.admin().indices().exists(request).actionGet().isExists();

        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Error Checking Index Exist", e);
            }
            throw new IndicesExistsFailedException(indexName, e);
        }
    }

    public static boolean mappingsExist(RestHighLevelClient client, String indexName) {
        try {
            GetMappingsRequest request = new GetMappingsRequest().indices(indexName);
            GetMappingsResponse response = client.indices().getMapping(request, RequestOptions.DEFAULT);

            return responseContainsMappings(response);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Error Getting Mappings", e);
            }
            throw new GetMappingsRequestFailedException(indexName, e);
        }
    }

    private static boolean responseContainsMappings(GetMappingsResponse response) {
        ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetaData>> result = response.mappings();

        if (result == null) {
            return false;
        }

        if (result.isEmpty()) {
            return false;
        }

        return true;
    }

    public static CreateIndexResponse createIndex(PreBuiltTransportClient client, String indexName) {
        try {
            return internalCreateIndex(client, indexName);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Error Creating Index", e);
            }
            throw new CreateIndexFailedException(indexName, e);
        }
    }

    public static void createOrUpdatemapping(RestHighLevelClient client, String indexName, IElasticSearchMapping mapping) {
        if (mappingsExist(client, indexName)) {
            updateMapping(client, indexName, mapping);
        } else {
            putMapping(client, indexName, mapping);
        }
    }

    public static AcknowledgedResponse putMapping(RestHighLevelClient client, String indexName, IElasticSearchMapping mapping) {
        try {
            return internalPutMapping(client, indexName, mapping);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Error Creating Index", e);
            }
            throw new PutMappingFailedException(indexName, e);
        }
    }

    public static AcknowledgedResponse updateMapping(RestHighLevelClient client, String indexName, IElasticSearchMapping mapping) {
        try {
            return internalUpdateMapping(client, indexName, mapping);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Error Updating Index", e);
            }
            throw new PutMappingFailedException(indexName, e);
        }
    }

    private static CreateIndexResponse internalCreateIndex(PreBuiltTransportClient client, String indexName) throws IOException {

        final CreateIndexRequest request = new CreateIndexRequest(indexName);

        final CreateIndexResponse response = client.admin().indices().create(request).actionGet();

//        IndexResponse response = client.prepareIndex(indexName, type).setSource().get();

        if (log.isDebugEnabled()) {
            log.debug("CreatedIndexResponse: isAcknowledged {}", response.isAcknowledged());
        }

        return response;
    }

    private static AcknowledgedResponse internalPutMapping(RestHighLevelClient client, String indexName, IElasticSearchMapping mapping) throws IOException {

        String json = Strings.toString(mapping.getMapping());

        final PutMappingRequest putMappingRequest = new PutMappingRequest(indexName)
                .source(json, XContentType.JSON);

        final AcknowledgedResponse putMappingResponse = client.indices().putMapping(putMappingRequest, RequestOptions.DEFAULT);

        if (log.isDebugEnabled()) {
            log.debug("PutMappingResponse: isAcknowledged {}", putMappingResponse.isAcknowledged());
        }

        return putMappingResponse;
    }

    private static AcknowledgedResponse internalUpdateMapping(RestHighLevelClient client, String indexName, IElasticSearchMapping mapping) throws IOException {

        // An update apparently only takes the "properties" field in Elasticsearch 7.0.
        //
        // So... First let's get a Map:
        Map<String, Object> objectMap = convertToMap(mapping);

        // Supress Warnings, because the cast is safe to do at this point (at least I hope so!):
        @SuppressWarnings("unchecked")
        Map<String, Object> innerMap = (Map<String, Object>) objectMap.get(mapping.getIndexType());

        final PutMappingRequest putMappingRequest = new PutMappingRequest(indexName)
                .source(innerMap);

        final AcknowledgedResponse putMappingResponse = client.indices().putMapping(putMappingRequest, RequestOptions.DEFAULT);

        if (log.isDebugEnabled()) {
            log.debug("PutMappingResponse: isAcknowledged {}", putMappingResponse.isAcknowledged());
        }

        return putMappingResponse;
    }

    private static Map<String, Object> convertToMap(IElasticSearchMapping mapping) {
        // Now get the XContentBuilder, which we have to use:
        XContentBuilder xContentBuilder = mapping.getMapping();

        // Convert the XContent into a Map using the XContentHelper class:
        return XContentHelper.convertToMap(BytesReference.bytes(xContentBuilder), true, xContentBuilder.contentType()).v2();
    }
}
