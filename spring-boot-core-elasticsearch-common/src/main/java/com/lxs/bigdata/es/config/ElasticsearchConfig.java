package com.lxs.bigdata.es.config;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableConfigurationProperties(value = {ElasticSearchProperties.class})
public class ElasticsearchConfig {

    @Autowired
    private ElasticSearchProperties elasticSearchProperties;

    @Bean
    public Gson gson() {
        return new Gson();
    }

    /**
     * 构造7.x版本的客户端
     */
//    @Bean
//    public RestHighLevelClient client() {
//        String clusterName = elasticSearchProperties.getClusterName();
//        String nodeStr = elasticSearchProperties.getNode();
//        if (StringUtils.isBlank(nodeStr)) {
//            nodeStr = "localhost:9200";
//        }
//        String[] nodes = StringUtils.split(nodeStr, ",");
//
//
//        List<HttpHost> builders = new ArrayList<>(nodes.length);
//        Arrays.stream(nodes).forEach(node -> {
//                    String[] nodePorts = StringUtils.split(node, ":");
//                    HttpHost httpHost = new HttpHost(nodePorts[0], Integer.parseInt(nodePorts[1]), "http");
//                    builders.add(httpHost);
//                }
//        );
//        RestClientBuilder builder = RestClient.builder(builders.toArray(new HttpHost[0]));
//        RestHighLevelClient client = new RestHighLevelClient(builder);
//        return client;
//    }

    /**
     * 构造6.x版本的客户端
     */
    @Bean
    public PreBuiltTransportClient transportClient() {
        String clusterName = elasticSearchProperties.getClusterName();
        String nodeStr = elasticSearchProperties.getNode();
        if (StringUtils.isBlank(nodeStr)) {
            nodeStr = "localhost:9300";
        }
        String[] nodes = StringUtils.split(nodeStr, ",");


        Settings settings = Settings.builder()
                .put("client", true)
                .put("data", false)
                .put("clusterName", clusterName)
                // 忽略集群名字验证, 打开后集群名字不对也能连接上
                .put("client.transport.ignore_cluster_name", true)
                .put("client.transport.sniff", true)//增加嗅探机制，找到ES集群
                .put("thread_pool.search.size", 5)//增加线程池个数，暂时设为5
                .build();
        PreBuiltTransportClient client = new PreBuiltTransportClient(settings);
        List<TransportAddress> addressList = new ArrayList<>(nodes.length);
        Arrays.stream(nodes).forEach(node -> {
            String[] nodePort = StringUtils.split(node, ":");
            TransportAddress transportAddress = null;
            try {
                transportAddress = new TransportAddress(InetAddress.getByName(nodePort[0]), Integer.parseInt(nodePort[1]));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            addressList.add(transportAddress);
        });
        client.addTransportAddresses(addressList.toArray(new TransportAddress[0]));
        return client;
    }
}
