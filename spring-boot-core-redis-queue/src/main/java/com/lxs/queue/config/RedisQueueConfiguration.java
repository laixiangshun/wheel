package com.lxs.queue.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.lxs.queue.anotation.RedisQueue;
import com.lxs.queue.handler.HandlerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.Set;

/**
 * redis队列自动装配类
 *
 * @author lxs
 */
@Slf4j
@Configuration
@ConditionalOnMissingBean(value = {JedisPool.class, JedisSentinelPool.class, JedisCluster.class})
@ConditionalOnClass(value = {JedisPool.class, JedisSentinelPool.class, JedisCluster.class})
public class RedisQueueConfiguration {
    
    @Autowired
    private RedisProperties redisProperties;
    
    /**
     * redis单价模式
     *
     * @param jedisPoolConfig
     * @return
     * @throws NoSuchFieldException
     */
    @Bean
    @ConditionalOnProperty(prefix = "spring.redis.host", matchIfMissing = true)
    public JedisPool jedisPool(JedisPoolConfig jedisPoolConfig) throws NoSuchFieldException {
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, redisProperties.getHost(), redisProperties.getPort(), (int) redisProperties.getTimeout().toMillis(), redisProperties.getPassword(), redisProperties.getDatabase());
        HandlerFactory.INSTANCE.modifyHandlerProperty(RedisQueue.class, "pool", jedisPool);
        log.info(">>>>>>>>>>>>>>>>>>>>>> Initial Jedis Pool Successfully.");
        return jedisPool;
    }
    
    /**
     * redis 哨兵模式
     *
     * @return
     * @throws NoSuchFieldException
     */
    @Bean
    @ConditionalOnProperty(prefix = "spring.redis.sentinel")
    public JedisSentinelPool jedisSentinelPool(JedisPoolConfig jedisPoolConfig) throws NoSuchFieldException {
        try {
            JedisSentinelPool jedisSentinelPool = new JedisSentinelPool(redisProperties.getSentinel().getMaster(),
                    new HashSet<>(redisProperties.getSentinel().getNodes()), jedisPoolConfig,
                    (int) redisProperties.getTimeout().toMillis(), redisProperties.getPassword(), redisProperties.getDatabase());
            HandlerFactory.INSTANCE.modifyHandlerProperty(RedisQueue.class, "pool", jedisSentinelPool);
            log.info(">>>>>>>>>>>>>>>>>>>>>> Initial Jedis Pool Successfully.");
            return jedisSentinelPool;
        } catch (Exception e) {
            log.error(">>>>>>>>>>>>>>>>>>>>>> Initial Jedis Pool Error.", e);
            throw e;
        }
    }
    
    /**
     * redis集群模式
     *
     * @param jedisPoolConfig
     * @return
     * @throws NoSuchFieldException
     */
    @Bean
    @ConditionalOnProperty(prefix = "spring.redis.cluster")
    public JedisCluster jedisCluster(JedisPoolConfig jedisPoolConfig) throws NoSuchFieldException {
        RedisProperties.Cluster cluster = redisProperties.getCluster();
        Set<HostAndPort> nodes = new HashSet<>();
        cluster.getNodes().forEach(node -> {
            String[] nodeInfos = StrUtil.split(node, ":");
            nodes.add(new HostAndPort(nodeInfos[0], Integer.valueOf(nodeInfos[1])));
        });
        JedisCluster jedisCluster = new JedisCluster(nodes, (int) redisProperties.getTimeout().toMillis(), cluster.getMaxRedirects(), jedisPoolConfig);
        HandlerFactory.INSTANCE.modifyHandlerProperty(RedisQueue.class, "jedisCluster", jedisCluster);
        log.info(">>>>>>>>>>>>>>>>>>>>>> Initial Jedis jedisCluster Successfully.");
        return jedisCluster;
    }
    
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        RedisProperties.Jedis jedis = redisProperties.getJedis();
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        if (ObjectUtil.isNotNull(jedis)) {
            RedisProperties.Pool pool = jedis.getPool();
            jedisPoolConfig.setMaxTotal(pool.getMaxActive());
            jedisPoolConfig.setMaxIdle(pool.getMaxIdle());
            jedisPoolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
            jedisPoolConfig.setMinIdle(pool.getMinIdle());
        }
        return jedisPoolConfig;
    }
}
