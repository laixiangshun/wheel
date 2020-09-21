package com.lxs.bigdata.es.service;

import com.lxs.bigdata.es.dto.EsBlog;
import com.lxs.bigdata.es.repository.EsBlogRepository;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 博客es搜索服务
 *
 * @author lxs
 */
@Service
public class BlogServiceImpl {

    @Autowired
    private EsBlogRepository esBlogRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 根据标题，概要分页搜索
     *
     * @param pageIndex
     * @param pageSize
     * @param keyWord
     * @param sumaryKeyWord
     * @return
     */
    public List<EsBlog> searchBlog(Integer pageIndex, Integer pageSize, String keyWord, String sumaryKeyWord) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(sumaryKeyWord)) {
            builder.must(QueryBuilders.fuzzyQuery("sumary", sumaryKeyWord));
        }
        if (StringUtils.isNotBlank(keyWord)) {
            builder.must(new QueryStringQueryBuilder("title").field(keyWord));
        }
        FieldSortBuilder sortBuilder = SortBuilders.fieldSort("commentSize").order(SortOrder.DESC);
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withPageable(pageRequest);
        queryBuilder.withSort(sortBuilder);
        queryBuilder.withQuery(builder);
        NativeSearchQuery build = queryBuilder.build();
        //方式1
        Page<EsBlog> page = esBlogRepository.search(build);
        //方法2
//        List<EsBlog> blogList = elasticsearchTemplate.queryForList(build, EsBlog.class);
        List<EsBlog> esBlogList = page.getContent();
//        long total = page.getTotalElements();
//        int totalPages = page.getTotalPages();
        return esBlogList;
    }

    /**
     * 目标：搜索写博客写得最多的用户（一个博客对应一个用户），通过搜索博客中的用户名的频次来达到想要的结果
     *
     * @return
     */
    public List<String> searchAggregation() {
        List<String> userNameList = new ArrayList<>();
        MatchAllQueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(queryBuilder);
        //指定索引的类型，只先从各分片中查询匹配的文档，再重新排序和排名，取前size个文档
        nativeSearchQueryBuilder.withSearchType(SearchType.QUERY_THEN_FETCH);
        nativeSearchQueryBuilder.withIndices("blog").withTypes("blog");
        //指定聚合函数
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("userList").field("userName")
                .order(BucketOrder.count(false));
        nativeSearchQueryBuilder.addAggregation(aggregationBuilder);
        NativeSearchQuery searchQuery = nativeSearchQueryBuilder.build();
        //方式1
        Page<EsBlog> page = esBlogRepository.search(searchQuery);
        List<EsBlog> content = page.getContent();
        for (EsBlog esBlog : content) {
            userNameList.add(esBlog.getUserName());
        }
        //方法2
//        List<EsBlog> esBlogs = elasticsearchTemplate.queryForList(searchQuery, EsBlog.class);
        //方式3
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });
        Map<String, Aggregation> aggregationMap = aggregations.asMap();
        //获得对应的聚合函数的聚合子类，该聚合子类也是个map集合,里面的value就是桶Bucket，我们要获得Bucket
        StringTerms userAggregation = (StringTerms) aggregationMap.get("userList");
        List<StringTerms.Bucket> buckets = userAggregation.getBuckets();
        for (StringTerms.Bucket bucket : buckets) {
            String userName = bucket.getKeyAsString();
            userNameList.add(userName);
        }
        return userNameList;
    }
}
