package com.lxs.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.util.concurrent.Executor;

/**
 * 异步线程池，用于异步处理rabbitmq消息
 *
 * @author lxs
 */
@Configuration
public class WebSocketConfig {
    
    /**
     * 暴露endpoint， 交给Spring IOC容器，表示要开启WebSocket功能
     * 当ServerEndpointExporter类通过Spring配置进行声明并被使用，它将会去扫描带有@ServerEndpoint注解的类
     * 被注解的类将被注册成为一个WebSocket端点
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
    
    /**
     * 异步线程池，用于异步处理rabbitmq消息
     */
    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);//线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean
        taskExecutor.setAwaitTerminationSeconds(60);//设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setMaxPoolSize(8);
        taskExecutor.setQueueCapacity(Integer.MAX_VALUE);//缓冲执行任务的队列
        taskExecutor.setKeepAliveSeconds(60);//当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
        taskExecutor.setThreadNamePrefix("kanjia-websocket-thread-");
        return taskExecutor;
    }
}
