package com.yian.account.handler;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.yian.commons.constant.HtichConstants;
import com.yian.commons.enums.BusinessErrors;
import com.yian.commons.exception.BusinessRuntimeException;
import com.yian.commons.utils.baidu.Base64Util;
import com.yian.commons.utils.baidu.FileUtil;
import com.yian.commons.utils.baidu.HttpUtil;
import com.yian.modules.po.VehiclePO;
import com.yian.modules.vo.license.LicensePlate;
import okhttp3.*;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

@Component
public class AiHelper {
    @Value("${baidu.apikey}")
    private static String API_KEY;
    @Value("${baidu.secretkey}")
    private static String SECRET_KEY;
    // private String API_KEY="N2wQVstcLgMxND4C2xmdYJyn";
    // private String SECRET_KEY="7M2clFA8VKCGhCQMrKhLfhLX3Sqn6ToW";
    @Value("${baidu.vehiclelicense_url}")
    private String VEHICLELICENSE_URL;
    @Value("${baidu.licenseplate_url}")
    private String LICENSEPLATE_URL;
    // private String LICENSEPLATE_URL ="https://aip.baidubce.com/rest/2.0/ocr/v1/license_plate";
    private final static Logger logger = LoggerFactory.getLogger(AiHelper.class);
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    protected static String accessToken="";
    final static OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();


    public static void main(String[] args) throws IOException {
        // 获取access_token 注意Resource和value注解失效
        new AiHelper().getAccessToken();

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

    /**
     * 车辆所有者认证
     * @param vehiclePO
     * @return
     * @throws IOException
     */
    public String getOwnerAuth(VehiclePO vehiclePO) throws IOException {

        /**
         * 车牌
         */
        String carFrontPhoto = vehiclePO.getCarFrontPhoto();
        File licensePlateFile = new File(
                AiHelper.class.getResource("/").getPath() +
                        "front-" + vehiclePO.getId() + carFrontPhoto.substring(carFrontPhoto.lastIndexOf("."), carFrontPhoto.length()));

        logger.info("licensePlateFile:{}", licensePlateFile.getAbsolutePath());
        FileUtils.copyURLToFile(new URL(carFrontPhoto), licensePlateFile);
        String plateNumber = getLicensePlateNum(licensePlateFile.getAbsolutePath());
        if (licensePlateFile.exists()){
            licensePlateFile.delete();
        }
        if (plateNumber==null|| plateNumber.isEmpty()){
            throw new BusinessRuntimeException(BusinessErrors.DATA_STATUS_ERROR,"未识别到车牌");
        }
        // return plateNumber;
        /**
         * 行驶证
          */
        String carBackPhoto = vehiclePO.getCarBackPhoto();
        File licenseFile = new File(
                AiHelper.class.getResource("/").getPath() +
                        "front-" + vehiclePO.getId() + carFrontPhoto.substring(carFrontPhoto.lastIndexOf("."), carFrontPhoto.length()));

        logger.info("licenseFile:{}", licenseFile.getAbsolutePath());
        FileUtils.copyURLToFile(new URL(carFrontPhoto), licenseFile);
        String licenseNumber = getVehicleLicenseNum(licenseFile.getAbsolutePath());
        if (licenseFile.exists()){
            licenseFile.delete();
        }
        if (licenseNumber==null|| licenseNumber.isEmpty()){
            throw new BusinessRuntimeException(BusinessErrors.DATA_STATUS_ERROR,"未识别到车牌");
        }
        // 判断车牌号与是行驶证车牌号是否一致
        if (plateNumber.equals(licenseNumber)) {
            return plateNumber;
        } else {
            throw new BusinessRuntimeException(BusinessErrors.DATA_STATUS_ERROR, "车牌号不一致");
        }

    }
    /**
     * 获取行驶证号码
     * @param imgPath
     * @return
     */
    public String getVehicleLicenseNum(String imgPath) {
        // String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/license_plate";
        try {
            byte[] imgData = FileUtil.readFileByBytes(imgPath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;
            // String accessToken = "24.d8d5e74d1f31dc2b1172137cfb3bb039.2592000.1725694857.282335-95848044";
            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            if (accessToken==null|| accessToken.isEmpty()){
                getAccessToken();
            }
            String response = HttpUtil.post(VEHICLELICENSE_URL, accessToken, param);
            String errorCode = JSON.parseObject(response).getString("error_code");
            if ("110".equals(errorCode)){
                getAccessToken();
                return getVehicleLicenseNum(imgPath);
            }
            String number = JSON.parseObject(response).getString("Number");
            logger.info("获取行驶证号码accessToken:{},response:{}", accessToken,number);
            return number;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取车牌号码
     * @param imgPath
     * @return
     */
    public String getLicensePlateNum(String imgPath) {
        // String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/license_plate";
        try {
            byte[] imgData = FileUtil.readFileByBytes(imgPath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;
            // String accessToken = "24.d8d5e74d1f31dc2b1172137cfb3bb039.2592000.1725694857.282335-95848044";
            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            if (accessToken==null|| accessToken.isEmpty()){
                getAccessToken();
            }
            String response = HttpUtil.post(LICENSEPLATE_URL, accessToken, param);
            String errorCode = JSON.parseObject(response).getString("error_code");
            if ("110".equals(errorCode)){
                getAccessToken();
                return getLicensePlateNum(imgPath);
            }
            String number = JSON.parseObject(response).getString("Number");
            logger.info("获取车牌号码 accessToken:{},response:{}", accessToken,number);

            return number;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取身份证号码
     * @return
     */
    public String getIdCard(){
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/idcard";
        try {
            // 本地文件路径
            String filePath = AiHelper.class.getClassLoader().getResource("img/id_card_front.jpg").getPath();
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "id_card_side=" + "front" + "&image=" + imgParam;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken="";
            String result = HttpUtil.post(url, accessToken, param);
            logger.info("response:{}", result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     * Access token默认有效期为30天
     *
     * @throws IOException IO异常
     */
    private String getAccessToken() throws IOException {
        // 从缓存中获取access_token
        String accessToken = redisTemplate.opsForValue().get(HtichConstants.ACCESS_TOKEN);
        if (accessToken!=null){
            logger.info("accessToken:{}", accessToken);
            AiHelper.accessToken = accessToken;
            return accessToken;
        }
        // 从外部获取access_token
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        if (200!=response.code()){
            throw new BusinessRuntimeException(BusinessErrors.AUTHENTICATION_ERROR,"更新access_token失败");
        }
        String updateAccessToken = JSON.parseObject(response.body().string()).getString("access_token");
        // 更新redis 提前一天更新，避免token失效
        redisTemplate.opsForValue().set(HtichConstants.ACCESS_TOKEN, updateAccessToken, (30-1)*24*60*60, TimeUnit.SECONDS);
        AiHelper.accessToken = updateAccessToken;
        logger.info("accessToken:{}", updateAccessToken);
        return updateAccessToken;

    }

}
