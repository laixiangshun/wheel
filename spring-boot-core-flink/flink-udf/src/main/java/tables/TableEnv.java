package tables;

import functions.HashCode;
import functions.RedisCode;
import functions.Split;
import functions.WeightedAvg;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.BatchTableEnvironment;

import java.util.HashMap;
import java.util.Map;

public class TableEnv {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        BatchTableEnvironment tableEnv = TableEnvironment.getTableEnvironment(env);

        //自定义标量函数
        tableEnv.registerFunction("hashCode", new HashCode(10));
        Table table = tableEnv.sqlQuery("select string,HASHCODE(string) from MyTable");
        table.printSchema();
        //注册自定义table表函数udf
        tableEnv.registerFunction("split", new Split("#"));
        tableEnv.sqlQuery("select a,word,length from myYable ,LATERAL TABLE(split(a)) as T(word,length)");
        tableEnv.sqlQuery("select a,word,length from myTable left join LATERAL TABLE(split(a)) as T(word,length) ON TRUE");

        //自定义聚合函数
        tableEnv.registerFunction("wAvg", new WeightedAvg());
        tableEnv.sqlQuery("SELECT user, wAvg(points, level) AS avgPoints FROM userScores GROUP BY user");

        //自定义udf获取全局Runtime信息
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("redis.host", "localhost");
        hashMap.put("redis.port", "6379");
        ParameterTool parameterTool = ParameterTool.fromMap(hashMap);
        env.getConfig().setGlobalJobParameters(parameterTool);
        tableEnv.registerFunction("redisCode", new RedisCode());
        Table sqlQuery = tableEnv.sqlQuery("select string,REDISCODE(string) from myTable ");
        sqlQuery.printSchema();
        env.execute("flink table udf");
    }
}
