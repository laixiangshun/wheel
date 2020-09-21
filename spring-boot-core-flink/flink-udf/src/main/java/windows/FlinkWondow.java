package windows;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;
import org.apache.flink.streaming.api.windowing.triggers.PurgingTrigger;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

public class FlinkWondow {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> source = env.socketTextStream("localhost", 9000);
        DataStream<Tuple2<String, Integer>> values = source.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
                String[] split = value.split("\\s+");
                for (String s : split) {
                    out.collect(new Tuple2<>(s, 1));
                }
            }
        });
        KeyedStream<Tuple2<String, Integer>, Tuple> keyedStream = values.keyBy(0);
        //使用数量的trigger
        WindowedStream<Tuple2<String, Integer>, Tuple, GlobalWindow> countWindowWithoutPurge = keyedStream.window(GlobalWindows.create())
                .trigger(CountTrigger.of(2));

        WindowedStream<Tuple2<String, Integer>, Tuple, GlobalWindow> countWindowWithPurge = keyedStream.window(GlobalWindows.create())
                .trigger(PurgingTrigger.of(CountTrigger.of(2)));
        countWindowWithoutPurge.sum(1).print();
        countWindowWithPurge.sum(1).print();
        env.execute("flink window");
    }
}
