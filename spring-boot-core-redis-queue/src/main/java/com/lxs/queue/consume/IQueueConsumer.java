package com.lxs.queue.consume;

/**
 * 自定义redis队列数据消费
 *
 * @author lxs
 */
public interface IQueueConsumer {
    
    /**
     * 消费数据
     *
     * @throws Exception
     */
    void consume() throws Exception;
}
