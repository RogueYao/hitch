package com.heima.account.handler;

import com.heima.modules.po.VehiclePO;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AiHelper {
    @Value("${baidu.apikey}")
    private String API_KEY;
    @Value("${baidu.secretkey}")
    private String SECRET_KEY;

    private final static Logger logger = LoggerFactory.getLogger(AiHelper.class);

    final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();


    public static void main(String []args) throws IOException {
        String code = new AiHelper().getLicense(null);
        System.out.println(code);
    }

    /*

    图像识别，获取车牌信息
    文档（行驶证识别）：https://cloud.baidu.com/doc/OCR/s/yk3h7y3ks
    文档（车牌识别）：https://cloud.baidu.com/doc/OCR/s/ck3h7y191
    获取车辆照片url
    将url下载到某个临时文件夹
    将文件编码为base64
    调百度AI接口，返回对应信息
    对比：行驶证车牌 和 车辆车牌是否一致
    如果一致，设置车牌信息，认证通过，身份变更为车主

    简化版业务流程（至少完成）：识别车辆车牌号即可

    * */
    public String getLicense(VehiclePO vehiclePO) throws IOException {

        return "00000";
    }

}
