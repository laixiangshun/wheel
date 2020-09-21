package com.lxs.bigdata.es.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IntelligentStoreDTO {

    private double lat;

    private double lon;

    private double distance;

    private Integer pageIndex;

    private Integer pageSize;
}
