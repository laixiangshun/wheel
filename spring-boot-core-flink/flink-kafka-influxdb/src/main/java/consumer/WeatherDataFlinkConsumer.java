package consumer;

import avro.AvroDeserializationSchema;
import org.apache.avro.Schema;
import org.apache.commons.lang3.Validate;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.WeatherDataRepository;
import schema.WeatherData;

import java.io.IOException;
import java.util.Properties;

/**
 * 消费kafka中的消息，写入时序数据库influxdb
 */
public class WeatherDataFlinkConsumer {
    private static final Logger logger = LoggerFactory.getLogger(WeatherDataFlinkConsumer.class);

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        try {
            Properties pro = new Properties();
            pro.load(WeatherDataFlinkConsumer.class.getResourceAsStream("/config.properties"));
            logger.info("Properties initialized.");
            Schema schema = new Schema.Parser().parse(WeatherDataFlinkConsumer.class.getResourceAsStream("/WeatherData.avsc"));
            logger.info("Avro schema initialized.");

            String topic = args[0];
            Validate.notNull(topic, "Topic must not be null");
            Validate.notNull(pro, "Flink properties must not be null");
            Validate.notNull(schema, "Schema must be provided");

            DataStreamSource<WeatherData> dataStream = env.addSource(new FlinkKafkaConsumer011<WeatherData>(topic,
                    new AvroDeserializationSchema<>(WeatherData.class, schema), pro));
            DataStream<String> mapStream = dataStream.rebalance()
                    .map(new MapFunction<WeatherData, String>() {
                        @Override
                        public String map(WeatherData value) throws Exception {
                            String weather = String.format("%s %s=%f", value.getName().toString().replace(" ", ""),
                                    "value", value.getMain().getTemp().floatValue());
                            return weather;
                        }
                    });

            //写入时序数据库influxdb
            mapStream.addSink(new RichSinkFunction<String>() {
                @Override
                public void invoke(String value, Context context) throws Exception {
                    WeatherDataRepository.getInstance().write("weather_data",value);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        env.execute("flink-kafka-influxdb");
    }
}
