package com.lxs.bigdata.es.api;

import com.lxs.bigdata.es.anotation.RequestLimit;
import com.lxs.bigdata.es.beans.AddPeopleDTO;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/es/")
public class EsOptApi {

    @Autowired
    public TransportClient client;

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "get", method = RequestMethod.POST)
    @ResponseBody
    @RequestLimit(count = 4)
    public ResponseEntity get(@RequestParam(name = "id", defaultValue = "") String id) {
        if (id.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        GetRequestBuilder requestBuilder = client.prepareGet("people", "man", id);
        GetResponse getResponse = requestBuilder.get();
        if (!getResponse.isExists()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(getResponse.getSource(), HttpStatus.OK);
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity add(@RequestBody @Valid AddPeopleDTO addPeopleDTO) {
        try {
            XContentBuilder contentBuilder = XContentFactory.jsonBuilder();
            contentBuilder.startObject()
                    .field("name", addPeopleDTO.getName())
                    .field("age", addPeopleDTO.getAge())
                    .field("date", addPeopleDTO.getAge())
                    .field("country", addPeopleDTO.getCountry())
                    .endObject();
            IndexResponse indexResponse = client.prepareIndex("people", "man")
                    .setSource(contentBuilder).get();
            return new ResponseEntity(indexResponse.getId(), HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity delete(@RequestParam(value = "id") String id) {
        DeleteResponse deleteResponse = client.prepareDelete("people", "man", id).get();
        return new ResponseEntity(deleteResponse.getResult().toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity update(@RequestParam(name = "id") String id, @RequestBody @Valid AddPeopleDTO addPeopleDTO) {
        UpdateRequest updateRequest = new UpdateRequest("people", "man", id);
        try {
            XContentBuilder contentBuilder = XContentFactory.jsonBuilder().startObject();
            if (StringUtils.isNotBlank(addPeopleDTO.getName())) {
                contentBuilder.field("name", addPeopleDTO.getName());
            }
            if (StringUtils.isNotBlank(addPeopleDTO.getCountry())) {
                contentBuilder.field("country", addPeopleDTO.getCountry());
            }
            if (addPeopleDTO.getAge() != null) {
                contentBuilder.field("age", addPeopleDTO.getAge());
            }
            if (addPeopleDTO.getDate() != null) {
                contentBuilder.field("date", addPeopleDTO.getDate());
            }
            contentBuilder.endObject();
            updateRequest.doc(contentBuilder);
            UpdateResponse updateResponse = client.update(updateRequest).get();
            return new ResponseEntity(updateResponse.getResult().toString(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "find", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity find(@RequestParam(name = "name", required = false) String name,
                               @RequestParam(name = "gt_age", defaultValue = "0") Integer gtAge,
                               @RequestParam(name = "lt_age", required = false) Integer ltAge) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(name)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("name", name));
        }
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("age").from(gtAge);
        if (ltAge != null) {
            rangeQueryBuilder.to(ltAge);
        }
        boolQueryBuilder.filter(rangeQueryBuilder);
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("people")
                .setTypes("man")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(boolQueryBuilder)
                .setFrom(0)
                .setSize(10);
        SearchResponse searchResponse = searchRequestBuilder.get();
        List<Map<String, Object>> result = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits()) {
            result.add(hit.getSourceAsMap());
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "search", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity search(@RequestParam(name = "pageNumber", required = true, defaultValue = "0") Integer pageNumber,
                                 @RequestParam(name = "pageSize", required = true, defaultValue = "10") Integer pageSize,
                                 @RequestParam(name = "searchContent", required = false) String searchContent) {
        //打分
        FunctionScoreQueryBuilder cityNameScore = QueryBuilders.functionScoreQuery(
                QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("cityName", searchContent)),
                ScoreFunctionBuilders.weightFactorFunction(1000));
        FunctionScoreQueryBuilder descriptionScore = QueryBuilders.functionScoreQuery(
                QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("description", searchContent)),
                ScoreFunctionBuilders.weightFactorFunction(100));
        //排序
        FieldSortBuilder sortBuilder = SortBuilders.fieldSort("id");

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("people")
                .setTypes("city")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(cityNameScore)
                .setQuery(descriptionScore)
                .addSort(sortBuilder);
        SearchResponse response = searchRequestBuilder.setFrom(pageNumber).setSize(pageSize).get();
        SearchHits hits = response.getHits();
        List<Map<String, Object>> result = new ArrayList<>();
        for (SearchHit hit : hits) {
            result.add(hit.getSourceAsMap());
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }
}
