package flink;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSink;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import utils.ExecutionEnvUtil;

/**
 * flink处理来自RabbitMQ中的数据
 * Exactly-once(只消费一次): 前提条件有，1 是要开启 checkpoint，因为只有在 checkpoint 完成后，才会返回确认消息给 RabbitMQ（这时，消息才会在 RabbitMQ 队列中删除)；
 * 2 是要使用 Correlation ID，在将消息发往 RabbitMQ 时，必须在消息属性中设置 Correlation ID。数据源根据 Correlation ID 把从 checkpoint 恢复的数据进行去重；
 * 3 是数据源不能并行，这种限制主要是由于 RabbitMQ 将消息从单个队列分派给多个消费者。
 * <p>
 * At-least-once(至少消费一次): 开启了 checkpoint，但未使用相 Correlation ID 或 数据源是并行的时候，那么就只能保证数据至少消费一次了
 * <p>
 * No guarantees(无法保证): Flink 接收到数据就返回确认消息给 RabbitMQ
 */
public class FlinkFromRabbitMQ {
    public static void main(String[] args) {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //如果想保证 exactly-once 或 at-least-once 需要把 checkpoint 开启
        env.enableCheckpointing(5000, CheckpointingMode.EXACTLY_ONCE);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        try {
            ParameterTool parameterPool = ExecutionEnvUtil.createParameterPool(args);
            env.getConfig().setGlobalJobParameters(parameterPool);
            env.getConfig().enableSysoutLogging();
            final RMQConnectionConfig config = new RMQConnectionConfig.Builder()
                    .setHost("localhost")
                    .setVirtualHost("/")
                    .setPassword("6379")
                    .setUserName("admin")
                    .setPassword("admin")
                    .setConnectionTimeout(1000_0)
                    .build();
            DataStreamSource<String> rmqStream = env.addSource(new RMQSource<>(config, "flink-rabbit", true, new SimpleStringSchema()));
            DataStreamSource<String> dataStreamSource = rmqStream.setParallelism(1);
            dataStreamSource.print();

            //处理过的数据到rabbitMQ中去
            dataStreamSource.addSink(new RMQSink<>(config, "flink-rabbit", new SimpleStringSchema()));
            env.execute("flink-rabbitmq");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
