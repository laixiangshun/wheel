package com.lxs.bigdata.es.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lxs.bigdata.es.common.DistanceUnitEnum;
import com.lxs.bigdata.es.core.Constant;
import com.lxs.bigdata.es.core.ElasticSearchClient;
import com.lxs.bigdata.es.core.GroupBy;
import com.lxs.bigdata.es.core.bulk.configuration.BulkProcessorConfiguration;
import com.lxs.bigdata.es.core.bulk.listener.LoggingBulkProcessorListener;
import com.lxs.bigdata.es.core.bulk.options.BulkProcessingOptions;
import com.lxs.bigdata.es.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 检索服务
 *
 * @author lxs
 */
@Slf4j
@Service
public class ElasticSearchService {

    private PreBuiltTransportClient client;

    private Gson gson;

    private BulkProcessorConfiguration configuration;

    @PostConstruct
    public void init() {
        BulkProcessingOptions options = BulkProcessingOptions.builder()
                .setBulkActions(200)
                .setConcurrentRequests(2)
                .setFlushInterval(TimeValue.ZERO)
                .build();
        configuration = new BulkProcessorConfiguration(options);
    }

    @Autowired
    public ElasticSearchService(PreBuiltTransportClient client, Gson gson) {
        this.client = client;
        this.gson = gson;
    }

