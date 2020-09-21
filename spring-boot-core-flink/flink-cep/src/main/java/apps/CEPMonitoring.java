package apps;

import events.MonitoringEvent;
import events.TemperatureAlert;
import events.TemperatureEvent;
import events.TemperatureWarning;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternFlatSelectFunction;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.IngestionTimeExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import sources.MonitoringEventSource;

import java.util.List;
import java.util.Map;

public class CEPMonitoring {

    private static final double TEMPERATURE_THRESHOLD = 100;

    private static final int MAX_RACK_ID = 10;
    private static final long PAUSE = 100;
    private static final double TEMPERATURE_RATIO = 0.5;
    private static final double POWER_STD = 10;
    private static final double POWER_MEAN = 100;
    private static final double TEMP_STD = 20;
    private static final double TEMP_MEAN = 80;

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000, CheckpointingMode.EXACTLY_ONCE);
        env.setParallelism(2);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        DataStreamSource<MonitoringEvent> inputStream = env.addSource(new MonitoringEventSource(MAX_RACK_ID,
                PAUSE,
                TEMPERATURE_RATIO,
                POWER_STD,
                POWER_MEAN,
                TEMP_STD,
                TEMP_MEAN));
        DataStream<MonitoringEvent> inputEventStream = inputStream.assignTimestampsAndWatermarks(new IngestionTimeExtractor<>());

        //高温警告事件模式
        Pattern<MonitoringEvent, TemperatureEvent> warningPattern = Pattern.<MonitoringEvent>begin("first event")
                .subtype(TemperatureEvent.class)
                .where(new IterativeCondition<TemperatureEvent>() {
                    private static final long serialVersionUID = -6301755149429716724L;

                    @Override
                    public boolean filter(TemperatureEvent monitoringEvent, Context<TemperatureEvent> context) throws Exception {
                        return monitoringEvent.getTemperature() >= TEMPERATURE_THRESHOLD;
                    }
                }).next("second event")
                .subtype(TemperatureEvent.class)
                .where(new IterativeCondition<TemperatureEvent>() {
                    private static final long serialVersionUID = 2392863109523984059L;

                    @Override
                    public boolean filter(TemperatureEvent temperatureEvent, Context<TemperatureEvent> context) throws Exception {
                        return temperatureEvent.getTemperature() >= TEMPERATURE_THRESHOLD;
                    }
                }).within(Time.seconds(10));

        KeyedStream<MonitoringEvent, Tuple> keyedStream = inputEventStream.keyBy("rackID");

        PatternStream<MonitoringEvent> tempPatternStream = CEP.pattern(keyedStream, warningPattern);
        DataStream<TemperatureWarning> warnings = tempPatternStream.select(new PatternSelectFunction<MonitoringEvent, TemperatureWarning>() {
            @Override
            public TemperatureWarning select(Map<String, List<MonitoringEvent>> map) throws Exception {
                TemperatureEvent first_event = (TemperatureEvent) map.get("first event").get(0);
                TemperatureEvent second_event = (TemperatureEvent) map.get("second event").get(0);
                return new TemperatureWarning(first_event.getRackID(), (first_event.getTemperature() + second_event.getTemperature()) / 2);
            }
        });

        //高温报警事件模式
        //发送高温警告不一定报警：在20秒内发生两次警告，并且平均温度在升高的机器进行报警
        Pattern<TemperatureWarning, TemperatureWarning> alertPattern = Pattern.<TemperatureWarning>begin("first")
                .next("second")
                .within(Time.seconds(20));
        PatternStream<TemperatureWarning> alertPatternStream = CEP.pattern(warnings.keyBy("rackID"), alertPattern);
        DataStream<TemperatureAlert> alerts = alertPatternStream.flatSelect(new PatternFlatSelectFunction<TemperatureWarning, TemperatureAlert>() {
            @Override
            public void flatSelect(Map<String, List<TemperatureWarning>> map, Collector<TemperatureAlert> collector) throws Exception {
                TemperatureWarning first = map.get("first").get(0);
                TemperatureWarning second = map.get("second").get(0);
                if (first.getAverageTemperature() < second.getAverageTemperature()) {
                    collector.collect(new TemperatureAlert(first.getRackID()));
                }
            }
        }, TypeInformation.of(TemperatureAlert.class));

        warnings.print();
        alerts.print();
        env.execute("CEP monitoring job");
    }
}
