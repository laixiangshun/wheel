package rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.Charset;

/**
 * rabbitMQ生成者
 * 模拟生成数据到指定队列
 */
public class RabbitMQProducer {
    private static final String QUEUE_NAME = "flink-rabbitmq";

    public static void main(String[] args) throws Exception {
        //创建一个rabbitMQ工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setChannelRpcTimeout(10000);
        factory.setConnectionTimeout(10000);
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("root");
        factory.setPassword("root");

        //创建一个连接
        Connection connection = null;
        try {
            connection = factory.newConnection();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        //创建一个通道
        Channel channel = connection.createChannel();
        //发送消息到队列中
        String message = "Hello zhisheng";
        for (int i = 0; i < 1000; i++) {
            channel.basicPublish("", QUEUE_NAME, null, (message + i).getBytes(Charset.forName("utf-8")));
            System.out.println("发送消息：" + message + i);
        }

        //关闭通道和连接
        channel.close();
        connection.close();
    }
}