    /**
     * 根据复杂条件聚合搜索
     *
     * @param condition 条件
     * @return 搜索结果
     */
    public List<JSONObject> searchAggByCondition(ESConditionDTO condition) {
        List<JSONObject> results = new ArrayList<>();
        GroupBy gb = new GroupBy();
        try {
            SearchRequestBuilder searchRequest = client.prepareSearch(condition.getIndex());
            buildSearchRequest(condition, searchRequest);

            BoolQueryBuilder boolQuery = buildBoolQueryBuilder(condition);
            searchRequest.setQuery(boolQuery);

            //查询分组
            String[] groupArr = new String[]{};
            if (StringUtils.isNotBlank(condition.getGroupFields())) {
                groupArr = condition.getGroupFields().split(Constant.SPITSTR1);
                for (String kvField : groupArr) {
                    String[] kv = kvField.split(Constant.SPITSTR2);
                    gb.addSubAgg(kv[0], kv[1]);
                }
            }
            //count统计
            String[] countArr = new String[]{};
            if (StringUtils.isNotBlank(condition.getCountFields())) {
                countArr = condition.getCountFields().split(Constant.SPITSTR1);
                for (String kvField : countArr) {
                    String[] kv = kvField.split(Constant.SPITSTR2);
                    gb.addCountAgg(kv[0], kv[1]);
                }
            }
            //sum统计
            String[] sumArr = new String[]{};
            if (StringUtils.isNotEmpty(condition.getSumFields())) {
                sumArr = condition.getSumFields().split(Constant.SPITSTR1);
                for (String kvField : sumArr) {
                    String[] kv = kvField.split(Constant.SPITSTR2);
                    gb.addSumAgg(kv[0], kv[1]);
                }
            }
            //max统计
            String[] maxArr = new String[]{};
            if (StringUtils.isNotEmpty(condition.getMaxFields())) {
                maxArr = condition.getMaxFields().split(Constant.SPITSTR1);
                for (String kvField : maxArr) {
                    String[] kv = kvField.split(Constant.SPITSTR2);
                    gb.addMaxAgg(kv[0], kv[1]);
                }
            }
            //min统计
            String[] minArr = new String[]{};
            if (StringUtils.isNotEmpty(condition.getMinFields())) {
                minArr = condition.getMinFields().split(Constant.SPITSTR1);
                for (String kvField : minArr) {
                    String[] kv = kvField.split(Constant.SPITSTR2);
                    gb.addMinAgg(kv[0], kv[1]);
                }
            }
            //avg统计
            String[] avgArr = new String[]{};
            if (StringUtils.isNotEmpty(condition.getAvgFields())) {
                avgArr = condition.getAvgFields().split(Constant.SPITSTR1);
                for (String kvField : avgArr) {
                    String[] kv = kvField.split(Constant.SPITSTR2);
                    gb.addAvgAgg(kv[0], kv[1]);
                }
            }
            //聚合
            searchRequest.addAggregation(gb.getTermsBuilder());

            //执行查询
            SearchResponse response = searchRequest.setSearchType(SearchType.QUERY_THEN_FETCH).execute().actionGet();
            Map<String, Aggregation> result = response.getAggregations().asMap();
            results = groupTree(groupArr, 0, result, countArr, maxArr, minArr, sumArr, avgArr);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return results;
    }

    /**
     * 根据条件检索，不进行聚合操作
     */
    public List<JSONObject> searchNoAggByCondition(ESConditionDTO condition) {
        SearchRequestBuilder searchRequest = client.prepareSearch(condition.getIndex());

        buildSearchRequest(condition, searchRequest);
        BoolQueryBuilder boolQuery = buildBoolQueryBuilder(condition);
        searchRequest.setQuery(boolQuery);

        List<JSONObject> sourceList = new ArrayList<>();
        try {
            SearchResponse response = searchRequest
                    .execute()
                    .actionGet();
            SearchHits hits = response.getHits();
            sourceList = new ArrayList<>();
            for (SearchHit sh : hits) {
                JSONObject jsonObject = JSONObject.parseObject(sh.getSourceAsString());
                jsonObject.put("id", sh.getId());
                sourceList.add(jsonObject);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return sourceList;
    }

    /**
     * 根据条件搜索
     * 根据条件判断是否进行聚合操作
     */
    public List<JSONObject> searchByCondition(ESConditionDTO condition) {
        String groupFields = condition.getGroupFields();
        String maxFields = condition.getMaxFields();
        String minFields = condition.getMinFields();
        String avgFields = condition.getAvgFields();
        String sumFields = condition.getSumFields();
        boolean aggCondition = false;
        if (StringUtils.isNotBlank(groupFields) || StringUtils.isNotBlank(maxFields) || StringUtils.isNotBlank(minFields)
                || StringUtils.isNotBlank(avgFields) || StringUtils.isNotBlank(sumFields)) {
            aggCondition = true;
        }
        List<JSONObject> result;
        if (aggCondition) {
            result = searchAggByCondition(condition);
        } else {
            result = searchNoAggByCondition(condition);
        }
        return result;
    }

    private BoolQueryBuilder buildBoolQueryBuilder(ESConditionDTO condition) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        List<RangeParamsDTO> rangeList = condition.getRangeList();
        List<RegexParamsDTO> regexList = condition.getRegexList();
        Map<String, List<String>> inMap = condition.getInField();

        if (rangeList != null && rangeList.size() != 0) {
            for (RangeParamsDTO rp : rangeList) {
                boolQuery.must(
                        QueryBuilders.rangeQuery(rp.getRangeField())
                                .from(rp.getStart())
                                .to(rp.getEnd())
                                .includeLower(rp.isIncludeLower())
                                .includeUpper(rp.isIncludeUpper()));
            }
        }
        if (regexList != null && regexList.size() != 0) {
            for (RegexParamsDTO rp : regexList) {
                boolQuery.must(
                        QueryBuilders.regexpQuery(rp.getRegexfiled(), rp.getRegex()));
            }
        }
        if (StringUtils.isNotEmpty(condition.getMatchStr())) {
            for (String s : condition.getMatchStr().split(Constant.SPITSTR1)) {
                String[] ss = s.split(Constant.SPITSTR2);
                if (ss.length > 1) {
                    boolQuery.must(QueryBuilders.matchQuery(ss[0], ss[1]));
                }
            }
        }
        if (inMap.size() != 0) {
            for (Map.Entry<String, List<String>> entry : inMap.entrySet()) {
                boolQuery.must(QueryBuilders.termsQuery(entry.getKey(), entry.getValue()));
            }
        }
        //field is null
        if (StringUtils.isNotEmpty(condition.getNullStr())) {
            for (String field : condition.getNullStr().split(Constant.SPITSTR1)) {
                boolQuery.must(QueryBuilders.existsQuery(field)).mustNot(QueryBuilders.regexpQuery(field, ".+"));
            }
        }
        if (StringUtils.isNotEmpty(condition.getNotNullStr())) {
            for (String field : condition.getNotNullStr().split(Constant.SPITSTR1)) {
                boolQuery.must(QueryBuilders.existsQuery(field)).must(QueryBuilders.regexpQuery(field, ".+"));
            }
        }

        //模糊查询
        if (StringUtils.isNotEmpty(condition.getWildcardStr())) {
            BoolQueryBuilder or = QueryBuilders.boolQuery();
            for (String s : condition.getWildcardStr().split(Constant.SPITSTR1)) {
                String[] ss = s.split(Constant.SPITSTR2);
                if (ss.length > 1) {
                    or = or.should(QueryBuilders.wildcardQuery(ss[0], "*" + ss[1] + "*"));
                }
            }
            boolQuery.must(or);
        }
        return boolQuery;
    }

    private void buildSearchRequest(ESConditionDTO condition, SearchRequestBuilder searchRequest) {
        searchRequest.setTypes(condition.getType());
        searchRequest.setSearchType(SearchType.QUERY_THEN_FETCH);

        List<SortParamsDTO> sortList = condition.getSortList();
        //指定查询字段
        if (StringUtils.isNotEmpty(condition.getSearchFields())) {
            searchRequest.setFetchSource(condition.getSearchFields().split(Constant.SPITSTR1), null);
            searchRequest.setFetchSource(true);
        }
        //排序
        if (sortList != null && sortList.size() != 0) {
            for (SortParamsDTO sp : sortList) {
                searchRequest.addSort(sp.getSortField(), getSortOrder(sp.getSortOrder()));
            }
        }
        //分页
        if (condition.getFrom() == null && condition.getSize() != null) {
            searchRequest.setSize(condition.getSize());
        } else if (condition.getFrom() != null && condition.getSize() != null) {
            searchRequest.setFrom(condition.getFrom()).setSize(condition.getSize());
        } else {
            searchRequest.setSize(Constant.MAXNUMBER);
        }

        //设置经纬度信息
        ESLocationDTO location = condition.getLocation();
        if (location != null && StringUtils.isNotBlank(location.getDistanceFiled())) {
            setLocation(searchRequest, location);
        }
    }

    /**
     * 设置经纬度信息
     *
     * @param locationDTO 位置信息
     */
    private void setLocation(SearchRequestBuilder searchRequest, ESLocationDTO locationDTO) {
        String order = locationDTO.getSortOrder();
        SortOrder sortOrder = getSortOrder(order);
        if (sortOrder == null) {
            sortOrder = SortOrder.ASC;
        }
        DistanceUnit distanceUnit = DistanceUnit.KILOMETERS;
        if (StringUtils.isNotBlank(locationDTO.getUnit())) {
            DistanceUnitEnum distanceUnitEnum = DistanceUnitEnum.fromString(locationDTO.getUnit());
            distanceUnit = getDistanceUnit(distanceUnitEnum);
        }

        //设置经纬度
        GeoDistanceQueryBuilder queryBuilder = QueryBuilders.geoDistanceQuery(locationDTO.getDistanceFiled())
                .point(locationDTO.getLat(), locationDTO.getLon())
                .distance(locationDTO.getDistance(), distanceUnit)
                .geoDistance(GeoDistance.ARC);

        //排序
        GeoDistanceSortBuilder sortBuilder = SortBuilders.geoDistanceSort(locationDTO.getDistanceFiled(), locationDTO.getLat(), locationDTO.getLon())
                .point(locationDTO.getLat(), locationDTO.getLon())
                .unit(DistanceUnit.METERS)
                .order(sortOrder);

        searchRequest.setQuery(queryBuilder);
        searchRequest.addSort(sortBuilder);
    }

    /**
     * 获取距离单位
     */
    private DistanceUnit getDistanceUnit(DistanceUnitEnum distanceUnitEnum) {
        DistanceUnit distanceUnit = DistanceUnit.KILOMETERS;
        if (distanceUnitEnum != null && StringUtils.isNotBlank(distanceUnitEnum.getUnit())) {
            distanceUnit = DistanceUnit.fromString(distanceUnitEnum.getUnit());
        }
        return distanceUnit;
    }

    /**
     * 获取排序方式
     */
    private SortOrder getSortOrder(String sort) {
        SortOrder sortOrder = SortOrder.ASC;
        if (StringUtils.isNotBlank(sort)) {
            sortOrder = SortOrder.valueOf(sort);
        }
        return sortOrder;
    }

    /**
     * 获取聚合结果
     */
    private List<JSONObject> groupTree(String[] groupArr, int index, Map<String, Aggregation> result,
                                       String[] countArr, String[] maxArr, String[] minArr, String[] sumArr, String[] avgArr) {
        List<JSONObject> jsonList = new ArrayList<>();
        if (index == groupArr.length - 1) {
            Terms terms = (Terms) result.get(groupArr[index].split(Constant.SPITSTR2)[0]);
            for (Terms.Bucket bt : terms.getBuckets()) {
                //先取出基础字段
                TopHits topHits = bt.getAggregations().get("top");
                //聚合只需取每组第一条封装
                SearchHit first = topHits.getHits().getHits()[0];
                JSONObject jsonObject = JSONObject.parseObject(first.getSourceAsString());

                jsonObject.put("count", bt.getDocCount());

                if (countArr != null) {
                    for (String countField : countArr) {
                        String[] kv = countField.split(Constant.SPITSTR2);
                        ValueCount count = bt.getAggregations().get(kv[0]);
                        jsonObject.put(kv[0], count.getValue());
                    }
                }
                if (maxArr != null) {
                    for (String maxField : maxArr) {
                        String[] kv = maxField.split(Constant.SPITSTR2);
                        Max max = bt.getAggregations().get(kv[0]);
                        jsonObject.put(kv[0], max.getValue());
                    }
                }
                if (minArr != null) {
                    for (String minField : minArr) {
                        String[] kv = minField.split(Constant.SPITSTR2);
                        Min min = bt.getAggregations().get(kv[0]);
                        jsonObject.put(kv[0], min.getValue());
                    }
                }
                if (sumArr != null) {
                    for (String sumField : sumArr) {
                        String[] kv = sumField.split(Constant.SPITSTR2);
                        Sum sum = bt.getAggregations().get(kv[0]);
                        jsonObject.put(kv[0], sum.getValue());
                    }
                }
                if (avgArr != null) {
                    for (String avgField : avgArr) {
                        String[] kv = avgField.split(Constant.SPITSTR2);
                        Avg avg = bt.getAggregations().get(kv[0]);
                        jsonObject.put(kv[0], avg.getValue());
                    }
                }
                jsonList.add(jsonObject);
            }
        } else {
            Terms terms = (Terms) result.get(groupArr[index].split(Constant.SPITSTR2)[0]);
            if (terms != null) {
                for (Terms.Bucket bt : terms.getBuckets()) {
                    Map<String, Aggregation> result2 = bt.getAggregations().asMap();
                    List<JSONObject> jsonList2 = groupTree(groupArr, index + 1, result2, countArr, maxArr, minArr, sumArr, avgArr);
                    jsonList.addAll(jsonList2);
                }
            }
        }
        return jsonList;
    }

    /**
     * 批量删除es数据
     *
     * @param deleteConditionDTO id集合
     */
    public boolean bulkDelete(ESDeleteConditionDTO deleteConditionDTO) {
        boolean result = true;
        Preconditions.checkArgument(!CollectionUtils.isEmpty(deleteConditionDTO.getIdList()), "Id集合为空");
//        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();

        //方式一
        BulkProcessor.Builder builder = BulkProcessor.builder((request, bulkListener) ->
                client.bulk(request, bulkListener), new LoggingBulkProcessorListener());

        //方式二
//        BulkProcessor bulkProcessor = BulkProcessor.builder(client, new LoggingBulkProcessorListener())
        // 5MB的数据刷新一次bulk
        BulkProcessor bulkProcessor = builder.setBulkSize(new ByteSizeValue(1, ByteSizeUnit.MB))
                //1w次请求执行一次bulk
                .setBulkActions(1000)
                // 固定5s必须刷新一次
                .setFlushInterval(TimeValue.timeValueSeconds(5))
                // 并发请求数量, 0不并发, 1并发允许执行
                .setConcurrentRequests(1)
                // 设置退避, 100ms后执行, 最大请求3次
                .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
                .build();

        List<String> idList = deleteConditionDTO.getIdList();
        idList.forEach(id -> {
            DeleteRequest request = new DeleteRequest();
            request.index(deleteConditionDTO.getIndex());
            request.type(deleteConditionDTO.getType());
            request.id(id);
//            bulkRequestBuilder.add(request);
            bulkProcessor.add(request);
        });

        bulkProcessor.flush();
        client.admin().indices().prepareRefresh(deleteConditionDTO.getIndex()).get();

//        BulkResponse bulkResponse = bulkRequestBuilder.execute().actionGet();
//        if (bulkResponse.hasFailures()) {
//            result = false;
//            log.error("删除id为：{}的数据出错，出错信息：{}", idList, bulkResponse.buildFailureMessage());
//            return result;
//        }
//        BulkItemResponse[] items = bulkResponse.getItems();
//        if (idList.size() != items.length) {
//            result = false;
//        }
        return result;
    }

    /**
     * 批量更新es中的数据
     *
     * @param updateConditionDTO json数据集合
     */
    public boolean bulkUpdate(ESUpdateConditionDTO updateConditionDTO) {
        Preconditions.checkArgument(!CollectionUtils.isEmpty(updateConditionDTO.getJsonList()), "更改数据为空");
        String index = updateConditionDTO.getIndex();
        String type = updateConditionDTO.getType();
        List<String> jsonList = updateConditionDTO.getJsonList();
        ElasticSearchClient<String> elasticSearchClient = new ElasticSearchClient<>(client, index, configuration, type);
        Map<String, String> dataIdMap = new HashMap<>(jsonList.size());
        jsonList.forEach(json -> {
            String id = getId(json);
            dataIdMap.putIfAbsent(id, json);
        });
        elasticSearchClient.updateIndex(dataIdMap);
        elasticSearchClient.flush();
        return true;
    }

    /**
     * 批量添加数据到es中
     *
     * @param addConditionDTO 添加数据集合
     */
    public boolean bulkAdd(ESAddConditionDTO addConditionDTO) {
        boolean result = true;
        Preconditions.checkArgument(!CollectionUtils.isEmpty(addConditionDTO.getJsonList()), "添加数据为空");
        String index = addConditionDTO.getIndex();
        String type = addConditionDTO.getType();
        List<String> jsonList = addConditionDTO.getJsonList();
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        jsonList.forEach(json -> {
            IndexRequest request = new IndexRequest();
            request.index(index);
            request.type(type);
            request.source(json, XContentType.JSON);
            String id = getId(json);
            if (StringUtils.isNotBlank(id)) {
                request.id(id);
            }
            bulkRequestBuilder.add(request);
        });
        BulkResponse bulkResponse = bulkRequestBuilder.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            result = false;
            log.error("批量添加数据出错，出错信息：{}", bulkResponse.buildFailureMessage());
            return result;
        }
        BulkItemResponse[] items = bulkResponse.getItems();
        if (jsonList.size() != items.length) {
            result = false;
        }
        return result;
    }

    /**
     * 获取数据ID字段值
     */
    private String getId(String json) {
        JsonObject fromJson = gson.fromJson(json, JsonObject.class);
        JsonElement jsonElement = fromJson.get("id");
        String id = jsonElement.getAsString();
        return id;
    }
}
