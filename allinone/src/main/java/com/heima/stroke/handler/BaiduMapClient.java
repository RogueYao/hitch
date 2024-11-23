package com.heima.stroke.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.heima.commons.domin.bo.RoutePlanResultBO;
import com.heima.commons.utils.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BaiduMapClient {
    @Value("${baidu.map.api}")
    private String api;
    @Value("${baidu.map.ak}")
    private String ak;

    private final static Logger logger = LoggerFactory.getLogger(BaiduMapClient.class);

    public RoutePlanResultBO pathPlanning(String origins, String destinations) {
        Map<String, String> reqMap = new HashMap<>();
        reqMap.put("ak", ak);
        reqMap.put("origins", origins);
        reqMap.put("destinations", destinations);
        String result = null;
        logger.info("send to Baidu:{}",reqMap);
        try {
            result = HttpClientUtils.doGet(api, reqMap);
            logger.info("get from Baidu:{}",result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = (JSONObject) JSON.parse(result);
        if (null != jsonObject && jsonObject.getString("status").equals("0")) {
            JSONArray resultArray = jsonObject.getJSONArray("result");
            if (null != resultArray && !resultArray.isEmpty()) {
                return resultArray.toJavaList(RoutePlanResultBO.class).get(0);
            }
        }
        return null;
    }

}
