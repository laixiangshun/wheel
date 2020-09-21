package com.lxs.bigdata.flink;

import com.lxs.bigdata.message.MessageSplitter;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;
import scala.Tuple2;

import java.util.Properties;

/**
 * Flink入口类，封装了对于Kafka消息的处理逻辑。
 * 本例每10秒统计一次结果并写入到本地文件
 */
public class KafkaMessageStreaming {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000, CheckpointingMode.EXACTLY_ONCE);//设置启动检查点
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        Properties prop = new Properties();
        prop.setProperty("bootstrap.servers", "localhost:9092");
        prop.setProperty("group.id", "flink-group");

        String topic = "memory-topic";
        String outPath = "C:\\Users\\hasee\\Desktop\\out.txt";

        FlinkKafkaConsumer010<String> kafkaConsumer010 = new FlinkKafkaConsumer010<>(topic, new SimpleStringSchema(), prop);
        DataStream<Tuple2<String, Long>> keyedStream = env.addSource(kafkaConsumer010).flatMap(new MessageSplitter())
                .keyBy(0)
                .timeWindow(Time.seconds(10))
                .apply(new WindowFunction<Tuple2<String, Long>, Tuple2<String, Long>, Tuple, TimeWindow>() {
                    @Override
                    public void apply(Tuple tuple, TimeWindow timeWindow, Iterable<Tuple2<String, Long>> input, Collector<Tuple2<String, Long>> collector) throws Exception {
                        long sum = 0L;
                        int count = 0;
                        for (Tuple2<String, Long> tuple2 : input) {
                            sum += tuple2._2();
                            count++;
                        }
                        Tuple2<String, Long> next = input.iterator().next();
                        collector.collect(new Tuple2<>(next._1(), sum / count));
                    }
                });
        keyedStream.writeAsText(outPath);
        env.execute("flink-kafka-demo");
    }
}
