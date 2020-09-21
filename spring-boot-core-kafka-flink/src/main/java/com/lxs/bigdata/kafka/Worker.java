package com.lxs.bigdata.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class Worker implements Runnable {

    private ConsumerRecord<String, String> consumerRecord;

    public Worker(ConsumerRecord<String, String> consumerRecord) {
        this.consumerRecord = consumerRecord;
    }

    @Override
    public void run() {
        // 这里写你的消息处理逻辑，本例中只是简单地打印消息
        System.out.println(Thread.currentThread().getName() + " consumed " + consumerRecord.partition()
                + "th message with offset: " + consumerRecord.offset());
    }
}
