package com.lxs.bigdata.es.core;

import lombok.Data;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.min.MinAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.StatsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;

@Data
public class GroupBy {


    private AggregationBuilder termsBuilder;

    //记录上次的子builder
    private AggregationBuilder lastGroupBuilder;

    public void addSubAgg(String aggName, String fieldName) {
        if (termsBuilder == null) {
            termsBuilder = AggregationBuilders.terms(aggName).field(fieldName).size(10000);
            lastGroupBuilder = termsBuilder;
        } else {
            AggregationBuilder n = AggregationBuilders.terms(aggName).field(fieldName).size(10000);
            lastGroupBuilder.subAggregation(n);
            lastGroupBuilder = n;
        }
    }

    public void addSumAgg(String aggName, String fieldName) {
        SumAggregationBuilder builder = AggregationBuilders.sum(aggName).field(fieldName);
        lastGroupBuilder = lastGroupBuilder.subAggregation(builder);
    }


    public void addCountAgg(String aggName, String fieldName) {
        ValueCountAggregationBuilder builder = AggregationBuilders.count(aggName).field(fieldName);
        lastGroupBuilder = lastGroupBuilder.subAggregation(builder);
    }


    public void addAvgAgg(String aggName, String fieldName) {
        AvgAggregationBuilder builder = AggregationBuilders.avg(aggName).field(fieldName);
        lastGroupBuilder = lastGroupBuilder.subAggregation(builder);
    }


    public void addMinAgg(String aggName, String fieldName) {
        MinAggregationBuilder builder = AggregationBuilders.min(aggName).field(fieldName);
        lastGroupBuilder = lastGroupBuilder.subAggregation(builder);
    }


    public void addMaxAgg(String aggName, String fieldName) {
        MaxAggregationBuilder builder = AggregationBuilders.max(aggName).field(fieldName);
        lastGroupBuilder = lastGroupBuilder.subAggregation(builder);
    }


    public void addStatsAgg(String aggName, String fieldName) {
        StatsAggregationBuilder builder = AggregationBuilders.stats(aggName).field(fieldName);
        lastGroupBuilder = lastGroupBuilder.subAggregation(builder);
    }


    public void addCardinalityAgg(String aggName, String fieldName) {
        CardinalityAggregationBuilder builder = AggregationBuilders.cardinality(aggName).field(fieldName);
        lastGroupBuilder = lastGroupBuilder.subAggregation(builder);
    }

    /**
     * 取出非聚合查询的字段，与聚合查询的字段合并
     */
    public AggregationBuilder getTermsBuilder() {
        AggregationBuilder top = AggregationBuilders.topHits("top").explain(true);
        lastGroupBuilder = lastGroupBuilder.subAggregation(top);
        return termsBuilder;
    }


    public void setTermsBuilder(AggregationBuilder termsBuilder) {
        this.termsBuilder = termsBuilder;
    }

}
