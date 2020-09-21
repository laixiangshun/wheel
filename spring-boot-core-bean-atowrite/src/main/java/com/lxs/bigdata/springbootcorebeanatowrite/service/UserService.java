package com.lxs.bigdata.springbootcorebeanatowrite.service;

public class UserService {

    private String name;

    public UserService(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void print() {
        System.out.println("动态注入bean，name=" + name);
    }
}
