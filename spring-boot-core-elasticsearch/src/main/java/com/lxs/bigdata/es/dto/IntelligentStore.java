package com.lxs.bigdata.es.dto;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.GeoPointField;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "es_store", createIndex = true)
public class IntelligentStore implements Serializable {

    private String id;

    /**
     * 体验店名称
     */
    private String name;
    /**
     * 联系人姓名
     */
    private String contactsName;
    /**
     * 联系手机号
     */
    private String phoneNumber;
    /**
     * 联系电话
     */
    private String telePhone;
    /**
     * 场景分类
     */
    private String category;
    /**
     * 开店时间
     */
    private LocalDate foundTime;
    /**
     * 营业时间
     */
    private String openHours;
    /**
     * 主营产品
     */
    private String mainProduct;
    /**
     * 店铺面积
     */
    private Double shopSize;
    /**
     * xx省xx市xx区
     */
    private String area;
    /**
     * 店铺面积
     */
    private String detailAddress;
    /**
     * 经维度，中间逗号隔开
     */
    @GeoPointField
    private String location;
}
