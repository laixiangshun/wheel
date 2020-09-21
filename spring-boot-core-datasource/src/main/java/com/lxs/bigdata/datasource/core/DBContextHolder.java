package com.lxs.bigdata.datasource.core;

import com.lxs.bigdata.datasource.DBTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 通过ThreadLocal将数据源设置到每个线程上下文中
 */
public class DBContextHolder {

    private static final Logger logger = LoggerFactory.getLogger(DBContextHolder.class);

    private static final ThreadLocal<DBTypeEnum> contextHolder = new ThreadLocal<>();

    private static final AtomicInteger counter = new AtomicInteger(-1);

    private static void set(DBTypeEnum dbTypeEnum) {
        contextHolder.set(dbTypeEnum);
    }

    static DBTypeEnum get() {
        return contextHolder.get();
    }

    public static void master() {
        set(DBTypeEnum.MASTER);
        logger.debug("切换到master数据库");
    }

    public static void slave() {
        int increment = counter.getAndIncrement();
        int index = increment % 2;
        if (counter.get() > 9999) {
            counter.set(-1);
        }
        if (index == 0) {
            set(DBTypeEnum.SLAVE1);
            logger.debug("切换到slave1数据源");
        } else {
            set(DBTypeEnum.SLAVE2);
            logger.debug("切换到slave2数据源");
        }
    }
}
