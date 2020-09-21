package com.lxs.bigdata.es.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lxs.bigdata.es.common.OperationTypeEnum;
import com.lxs.bigdata.es.core.ElasticSearchClient;
import com.lxs.bigdata.es.core.bulk.configuration.BulkProcessorConfiguration;
import com.lxs.bigdata.es.core.bulk.options.BulkProcessingOptions;
import com.lxs.bigdata.es.dto.ESBulkConditionDTO;
import com.lxs.bigdata.es.utils.ElasticSearchUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * elasticsearch 批量插入
 *
 * @author lxs
 */
@Slf4j
@Service
public class ElasticsearchBulkService {

    private PreBuiltTransportClient client;

    private Gson gson;

    private BulkProcessorConfiguration configuration;

    @Autowired
    public ElasticsearchBulkService(PreBuiltTransportClient client, Gson gson) {
        this.client = client;
        this.gson = gson;
    }

    @PostConstruct
    public void init() {
        BulkProcessingOptions options = BulkProcessingOptions.builder()
                // 5MB的数据刷新一次bulk
                .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))
                //1w次请求执行一次bulk
                .setBulkActions(10000)
                // 固定5s必须刷新一次
                .setFlushInterval(TimeValue.timeValueSeconds(5))
                // 并发请求数量, 0不并发, 1并发允许执行
                .setConcurrentRequests(1)
                // 设置退避, 100ms后执行, 最大请求3次
                .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
                .build();
        configuration = new BulkProcessorConfiguration(options);
    }

    /**
     * 处理kafka中的数据，将其按操作类型更新到elasticsearch中
     */
    public void processData(List<ESBulkConditionDTO> dataList) {
        Map<String, Map<String, List<String>>> indexAddMap = new HashMap<>(dataList.size());
        Map<String, Map<String, List<String>>> indexUpdateMap = new HashMap<>(dataList.size());
        Map<String, Map<String, List<String>>> indexDeleteMap = new HashMap<>(dataList.size());
        for (ESBulkConditionDTO esBulkCondition : dataList) {
            Integer operationType = esBulkCondition.getOperationType();
            OperationTypeEnum operationTypeEnum = OperationTypeEnum.getOperationType(operationType);
            switch (operationTypeEnum) {
                case ADD:
                    addData2Map(indexAddMap, esBulkCondition);
                    break;
                case DELETE:
                    addData2Map(indexDeleteMap, esBulkCondition);
                    break;
                case UPDATE:
                    addData2Map(indexUpdateMap, esBulkCondition);
                    break;
                default:
                    break;
            }
        }
        //批量添加数据
        createData2ElasticSearch(indexAddMap, OperationTypeEnum.ADD.getCode(), configuration);
        //批量更新数据
        createData2ElasticSearch(indexUpdateMap, OperationTypeEnum.UPDATE.getCode(), configuration);
        //批量删除数据
        createData2ElasticSearch(indexDeleteMap, OperationTypeEnum.DELETE.getCode(), configuration);
    }

    /**
     * 添加数据到指定的分类操作Map中
     */
    private void addData2Map(Map<String, Map<String, List<String>>> dataMap, ESBulkConditionDTO bulkCondition) {
        String index = bulkCondition.getIndex();
        String type = bulkCondition.getType();
        Map<String, List<String>> typeMap;
        if (dataMap.containsKey(index)) {
            typeMap = dataMap.get(index);
        } else {
            typeMap = new HashMap<>(dataMap.size());
        }
        List<String> conditionList;
        if (typeMap.containsKey(type)) {
            conditionList = typeMap.get(type);
        } else {
            conditionList = new ArrayList<>(typeMap.size());
        }
        conditionList.add(bulkCondition.getJson());
        typeMap.put(type, conditionList);
        dataMap.put(index, typeMap);
    }

    /**
     * 保存数据到elasticsearch
     */
    private void createData2ElasticSearch(Map<String, Map<String, List<String>>> dataMap,
                                          Integer operationType,
                                          BulkProcessorConfiguration configuration) {
        boolean indexExist = false;
        for (Map.Entry<String, Map<String, List<String>>> entry : dataMap.entrySet()) {
            String index = entry.getKey();
            if (!indexExist) {
                indexExist = createIndex(client, index);
            }
            Map<String, List<String>> typeMap = entry.getValue();
            for (Map.Entry<String, List<String>> typeEntry : typeMap.entrySet()) {
                String type = typeEntry.getKey();
                ElasticSearchClient<String> elasticSearchClient = new ElasticSearchClient<>(client, index, configuration, type);
                List<String> dataList = typeEntry.getValue();
                if (operationType.equals(OperationTypeEnum.ADD.getCode())) {
                    Map<String, String> dataIdMap = getDataMap(dataList);
                    elasticSearchClient.addIndex(dataIdMap);
                } else if (operationType.equals(OperationTypeEnum.UPDATE.getCode())) {
                    Map<String, String> dataIdMap = getDataMap(dataList);
                    elasticSearchClient.updateIndex(dataIdMap);
                } else if (operationType.equals(OperationTypeEnum.DELETE.getCode())) {
                    List<String> dataIdList = new ArrayList<>(dataList.size());
                    dataList.forEach(json -> {
                        JsonObject fromJson = gson.fromJson(json, JsonObject.class);
                        JsonElement jsonElement = fromJson.get("id");
                        String id = jsonElement.getAsString();
                        dataIdList.add(id);
                    });
                    Stream<String> dataStream = dataIdList.stream();
                    elasticSearchClient.deleteIndex(dataStream);
                }
                elasticSearchClient.flush();
            }
        }
    }

    private Map<String, String> getDataMap(List<String> jsonList) {
        Map<String, String> dataMap = new HashMap<>(jsonList.size());
        jsonList.forEach(json -> {
            JsonObject fromJson = gson.fromJson(json, JsonObject.class);
            JsonElement jsonElement = fromJson.get("id");
            String id = jsonElement.getAsString();
            dataMap.putIfAbsent(id, json);
        });
        return dataMap;
    }

    /**
     * 判断索引是否存在
     */
    private boolean createIndex(PreBuiltTransportClient client, String indexName) {
        if (!ElasticSearchUtils.indexExist(client, indexName)) {
            ElasticSearchUtils.createIndex(client, indexName);
        }
        return true;
    }
}
