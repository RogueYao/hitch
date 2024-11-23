package com.yian.modules.vo.license;

import lombok.Data;

import java.util.List;


@Data
public class Words_result {
    private String number;
    private List<Vertexes_location> vertexes_location;
    private String color;
    private List<Double> probability;

}
