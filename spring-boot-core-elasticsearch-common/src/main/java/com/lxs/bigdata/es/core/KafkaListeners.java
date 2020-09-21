package com.lxs.bigdata.es.core;

import com.google.gson.Gson;
import com.lxs.bigdata.es.dto.ESBulkConditionDTO;
import com.lxs.bigdata.es.exception.KafkaConsumerException;
import com.lxs.bigdata.es.service.ElasticsearchBulkService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * kafka消费端，支持批量消费
 */
@Component
@Slf4j
public class KafkaListeners {

    private ElasticsearchBulkService bulkService;

    private Gson gson;

    @Autowired
    public KafkaListeners(ElasticsearchBulkService bulkService, Gson gson) {
        this.bulkService = bulkService;
        this.gson = gson;
    }

    @KafkaListener(containerFactory = "kafkaBatchListener", topics = {"#{'${kafka.listener.topics}'.split(',')[0]}"})
    public void batchListener(List<ConsumerRecord<?, ?>> records, Acknowledgment ack) {

        try {
            CompletableFuture.runAsync(() -> {
                List<ESBulkConditionDTO> dataList = new ArrayList<>(records.size());
                records.forEach(
                        record -> {
                            Object value = record.value();
                            if (!(value instanceof String)) {
                                value = gson.toJson(value);
                            }
                            ESBulkConditionDTO bulkCondition = gson.fromJson((String) value, ESBulkConditionDTO.class);
                            dataList.add(bulkCondition);
                        }
                );
                if (!CollectionUtils.isEmpty(dataList)) {
                    bulkService.processData(dataList);
                }
            }).exceptionally(e -> {
                if (e != null) {
                    log.error("处理kafka消息出错，原因：" + e.getMessage(), e);
                }
                throw new KafkaConsumerException(e.getMessage(), e);
            });
        } catch (Exception e) {
            log.error("Kafka监听异常" + e.getMessage(), e);
        } finally {
            ack.acknowledge();//手动提交偏移量
        }
    }
}
