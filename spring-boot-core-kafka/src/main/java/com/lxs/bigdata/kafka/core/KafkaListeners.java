package com.lxs.bigdata.kafka.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class KafkaListeners {

    @KafkaListener(containerFactory = "kafkaBatchListener", topics = {"#{'${kafka.listener.topics}'.split(',')[0]}"})
    public void batchListener(List<ConsumerRecord<?, ?>> records, Acknowledgment ack) {

        try {
            records.forEach(record -> {
                log.info("消息：{}", record);
            });
        } catch (Exception e) {
            log.error("Kafka监听异常" + e.getMessage(), e);
        } finally {
            ack.acknowledge();//手动提交偏移量
        }
    }
}
