package com.heima.stroke.handler;

import com.heima.commons.domin.bo.RoutePlanResultBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BaiduMapClient {
    @Value("${baidu.map.api}")
    private String api;
    @Value("${baidu.map.ak}")
    private String ak;

    private final static Logger logger = LoggerFactory.getLogger(BaiduMapClient.class);

    //TODO:任务3.2-调百度路径计算两点间的距离，和预估抵达时长
    public RoutePlanResultBO pathPlanning(String origins, String destinations) {

        return null;
    }

}
