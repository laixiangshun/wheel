package com.lxs.bigdata.redission.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "redis")
public class RedisProperties {
    private String host;

    private Integer port;

    private String masterName;

    private String password;

    private Integer database;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getDatabase() {
        return database;
    }

    public void setDatabase(Integer database) {
        this.database = database;
    }
}
