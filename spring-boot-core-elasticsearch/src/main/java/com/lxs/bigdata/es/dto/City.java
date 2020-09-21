package com.lxs.bigdata.es.dto;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class City {

    private long id;// id
    private String city;// 城市名
    private double lat;// 纬度
    private double lon;// 经度
    private double[] location;// 经纬度数组，第一个元素纬度，第二个元素经度
    private String title;// 标题

    public City(long id, String city, double lon, double lat, String title) {
        super();
        this.id = id;
        this.city = city;
        this.lat = lat;
        this.lon = lon;
        this.title = title;
    }

}
