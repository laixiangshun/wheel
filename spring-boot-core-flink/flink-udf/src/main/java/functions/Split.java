package functions;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.table.functions.TableFunction;

/**
 * 自定义表函数
 */
public class Split extends TableFunction<Tuple2<String, Integer>> {
    private String separator = " ";
    public Split(String separator) {
        this.separator = separator;
    }
    public void eval(String str) {
        for (String s : str.split(separator)) {
            // use collect(...) to emit a row
            collect(new Tuple2<>(s, s.length()));
        }
    }

}
