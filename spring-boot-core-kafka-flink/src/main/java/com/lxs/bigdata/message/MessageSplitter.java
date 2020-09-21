package com.lxs.bigdata.message;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import scala.Tuple2;

/**
 * Kafka消息格式固定为：时间戳,主机名,当前可用内存数
 * 将获取到的每条Kafka消息根据“，”分割取出其中的主机名和内存数信息
 */
public class MessageSplitter implements FlatMapFunction<String, Tuple2<String, Long>> {

    @Override
    public void flatMap(String value, Collector<Tuple2<String, Long>> collector) throws Exception {
        if (StringUtils.isNotBlank(value) && value.contains(",")) {
            String[] split = value.split(",");
            collector.collect(new Tuple2<>(split[1], Long.parseLong(split[2])));
        }
    }
}
