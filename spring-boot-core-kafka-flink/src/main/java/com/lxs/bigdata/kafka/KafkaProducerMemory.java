package com.lxs.bigdata.kafka;

import com.lxs.bigdata.utils.MemoryUsageExtrator;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;

/**
 * kafka生产者，模拟发送内存监控数据
 */
public class KafkaProducerMemory {
    public static void main(String[] args) throws InterruptedException {
        Properties pro = new Properties();
        pro.put("bootstrap.servers", "localhost:9092");
        pro.put("acks", "all");
        pro.put("retries", 0);
        pro.put("batch.size", 16384);
        pro.put("linger.ms", 1);
        pro.put("buffer.memory", 33554432);
        pro.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        pro.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<>(pro);
        int totalMessageCount = 10000;
        for (int i = 0; i < totalMessageCount; i++) {
            String value = String.format("%d,%s,%s", System.currentTimeMillis(), "machine-1", currentMemSize());
            ProducerRecord<String, String> record = new ProducerRecord<>("memory-topic", value);
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (e != null) {
                        System.err.println("Failed to send message with exception " + e);
                    }
                }
            });
            Thread.sleep(1000L);
        }
        producer.close();
    }

    private static long currentMemSize() {
        return MemoryUsageExtrator.currentFreeMemorySizeInBytes();
    }
}
