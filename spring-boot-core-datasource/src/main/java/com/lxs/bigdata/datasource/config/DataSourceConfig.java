package com.lxs.bigdata.datasource.config;

import com.lxs.bigdata.datasource.DBTypeEnum;
import com.lxs.bigdata.datasource.core.MyRoutingDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据源配置
 */
@Configuration
public class DataSourceConfig {

    @Bean(name = "masterDataSource")
    @ConfigurationProperties("spring.datasource.master")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "slave1DataSource")
    @ConfigurationProperties("spring.datasource.slave1")
    public DataSource slave1DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "slave2DataSource")
    @ConfigurationProperties("spring.datasource.slave2")
    public DataSource slave2DataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 路由数据源
     */
    @Bean(name = "myRoutingDataSource")
    public DataSource routeDataSource(@Qualifier("masterDataSource") DataSource masterDataSource,
                                      @Qualifier("slave1DataSource") DataSource slave1DataSource,
                                      @Qualifier("slave2DataSource") DataSource slave2DataSource) {
        Map<Object, Object> targetDataSourceMap = new HashMap<>();
        targetDataSourceMap.put(DBTypeEnum.MASTER, masterDataSource);
        targetDataSourceMap.put(DBTypeEnum.SLAVE1, slave1DataSource);
        targetDataSourceMap.put(DBTypeEnum.SLAVE2, slave2DataSource);
        MyRoutingDataSource myRoutingDataSource = new MyRoutingDataSource();
        myRoutingDataSource.setDefaultTargetDataSource(masterDataSource);
        myRoutingDataSource.setTargetDataSources(targetDataSourceMap);
        return myRoutingDataSource;
    }
}
