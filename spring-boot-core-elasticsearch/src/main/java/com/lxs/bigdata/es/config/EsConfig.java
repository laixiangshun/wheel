package com.lxs.bigdata.es.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * es 配置文件
 *
 * @author lxs
 */
@Configuration
public class EsConfig {

    @Autowired
    public EsProperties properties;

    @Bean
    public TransportClient client() throws UnknownHostException {
        Settings.Builder builder = Settings.builder();
        builder.put("cluster.name", properties.getCluster());
        builder.put("client.transport.sniff", true);
        Settings settings = builder.build();
        TransportAddress transportAddress = new TransportAddress(InetAddress.getByName(properties.getHost()), properties.getPort());
        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(transportAddress);
        return client;
    }
}
