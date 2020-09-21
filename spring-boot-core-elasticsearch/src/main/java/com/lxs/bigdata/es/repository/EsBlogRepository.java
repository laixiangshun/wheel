package com.lxs.bigdata.es.repository;

import com.lxs.bigdata.es.dto.EsBlog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsBlogRepository extends ElasticsearchRepository<EsBlog,String> {

}
