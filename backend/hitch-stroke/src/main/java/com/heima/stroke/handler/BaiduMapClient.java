package com.heima.stroke.handler;


import com.heima.commons.domin.bo.RoutePlanResultBO;
import com.heima.commons.domin.bo.TextValue;


import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;


@Component
public class BaiduMapClient {
    @Value("${baidu.map.api}")
    private String api;
    @Value("${baidu.map.ak}")
    private String ak;
    // public static String api = "https://api.map.baidu.com/routematrix/v2/driving?";
    //
    // public static String ak = "siduZgE6XlavzaMIBoRUuEjKTY50qQPq";
    private final static Logger logger = LoggerFactory.getLogger(BaiduMapClient.class);

    // public static void main(String[] args) {
    //     BaiduMapClient baiduMapClient = new BaiduMapClient();
    //     RoutePlanResultBO routePlanResultBO = baiduMapClient.pathPlanning(
    //             "40.056878,116.30815", "40.063597,116.364973");
    //     logger.info("routePlanResultBO:{0}" + routePlanResultBO.toString());
    // }

    // TODO:任务3.2-调百度路径计算两点间的距离，和预估抵达时长

    /**
     * 调requestGetAK方法计算两点间的距离，和预估抵达时长
     *
     * @param origins
     * @param destinations
     * @return
     */
    public RoutePlanResultBO pathPlanning(String origins, String destinations) {
        Map params = new LinkedHashMap<String, String>();
        params.put("origins", origins);
        params.put("destinations", destinations);
        params.put("ak", ak);
        String AK = null;
        try {
            // 1. 获取json数据
            AK = this.requestGetAK(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (AK == null || AK.isEmpty()) {
            return null;
        }
        // 2. 解析json数据
        JSONObject jsonObject = new JSONObject(AK);
        if (jsonObject.getInt("status") == 0) {
            RoutePlanResultBO routePlanResultBO = new RoutePlanResultBO();
            TextValue distanceTextValue = new TextValue();
            TextValue durationTextValue = new TextValue();
            // 3. 获取距离和预估时长
            JSONArray result = jsonObject.getJSONArray("result");
            int mini = 0, min = result.getJSONObject(0).getJSONObject("duration").getInt("value");
            for (int i = 0; i < result.length(); i++) {
                if (result.getJSONObject(i).getJSONObject("duration").getInt("value") < min) {
                    min = result.getJSONObject(i).getJSONObject("duration").getInt("value");
                    mini = i;
                }
            }
            JSONObject distance = result.getJSONObject(mini).getJSONObject("distance");
            JSONObject duration = result.getJSONObject(mini).getJSONObject("duration");
            // 4. 处理距离和预估时长
            distanceTextValue.setText(distance.getString("text"));
            distanceTextValue.setValue(distance.getInt("value"));
            routePlanResultBO.setDistance(distanceTextValue);

            durationTextValue.setText(duration.getString("text"));
            durationTextValue.setValue(duration.getInt("value"));
            routePlanResultBO.setDuration(durationTextValue);
            return routePlanResultBO;
        }
        return null;
        // Map<String, String> reqMap = new HashMap<>();
        // reqMap.put("ak", ak);
        // reqMap.put("origins", origins);
        // reqMap.put("destinations", destinations);
        // String result = null;
        // logger.info("send to Baidu:{}",reqMap);
        // try {
        //     result = HttpClientUtils.doGet(api, reqMap);
        //     logger.info("get from Baidu:{}",result);
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
        // JSONObject jsonObject = (JSONObject) JSON.parse(result);
        // if (null != jsonObject && jsonObject.getString("status").equals("0")) {
        //     JSONArray resultArray = jsonObject.getJSONArray("result");
        //     if (null != resultArray && !resultArray.isEmpty()) {
        //         return resultArray.toJavaList(RoutePlanResultBO.class).get(0);
        //     }
        // }
        // return null;
    }

    /**
     * 调百度路径返回两点间的距离，和预估抵达时长的数据
     *
     * @param param
     * @return
     * @throws Exception
     */
    public String requestGetAK(Map<String, String> param) throws Exception {
        if (api == null || api.length() <= 0 || param == null || param.size() <= 0) {
            return "";
        }

        StringBuffer queryString = new StringBuffer();
        queryString.append(api);
        for (Map.Entry<?, ?> pair : param.entrySet()) {
            queryString.append(pair.getKey() + "=");
            //    第一种方式使用的 jdk 自带的转码方式  第二种方式使用的 spring 的转码方法 两种均可
            //    queryString.append(URLEncoder.encode((String) pair.getValue(), "UTF-8").replace("+", "%20") + "&");
            queryString.append(UriUtils.encode((String) pair.getValue(), "UTF-8") + "&");
        }

        if (queryString.length() > 0) {
            queryString.deleteCharAt(queryString.length() - 1);
        }

        URL url = new URL(queryString.toString());
        System.out.println(queryString.toString());
        URLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.connect();

        InputStreamReader isr = new InputStreamReader(httpConnection.getInputStream());
        BufferedReader reader = new BufferedReader(isr);
        StringBuffer buffer = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        reader.close();
        isr.close();
        // System.out.println("AK: " + buffer.toString());
        return buffer.toString();
    }
}
