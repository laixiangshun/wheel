package com.lxs.bigdata.es.core;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.Aggregations;
import org.springframework.data.elasticsearch.core.ResultsExtractor;

@Slf4j
public class AggregationResultsExtractor implements ResultsExtractor<Aggregations> {
    @Override
    public Aggregations extract(SearchResponse response) {
        log.info("匹配记录数：" + response.getHits().getTotalHits());
        return response.getAggregations();
    }
}
