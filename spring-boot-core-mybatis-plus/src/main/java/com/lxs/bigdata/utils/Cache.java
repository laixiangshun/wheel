package com.lxs.bigdata.utils;

import com.google.common.cache.*;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 完整的guava缓存实例
 *
 * @author lxs
 */
public class Cache {

    ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

    LoadingCache<String, Object> cache = CacheBuilder.newBuilder()
            .initialCapacity(10)//初始化个数
            .maximumSize(55)//设置最大个数
            .maximumWeight(1000) //设置重量，配合weigher使用
            .weigher(new Weigher<String, Object>() { //weigher相当一杆秤,称每个元数多重
                @Override
                public int weigh(String key, Object value) {
                    return 100;
                }
            })
            .expireAfterAccess(10, TimeUnit.SECONDS) //多长时间未读写后过期
            .expireAfterWrite(10, TimeUnit.SECONDS)  //多长时间未写后过期
            //指定时间内没有被创建/覆盖，则指定时间过后，再次访问时，会去刷新该缓存，在新值没有到来之前，始终返回旧值
            .refreshAfterWrite(2, TimeUnit.SECONDS)
            .concurrencyLevel(1) //写的并发数
            .softValues() //软引用
            .weakKeys() //弱引用
            .weakValues() //弱引用
            .recordStats() //统计的
            .removalListener(new RemovalListener<String, Object>() {
                @Override
                public void onRemoval(RemovalNotification<String, Object> notification) {
                    /**
                     *RemovalCause 枚举
                     * 标明是什么情况下 被移除的
                     */
                    RemovalCause cause = notification.getCause();
                    if (notification.wasEvicted()) { //是否被移除(排除主动删除,和替换)
                        System.out.println(notification.getKey() + notification.getValue());
                    }
                }
            })
            .build(new CacheLoader<Object, Object>() { //若没有元素,则创建并且放入缓存
                @Override
                public Object load(Object key) throws Exception {
                    return String.valueOf(System.currentTimeMillis());
                }

                @Override
                public ListenableFuture<Object> reload(Object key, Object oldValue) throws Exception {
                    System.out.println("......后台线程池异步刷新:" + key);
                    return service.submit(new Callable<Object>() { //模拟一个需要耗时2s的数据库查询任务
                        @Override
                        public String call() throws Exception {
                            System.out.println("begin to mock query db...");
                            Thread.sleep(2000);
                            System.out.println("success to mock query db...");
                            return UUID.randomUUID().toString() + key;
                        }
                    });
                }
            });
}
