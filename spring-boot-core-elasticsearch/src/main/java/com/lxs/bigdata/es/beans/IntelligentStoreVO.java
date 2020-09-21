package com.lxs.bigdata.es.beans;

import lombok.*;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntelligentStoreVO {

    private String id;

    /**
     * 体验店名称
     */
    private String name;

    /**
     * xx省xx市xx区
     */
    private String area;

    private String detailAddress;

    private String location;

    private String distance;

    private String createTime;

    private String updateTime;
}
