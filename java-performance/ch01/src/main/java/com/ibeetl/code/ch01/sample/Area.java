package com.ibeetl.code.ch01.sample;

import lombok.Data;

@Data
public class Area {
    private Integer provinceId;
    private Integer cityId;
    private Integer townId;

    public Area() {
    }

    public Area(Integer provinceId, Integer cityId) {
        this.provinceId = provinceId;
        this.cityId = cityId;
    }

    public CityKey buildKey(){
        return new CityKey(provinceId,cityId);
    }
}
