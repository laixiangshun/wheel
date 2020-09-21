package functions;

import org.apache.flink.table.functions.FunctionContext;
import org.apache.flink.table.functions.ScalarFunction;
import redis.clients.jedis.Jedis;

public class RedisCode extends ScalarFunction {
    private int factor = 12;
    Jedis jedis = null;

    public RedisCode() {
        super();
    }

    public RedisCode(int factor) {
        this.factor = factor;
    }

    @Override
    public void open(FunctionContext context) throws Exception {
        super.open(context);
        String redisHost = context.getJobParameter("redis.host", "localhost");
        String redisPort = context.getJobParameter("redis.port", "6379");
        jedis = new Jedis(redisHost, Integer.parseInt(redisPort));
    }

    @Override
    public void close() throws Exception {
        super.close();
        jedis.close();
    }

    public int eval(int s) {
        s = s % 3;
        if (s == 2) {
            return Integer.parseInt(jedis.get(String.valueOf(s)));
        } else {
            return 0;
        }
    }
}
