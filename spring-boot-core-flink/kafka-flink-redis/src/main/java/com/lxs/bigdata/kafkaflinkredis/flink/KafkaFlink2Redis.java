package com.lxs.bigdata.kafkaflinkredis.flink;

import com.lxs.bigdata.kafkaflinkredis.redis.RedisExampleMapper;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;

import java.util.Properties;

/**
 * flink消费kafak数据并把实时计算数据写入redis
 */
public class KafkaFlink2Redis {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000, CheckpointingMode.EXACTLY_ONCE);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.1.20:9092");
        properties.setProperty("zookeeper.connect", "192.168.1.20:2181");
        properties.setProperty("group.id", "test");

        FlinkKafkaConsumer010<String> consumer010 = new FlinkKafkaConsumer010<>("test", new SimpleStringSchema(), properties);
        DataStream<String> stream = env.addSource(consumer010);
        DataStream<Tuple2<String, Integer>> counts = stream.flatMap((FlatMapFunction<String, Tuple2<String, Integer>>) (value, out) -> {
            String[] split = value.toLowerCase().split("\\W+");
            for (String token : split) {
                if (token.length() > 0) {
                    out.collect(new Tuple2<>(token, 1));
                }
            }
        });
        DataStream<Tuple2<String, Integer>> sumStream = counts.keyBy(0).sum(1);
        FlinkJedisPoolConfig.Builder builder = new FlinkJedisPoolConfig.Builder();
        builder.setHost("localhost");
        builder.setPort(6379);
        builder.setDatabase(0);
        builder.setMaxIdle(8);
        builder.setMaxTotal(8);
        builder.setMinIdle(0);
        builder.setTimeout(0);
        RedisSink<Tuple2<String, Integer>> redisSink = new RedisSink<>(builder.build(), new RedisExampleMapper());
        sumStream.addSink(redisSink);
        env.setParallelism(2);
        env.execute("kafka-flink-redis");
    }

}
