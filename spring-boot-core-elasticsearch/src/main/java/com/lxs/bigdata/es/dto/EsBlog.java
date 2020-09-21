package com.lxs.bigdata.es.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * 博客映射es的实体
 *
 * @author lxs
 */
@Data
@Document(indexName = "blog", type = "blog")
public class EsBlog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field(index = FieldIndex.not_analyzed, type = FieldType.Long)
    private Long blogId;

    @Field(analyzer = "ik_max_word", type = FieldType.String)
    private String title;

    /**
     * 概要
     */
    @Field(analyzer = "ik_max_word", type = FieldType.String)
    private String summary;

    @Field(analyzer = "ik_max_word", type = FieldType.String)
    private String content;

    @Field(index = FieldIndex.not_analyzed, type = FieldType.Integer)
    private Integer commentSize;

    @Field(type = FieldType.String,index = FieldIndex.analyzed)
    private String userName;
}
