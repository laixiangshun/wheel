package com.lxs.bigdata.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * AckMode ：
 * RECORD
 * 每处理一条commit一次
 * BATCH(默认)
 * 每次poll的时候批量提交一次，频率取决于每次poll的调用频率
 * TIME
 * 每次间隔ackTime的时间去commit(跟auto commit interval有什么区别呢？)
 * COUNT
 * 累积达到ackCount次的ack去commit
 * COUNT_TIME
 * ackTime或ackCount哪个条件先满足，就commit
 * MANUAL
 * listener负责ack，但是背后也是批量上去
 * MANUAL_IMMEDIATE
 * listner负责ack，每调用一次，就立即commit
 */
@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    
    @Value("${kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;
    
    @Value("${kafka.consumer.enable-auto-commit}")
    private Boolean autoCommit;
    
    @Value("${kafka.consumer.auto-commit-interval}")
    private Integer autoCommitInterval;
    
    @Value("${kafka.consumer.max-poll-records}")
    private Integer maxPollRecords;
    
    @Value("${kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;
    
    @Value("#{'${kafka.listener.concurrencys}'.split(',')[0]}")
    private Integer concurrency3;
    
    @Value("#{'${kafka.listener.concurrencys}'.split(',')[1]}")
    private Integer concurrency6;
    
    @Value("${kafka.listener.poll-timeout}")
    private Long pollTimeout;
    
    @Value("${kafka.consumer.session-timeout}")
    private String sessionTimeout;
    
    @Value("${kafka.listener.batch-listener}")
    private Boolean batchListener;
    
    @Value("${kafka.consumer.max-poll-interval}")
    private Integer maxPollInterval;
    
    @Value("${kafka.consumer.max-partition-fetch-bytes}")
    private Integer maxPartitionFetchBytes;
    
    /**
     * 并发数6
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(name = "kafkaBatchListener")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaBatchListener() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = kafkaListenerContainerFactory();
        factory.setConcurrency(concurrency6);
        return factory;
    }
    
    /**
     * 并发数3
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(name = "kafkaBatchListener3")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaBatchListener3() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = kafkaListenerContainerFactory();
        factory.setConcurrency(concurrency3);
        return factory;
    }
    
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        //批量消费
        factory.setBatchListener(batchListener);
        //如果消息队列中没有消息，等待timeout毫秒后，调用poll()方法。
        // 如果队列中有消息，立即消费消息，每次消费的消息的多少可以通过max.poll.records配置。
        //手动提交无需配置
        factory.getContainerProperties().setPollTimeout(pollTimeout);
        //设置提交偏移量的方式， MANUAL_IMMEDIATE 表示消费一条提交一次；MANUAL表示批量提交一次
        factory.getContainerProperties()
                .setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
    
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }
    
    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>(10);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitInterval);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollInterval);
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytes);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }
}
