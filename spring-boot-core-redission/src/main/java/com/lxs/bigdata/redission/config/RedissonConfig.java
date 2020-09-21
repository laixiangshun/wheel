package com.lxs.bigdata.redission.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {RedisProperties.class})
public class RedissonConfig {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public RedissonClient redissonClient(){
        Config config=new Config();
        config.useSentinelServers().setDatabase(redisProperties.getDatabase())
                .setPassword(redisProperties.getPassword())
                .setMasterName(redisProperties.getMasterName())
                .addSentinelAddress(redisProperties.getHost().split(","))
                .setConnectTimeout(10000);
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
