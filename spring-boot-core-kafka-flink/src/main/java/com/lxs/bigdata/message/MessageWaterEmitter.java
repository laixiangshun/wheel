package com.lxs.bigdata.message;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;

/**
 * 根据Kafka消息确定Flink的水位
 */
public class MessageWaterEmitter implements AssignerWithPunctuatedWatermarks<String> {

    @Nullable
    @Override
    public Watermark checkAndGetNextWatermark(String lastElement, long extractTime) {
        if (StringUtils.isNotBlank(lastElement) && lastElement.contains(",")) {
            String[] split = lastElement.split(",");
            return new Watermark(Long.parseLong(split[0]));
        }
        return null;
    }

    @Override
    public long extractTimestamp(String element, long previousElementTimestamp) {
        if (StringUtils.isNotBlank(element) && element.contains(",")) {
            String[] split = element.split(",");
            return Long.parseLong(split[0]);
        }
        return 0;
    }
}
