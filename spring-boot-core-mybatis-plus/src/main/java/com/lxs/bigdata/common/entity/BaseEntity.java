package com.lxs.bigdata.common.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.MappedSuperclass;
import java.util.Date;

@Data
//@MappedSuperclass标识的类表示其不能映射到数据库表，
// 因为其不是一个完整的实体类，但是它所拥有的属性能够隐射在其子类对用的数据库表中
@MappedSuperclass
public class BaseEntity {
    @CreatedDate
    private Date createTime;

    @LastModifiedDate
    private Date updateTime;
}
