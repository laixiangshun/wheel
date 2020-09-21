package com.lxs.bigdata.es.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lxs.bigdata.es.anotation.*;
import com.lxs.bigdata.es.core.AggregationResultsExtractor;
import com.lxs.bigdata.es.core.Container;
import com.lxs.bigdata.es.core.Operator;
import com.lxs.bigdata.es.dto.ScrollIdDTO;
import com.lxs.bigdata.es.exception.SearchQueryBuildException;
import com.lxs.bigdata.es.exception.SearchResultBuildException;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.lxs.bigdata.es.core.Container.should;
import static org.elasticsearch.index.query.QueryBuilders.*;

@Slf4j
@Component
public class SimpleSearchQueryEngine<T> extends SearchQueryEngine<T> {

    private int numberOfRowsPerScan = 10;

    @Override
    public int saveOrUpdate(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }

        T base = list.get(0);
        Field id = null;
        for (Field field : base.getClass().getDeclaredFields()) {
            BusinessID businessID = field.getAnnotation(BusinessID.class);
            if (businessID != null) {
                id = field;
                break;
            }
        }
        if (id == null) {
            throw new SearchQueryBuildException("Can't find @BusinessID on " + base.getClass().getName());
        }

        Document document = getDocument(base);
        List<UpdateQuery> bulkIndex = new ArrayList<>();
        for (T t : list) {
            UpdateQuery updateQuery = new UpdateQuery();
            updateQuery.setIndexName(document.indexName());
            updateQuery.setType(document.type());
            updateQuery.setId(getFieldValue(id, t).toString());
            updateQuery.setUpdateRequest(new UpdateRequest(updateQuery.getIndexName(), updateQuery.getType(), updateQuery.getId()).doc(JSONObject.toJSONString(t, SerializerFeature.WriteMapNullValue)));
            updateQuery.setDoUpsert(true);
            updateQuery.setClazz(t.getClass());
            bulkIndex.add(updateQuery);
        }
        elasticsearchTemplate.bulkUpdate(bulkIndex);
        return list.size();
    }

    @Override
    public <R> List<R> aggregation(T query, Class<R> clazz) {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = buildNativeSearchQueryBuilder(query);
        nativeSearchQueryBuilder.addAggregation(buildGroupBy(query));
        Aggregations aggregations = elasticsearchTemplate.query(nativeSearchQueryBuilder.build(), new AggregationResultsExtractor());
        try {
            return transformList(null, aggregations, clazz.newInstance(), new ArrayList());
        } catch (Exception e) {
            throw new SearchResultBuildException(e);
        }
    }

    /**
     * 将Aggregations转为List
     *
     * @param terms
     * @param aggregations
     * @param baseObj
     * @param resultList
     * @param <R>
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private <R> List<R> transformList(Aggregation terms, Aggregations aggregations, R baseObj, List<R> resultList)
            throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        for (String column : aggregations.asMap().keySet()) {
            Aggregation childAggregation = aggregations.get(column);
            if (childAggregation instanceof InternalSum) {
                // 使用@Sum
                if (!(terms instanceof InternalSum)) {
                    R targetObj = (R) baseObj.getClass().newInstance();
                    BeanUtils.copyProperties(baseObj, targetObj);
                    resultList.add(targetObj);
                }
                setFieldValue(baseObj.getClass().getDeclaredField(column), resultList.get(resultList.size() - 1),
                        ((InternalSum) childAggregation).getValue());
                terms = childAggregation;
            } else {
                Terms childTerms = (Terms) childAggregation;
                for (Terms.Bucket bucket : childTerms.getBuckets()) {
                    if (CollectionUtils.isEmpty(bucket.getAggregations().asList())) {
                        // 未使用@Sum
                        R targetObj = (R) baseObj.getClass().newInstance();
                        BeanUtils.copyProperties(baseObj, targetObj);
                        setFieldValue(targetObj.getClass().getDeclaredField(column), targetObj, bucket.getKey());
                        resultList.add(targetObj);
                    } else {
                        setFieldValue(baseObj.getClass().getDeclaredField(column), baseObj, bucket.getKey());
                        transformList(childTerms, bucket.getAggregations(), baseObj, resultList);
                    }
                }
            }
        }
        return resultList;
    }

    @Override
    public <R> Page<R> scroll(T query, Class<R> clazz, Pageable pageable, ScrollIdDTO scrollId) {
        if (pageable.getPageSize() % numberOfRowsPerScan > 0) {
            throw new SearchQueryBuildException("Page size must be an integral multiple of " + numberOfRowsPerScan);
        }
        NativeSearchQuery searchQuery = buildNativeSearchQueryBuilder(query)
                .withPageable(PageRequest.of(pageable.getPageNumber(), numberOfRowsPerScan / getNumberOfShards(query), pageable.getSort()))
                .build();
//        if (StringUtils.isEmpty(scrollId.getValue())) {
//            scrollId.setValue(elasticsearchTemplate.scan(searchQuery, 10000L, false));
//        }
//        Page<R> page = elasticsearchTemplate.continueScroll(scrollId.getValue(), 10000L, clazz);
//        if (page == null || page.getContent().size() == 0) {
//            elasticsearchTemplate.clearScroll(scrollId.getValue());
//        }
        Page<R> page = elasticsearchTemplate.queryForPage(searchQuery, clazz);
        return page;
    }

    @Override
    public <R> List<R> find(T query, Class<R> clazz, int size) {
        // Caused by: QueryPhaseExecutionException[Result window is too large, from + size must be less than or equal to: [10000] but was [2147483647].
        // See the scroll api for a more efficient way to request large data sets. This limit can be set by changing the [index.max_result_window] index level parameter.]
        if (size % numberOfRowsPerScan > 0) {
            throw new SearchQueryBuildException("Parameter 'size' must be an integral multiple of " + numberOfRowsPerScan);
        }
        int pageNum = 0;
//        List<R> result = new ArrayList<>();
//        ScrollId scrollId = new ScrollId();
//        while (true) {
//            Page<R> page = scroll(query, clazz, PageRequest.of(pageNum, numberOfRowsPerScan), scrollId);
//            if (page != null && page.getContent().size() > 0) {
//                result.addAll(page.getContent());
//            } else {
//                break;
//            }
//            if (result.size() >= size) {
//                break;
//            } else {
//                pageNum++;
//            }
//        }
//        elasticsearchTemplate.clearScroll(scrollId.getValue());

        PageRequest pageRequest = PageRequest.of(pageNum, size, Sort.Direction.DESC);
        NativeSearchQuery searchQuery = buildNativeSearchQueryBuilder(query)
                .withPageable(pageRequest)
                .build();
        List<R> result = elasticsearchTemplate.queryForList(searchQuery, clazz);
        return result;
    }

    @Override
    public <R> Page<R> find(T query, Class<R> clazz, Pageable pageable) {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = buildNativeSearchQueryBuilder(query).withPageable(pageable);
        return elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), clazz);
    }

    @Override
    public <R> R sum(T query, Class<R> clazz) {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = buildNativeSearchQueryBuilder(query);
        for (SumAggregationBuilder sumBuilder : getSumBuilderList(query)) {
            nativeSearchQueryBuilder.addAggregation(sumBuilder);
        }
        Aggregations aggregations = elasticsearchTemplate.query(nativeSearchQueryBuilder.build(), new AggregationResultsExtractor());
        try {
            return transformSumResult(aggregations, clazz);
        } catch (Exception e) {
            throw new SearchResultBuildException(e);
        }
    }

    private <R> R transformSumResult(Aggregations aggregations, Class<R> clazz) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        R targetObj = clazz.newInstance();
        for (Aggregation sum : aggregations.asList()) {
            if (sum instanceof InternalSum) {
                setFieldValue(targetObj.getClass().getDeclaredField(sum.getName()), targetObj, ((InternalSum) sum).getValue());
            }
        }
        return targetObj;
    }

    private NativeSearchQueryBuilder buildNativeSearchQueryBuilder(T query) {
        Document document = getDocument(query);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withIndices(document.indexName())
                .withTypes(document.type());

        QueryBuilder whereBuilder = buildBoolQuery(query);
        if (whereBuilder != null) {
            nativeSearchQueryBuilder.withQuery(whereBuilder);
        }

        return nativeSearchQueryBuilder;
    }

    /**
     * 布尔查询构建
     *
     * @param query
     * @return
     */
    private BoolQueryBuilder buildBoolQuery(T query) {
        BoolQueryBuilder boolQueryBuilder = boolQuery();
        buildMatchQuery(boolQueryBuilder, query);
        buildRangeQuery(boolQueryBuilder, query);
        BoolQueryBuilder queryBuilder = boolQuery().must(boolQueryBuilder);
        return queryBuilder;
    }

    /**
     * and or 查询构建
     *
     * @param boolQueryBuilder
     * @param query
     */
    private void buildMatchQuery(BoolQueryBuilder boolQueryBuilder, T query) {
        Class clazz = query.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            MatchQuery annotation = field.getAnnotation(MatchQuery.class);
            Object value = getFieldValue(field, query);
            if (annotation == null || value == null) {
                continue;
            }
            if (Container.must.equals(annotation.container())) {
                boolQueryBuilder.must(matchQuery(getFieldName(field, annotation.column()), formatValue(value)));
            } else if (should.equals(annotation.container())) {
                if (value instanceof Collection) {
                    BoolQueryBuilder shouldQueryBuilder = boolQuery();
                    Collection tmp = (Collection) value;
                    for (Object obj : tmp) {
                        shouldQueryBuilder.should(matchQuery(getFieldName(field, annotation.column()), formatValue(obj)));
                    }
                    boolQueryBuilder.must(shouldQueryBuilder);
                } else {
                    boolQueryBuilder.must(boolQuery().should(matchQuery(getFieldName(field, annotation.column()), formatValue(value))));
                }
            }
        }
    }

    /**
     * 范围查询构建
     *
     * @param boolQueryBuilder
     * @param query
     */
    private void buildRangeQuery(BoolQueryBuilder boolQueryBuilder, T query) {
        Class clazz = query.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            RangeQuery annotation = field.getAnnotation(RangeQuery.class);
            Object value = getFieldValue(field, query);
            if (annotation == null || value == null) {
                continue;
            }
            if (Operator.gt.equals(annotation.operator())) {
                boolQueryBuilder.must(rangeQuery(getFieldName(field, annotation.column())).gt(formatValue(value)));
            } else if (Operator.gte.equals(annotation.operator())) {
                boolQueryBuilder.must(rangeQuery(getFieldName(field, annotation.column())).gte(formatValue(value)));
            } else if (Operator.lt.equals(annotation.operator())) {
                boolQueryBuilder.must(rangeQuery(getFieldName(field, annotation.column())).lt(formatValue(value)));
            } else if (Operator.lte.equals(annotation.operator())) {
                boolQueryBuilder.must(rangeQuery(getFieldName(field, annotation.column())).lte(formatValue(value)));
            }
        }
    }

    /**
     * Sum构建
     *
     * @param query
     * @return
     */
    private List<SumAggregationBuilder> getSumBuilderList(T query) {
        List<SumAggregationBuilder> list = new ArrayList<>();
        Class clazz = query.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            Sum annotation = field.getAnnotation(Sum.class);
            if (annotation == null) {
                continue;
            }
            list.add(AggregationBuilders.sum(field.getName()).field(field.getName()));
        }
        if (CollectionUtils.isEmpty(list)) {
            throw new SearchQueryBuildException("Can't find @Sum on " + clazz.getName());
        }
        return list;
    }


    /**
     * GroupBy构建
     *
     * @param query
     * @return
     */
    private TermsAggregationBuilder buildGroupBy(T query) {
        List<Field> sumList = new ArrayList<>();
        Object groupByCollection = null;
        Class clazz = query.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            Sum sumAnnotation = field.getAnnotation(Sum.class);
            if (sumAnnotation != null) {
                sumList.add(field);
            }
            GroupBy groupByannotation = field.getAnnotation(GroupBy.class);
            Object value = getFieldValue(field, query);
            if (groupByannotation == null || value == null) {
                continue;
            } else if (!(value instanceof Collection)) {
                throw new SearchQueryBuildException("GroupBy filed must be collection");
            } else if (CollectionUtils.isEmpty((Collection<String>) value)) {
                continue;
            } else if (groupByCollection != null) {
                throw new SearchQueryBuildException("Only one @GroupBy is allowed");
            } else {
                groupByCollection = value;
            }
        }
        Iterator<String> iterator = ((Collection<String>) groupByCollection).iterator();
        TermsAggregationBuilder termsBuilder = recursiveAddAggregation(iterator, sumList);
        return termsBuilder;
    }

    /**
     * 添加Aggregation
     *
     * @param iterator
     * @return
     */
    private TermsAggregationBuilder recursiveAddAggregation(Iterator<String> iterator, List<Field> sumList) {
        String groupBy = iterator.next();
        TermsAggregationBuilder termsBuilder = AggregationBuilders.terms(groupBy).field(groupBy).size(0);
        if (iterator.hasNext()) {
            termsBuilder.subAggregation(recursiveAddAggregation(iterator, sumList));
        } else {
            for (Field field : sumList) {
                termsBuilder.subAggregation(AggregationBuilders.sum(field.getName()).field(field.getName()));
            }
            sumList.clear();
        }
        return termsBuilder.order(BucketOrder.count(true));
    }
}
