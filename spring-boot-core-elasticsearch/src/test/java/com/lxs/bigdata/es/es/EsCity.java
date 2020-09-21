package com.lxs.bigdata.es.es;

import com.lxs.bigdata.es.dto.City;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class EsCity {
    @Test
    public void esCity() throws IOException {
        //初始化es连接
        Settings.Builder builder = Settings.builder();
        builder.put("cluster.name", "es_cluster");
        builder.put("client.transport.sniff", true);
        Settings settings = builder.build();
        TransportAddress transportAddress = new TransportAddress(new InetSocketAddress("127.0.0.1", 9300));
        Client client = new PreBuiltTransportClient(settings).addTransportAddress(transportAddress);

        String index = "city";
        String type = "xq";
        //创建索引
        createIndex("testes", "xq");

        //添加数据
        addIndexData("testes", "xq");

        //检索附近城市
        double lat = 39.929986;
        double lon = 116.395645;
        long start = System.currentTimeMillis();
        testGetNearbyCities(client, index, type, lat, lon);
        long end = System.currentTimeMillis();
        System.out.println((end - start) + "毫秒");

        //关键字检索
        query("*海鲜*");
        client.close();
    }

    // 创建索引
    private static void createIndex(String indexName, String indexType) throws IOException {
        Settings.Builder builder = Settings.builder();
        builder.put("cluster.name", "es_cluster");
        builder.put("client.transport.sniff", true);
        Settings settings = builder.build();
        TransportAddress transportAddress = new TransportAddress(new InetSocketAddress("127.0.0.1", 9300));
        Client esClient = new PreBuiltTransportClient(settings).addTransportAddress(transportAddress);

        // 创建Mapping
        XContentBuilder mapping = createMapping(indexType);
        System.out.println("mapping:" + mapping.toString());

        // 创建一个空索引
        esClient.admin().indices().prepareCreate(indexName).execute().actionGet();

        PutMappingRequest putMapping = Requests.putMappingRequest(indexName).type(indexType).source(mapping);
        AcknowledgedResponse response = esClient.admin().indices().putMapping(putMapping).actionGet();
        if (!response.isAcknowledged()) {
            System.out.println("Could not define mapping for type [" + indexName + "]/[" + indexType + "].");
        } else {
            System.out.println("Mapping definition for [" + indexName + "]/[" + indexType + "] succesfully created.");
        }
    }

    // 创建mapping
    private static XContentBuilder createMapping(String indexType) {
        XContentBuilder mapping = null;
        try {
            mapping = jsonBuilder().startObject()
                    // 索引库名（类似数据库中的表）
                    .startObject(indexType).startObject("properties")
                    // ID
                    .startObject("id").field("type", "long").endObject()
                    // 城市
                    .startObject("city").field("type", "string").endObject()
                    // 位置
                    .startObject("location").field("type", "geo_point").endObject()
                    // 标题
                    .startObject("title").field("type", "string").endObject()

                    .endObject().endObject().endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mapping;
    }

    // 添加数据
    public static Integer addIndexData(String indexName, String indexType) {
        Settings.Builder builder = Settings.builder();
        builder.put("cluster.name", "es_cluster");
        builder.put("client.transport.sniff", true);
        Settings settings = builder.build();
        TransportAddress transportAddress = new TransportAddress(new InetSocketAddress("127.0.0.1", 9300));
        Client client = new PreBuiltTransportClient(settings).addTransportAddress(transportAddress);
        List<String> cityList = new ArrayList<>();

        City city1 = new City(1L, "北京", 116.395645, 39.929986, "中国人民站起来了，北京人民可以天天站在天安门广场吃烤鸭了");
        City city2 = new City(2L, "天津", 117.210813, 39.143931, "中国人民站起来了，天津人民可以天天在迎宾广场吃麻花了");
        City city3 = new City(3L, "青岛", 120.384428, 36.105215, "中国人民站起来了，青岛人民可以天天在五四广场吃海鲜了，虾TM就是贵点儿，38元一只，38元最后一次！！！最后一次，不要错过今天");
        City city4 = new City(4L, "哈尔滨", 126.657717, 45.773225, "中国人民站起来了，哈尔滨人民可以天天站在索菲亚广场吃红肠了");
        City city5 = new City(5L, "乌鲁木齐", 87.564988, 43.840381, "中国人民站起来了，乌鲁木齐人民可以天天在人民广场啃羊腿了");
        City city6 = new City(6L, "三亚", 109.522771, 18.257776, "中国人民站起来了，三亚人民可以天天在明珠广场吃鲍鱼了，三亚人民这次没丢脸，脸让青岛政府去丢吧，让他们创城去吧！");

        cityList.add(obj2JsonUserData(city1));
        cityList.add(obj2JsonUserData(city2));
        cityList.add(obj2JsonUserData(city3));
        cityList.add(obj2JsonUserData(city4));
        cityList.add(obj2JsonUserData(city5));
        cityList.add(obj2JsonUserData(city6));

        // 创建索引库
        List<IndexRequest> requests = new ArrayList<>();
        for (String s : cityList) {
            IndexRequest request = client.prepareIndex(indexName, indexType).setSource(s).request();
            requests.add(request);
        }

        // 批量创建索引
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (IndexRequest request : requests) {
            bulkRequest.add(request);
        }

        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            System.out.println("批量创建索引错误！");
        }
        return bulkRequest.numberOfActions();
    }

    private static String obj2JsonUserData(City city) {
        String jsonData = null;
        try {
            // 使用XContentBuilder创建json数据
            XContentBuilder jsonBuild = jsonBuilder();
            jsonBuild.startObject()
                    .field("id", city.getId())
                    .field("city", city.getCity())
                    .startArray("location")
                    .value(city.getLat())
                    .value(city.getLon())
                    .endArray()
                    .field("title", city.getTitle())
                    .endObject();
            jsonData = jsonBuild.toString();
            System.out.println(jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonData;
    }

    // 模糊查询
    private static void query(String query) {
        Settings.Builder builder = Settings.builder();
        builder.put("cluster.name", "es_cluster");
        builder.put("client.transport.sniff", true);
        Settings settings = builder.build();
        TransportAddress transportAddress = new TransportAddress(new InetSocketAddress("127.0.0.1", 9300));
        Client client = new PreBuiltTransportClient(settings).addTransportAddress(transportAddress);

        QueryStringQueryBuilder qsqb = new QueryStringQueryBuilder(query);
        // qsqb.analyzer("ik").field("title");
        qsqb.field("title");
        client.admin().indices().prepareRefresh().execute().actionGet();

        SearchResponse searchResponse = client.prepareSearch("testes")
                .setTypes("xq")
                .setQuery(qsqb)
                // .setScroll(new TimeValue(60000))
                .storedFields("id", "title", "updatetime")
                // .addSort("updatetime", SortOrder.DESC)
                .addSort("_score", SortOrder.DESC)
                // .addHighlightedField("title")
                .execute().actionGet();
        // 搜索耗时
        Float usetime = searchResponse.getTook().getMillis() / 1000f;
        // 命中记录数
        long hits = searchResponse.getHits().getTotalHits();
        System.out.println("查询到记录数=" + hits);

        for (SearchHit hit : searchResponse.getHits()) {
            // 打分
            Float score = hit.getScore();
            Integer id = Integer.parseInt(hit.getFields().get("id").getValue().toString());
            String title = hit.getFields().get("title").getValue().toString();
            System.out.println(title);
        }
    }

    // 获取附近的城市
    private static void testGetNearbyCities(Client client, String index, String type, double lat, double lon) {
        SearchRequestBuilder srb = client.prepareSearch(index).setTypes(type);
        // wx4g0th9p0gk 为北京的geohash 范围为lt(小于) 1000km内的数据
        QueryBuilder builder = QueryBuilders.geoDistanceQuery("location")
                .point(lon, lat)
                .distance(1000, DistanceUnit.KILOMETERS)
                .geoDistance(GeoDistance.PLANE);
        srb.setPostFilter(builder);

        // 获取距离多少公里 这个才是获取点与点之间的距离的
        GeoDistanceSortBuilder sort = SortBuilders.geoDistanceSort("location", GeoPoint.parseFromLatLon(String.format("%s,%s", lat, lat)));
        sort.unit(DistanceUnit.KILOMETERS);
        sort.order(SortOrder.ASC);
        sort.point(lon, lat);
        srb.addSort(sort);

        SearchResponse searchResponse = srb.execute().actionGet();

        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHists = hits.getHits();
        System.out.println("北京附近的城市(" + hits.getTotalHits() + "个)：");
        for (SearchHit hit : searchHists) {
            String city = (String) hit.getSourceAsMap().get("city");
            String title = (String) hit.getSourceAsMap().get("title");
            // 获取距离值，并保留两位小数点
            BigDecimal geoDis = new BigDecimal((Double) hit.getSortValues()[0]);
            Map<String, Object> hitMap = hit.getSourceAsMap();
            // 在创建MAPPING的时候，属性名的不可为geoDistance。
            hitMap.put("geoDistance", geoDis.setScale(2, BigDecimal.ROUND_HALF_DOWN));
            System.out.println(city + "距离北京" + hit.getSourceAsMap().get("geoDistance") + DistanceUnit.KILOMETERS.toString() + "---" + title);
        }
    }
}
