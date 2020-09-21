package com.lxs.bigdata.es.service;

import com.lxs.bigdata.es.beans.IntelligentStoreDTO;
import com.lxs.bigdata.es.beans.IntelligentStoreVO;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

@Service
public class IntelligentStoreService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    public Page<IntelligentStoreVO> findAllByLocaltion(IntelligentStoreDTO intelligentStoreDTO) {
        Pageable pageable = PageRequest.of(intelligentStoreDTO.getPageIndex(), intelligentStoreDTO.getPageSize());

        //设置经纬度
        GeoDistanceQueryBuilder queryBuilder = QueryBuilders.geoDistanceQuery("location")
                .point(intelligentStoreDTO.getLat(), intelligentStoreDTO.getLon())
                .distance(intelligentStoreDTO.getDistance(), DistanceUnit.METERS)
                .geoDistance(GeoDistance.ARC);

        //排序
        GeoDistanceSortBuilder sortBuilder = SortBuilders.geoDistanceSort("location", intelligentStoreDTO.getLat(), intelligentStoreDTO.getLon())
                .point(intelligentStoreDTO.getLat(), intelligentStoreDTO.getLon())
                .unit(DistanceUnit.METERS)
                .order(SortOrder.ASC);

        //构造条件
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withFilter(queryBuilder);
        nativeSearchQueryBuilder.withSort(sortBuilder);
        nativeSearchQueryBuilder.withPageable(pageable);

        Page<IntelligentStoreVO> page = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), IntelligentStoreVO.class);
        return page;
    }
}
