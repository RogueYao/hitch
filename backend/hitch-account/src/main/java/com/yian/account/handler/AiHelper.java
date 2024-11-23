package com.yian.account.handler;

import com.google.gson.Gson;
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
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

@Component
public class AiHelper {
    @Value("${baidu.apikey}")
    private String API_KEY;
    @Value("${baidu.secretkey}")
    private String SECRET_KEY;
    // private String API_KEY="N2wQVstcLgMxND4C2xmdYJyn";
    // private String SECRET_KEY="7M2clFA8VKCGhCQMrKhLfhLX3Sqn6ToW";
    @Value("${baidu.vehiclelicense_url}")
    private String VEHICLELICENSE_URL;
    @Value("${baidu.licenseplate_url}")
    private String LICENSEPLATE_URL;
    // private String LICENSEPLATE_URL ="https://aip.baidubce.com/rest/2.0/ocr/v1/license_plate";
    private final static Logger logger = LoggerFactory.getLogger(AiHelper.class);

    final static OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();


    public static void main(String[] args) throws IOException {
        // String path = AiHelper.class.getResource("/img/").getPath()+"id_card_front.jpg";
        // ClassLoader classLoader = AiHelper.class.getClassLoader();
        // URL resource = classLoader.getResource("img/id_card_front.jpg");
        // String path = resource.getPath();
        // System.out.println(path);
        // String idCard = new AiHelper().getIdCard();
        // boolean exists = resource != null;
        // System.out.println("Resource exists: " + exists);
        // logger.info(path);


        String code = new AiHelper().getAccessToken();
        logger.info("code:{}", code);
        // String path = AiHelper.class.getResource("/img/license_plate_numb.png").getPath();
        // logger.info("path:{}", path);
        // LicensePlate licensePlate = new AiHelper().getLicensePlate(path);
        // logger.info("licensePlate:{}", licensePlate.getWords_result().getNumber());
    }

    /**
     * 获取Access_token方法
     */
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
        /**
         * 行驶证
         */
        // String carBackPhoto = vehiclePO.getCarBackPhoto();
        // File vehicleLicenseFile = new File(AiHelper.class.getResource("/").getPath() +
        //         "back-" + vehiclePO.getId() + carBackPhoto.substring(carBackPhoto.lastIndexOf("."), carBackPhoto.length()));
        // logger.info("vehicleLicenseFile:{}", vehicleLicenseFile.getAbsolutePath());
        // FileUtils.copyURLToFile(new URL(carBackPhoto), vehicleLicenseFile);
        // String vehicleLicense = this.getVehicleLicense(vehicleLicenseFile.getAbsolutePath());
        // if (vehicleLicenseFile.exists()) {
        //     vehicleLicenseFile.delete();
        // }
        // ObjectMapper objectMapper = new ObjectMapper();
        // JsonNode rootNode = objectMapper.readTree(vehicleLicense);
        // if (rootNode.get("error_code") != null) {
        //     throw new BusinessRuntimeException(BusinessErrors.DATA_STATUS_ERROR, rootNode.get("error_msg").asText());
        // }
        // if (vehicleLicenseFile.exists()) {
        //     vehicleLicenseFile.delete();
        // }
        // logger.info("号牌号码:{}",rootNode.get("words_result").get("号牌号码").get("words").asText());
        // String vehicleLicenseNumber = rootNode.get("words_result").get("号牌号码").get("words").asText();
        /**
         * 车牌
         */
        String carFrontPhoto = vehiclePO.getCarFrontPhoto();
        File licensePlateFile = new File(
                AiHelper.class.getResource("/").getPath() +
                        "front-" + vehiclePO.getId() + carFrontPhoto.substring(carFrontPhoto.lastIndexOf("."), carFrontPhoto.length()));

        logger.info("licensePlateFile:{}", licensePlateFile.getAbsolutePath());
        FileUtils.copyURLToFile(new URL(carFrontPhoto), licensePlateFile);
        LicensePlate licensePlate = this.getLicensePlate(licensePlateFile.getAbsolutePath());

        // if (licensePlate==null){
        //     throw new BusinessRuntimeException(BusinessErrors.DATA_STATUS_ERROR,"未识别到车牌");
        // }
        String licensePlateNumber = licensePlate.getWords_result().getNumber();
        if (licensePlateFile.exists()){
            licensePlateFile.delete();
        }
        return licensePlateNumber;
        // logger.info("licensePlateNumber:{}", licensePlateNumber);
        // if (licensePlateNumber.equals(vehicleLicenseNumber)) {
        //     return licensePlateNumber;
        // } else {
        //     throw new BusinessRuntimeException(BusinessErrors.DATA_STATUS_ERROR, "车牌号不一致");
        // }

    }

    public String getLicense(String ocrUrl, String imgPath) {
        try {
            byte[] imgData = FileUtil.readFileByBytes(imgPath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, param);
            Request request = new Request.Builder()
                    .url(ocrUrl + "?access_token=" + getAccessToken())
                    .method("POST", body) // Use .post() instead of .method("POST", body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Accept", "application/json")
                    .build();
            Response response = HTTP_CLIENT.newCall(request).execute();
            String json = response.body().string();
            logger.info("response:{}", json);

            return json;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String getVehicleLicense(String imgPath) {
        // 请求url
        // String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/vehicle_license";
        try {
            // 本地文件路径
            byte[] imgData = FileUtil.readFileByBytes(imgPath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String accessToken = "24.3e4164a390112a70d25cef4ec28d32ab.2592000.1723871591.282335-95848044";

            String param = "image=" + imgParam;
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, param);
            Request request = new Request.Builder()
                    .url(VEHICLELICENSE_URL + "?access_token=" + accessToken)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Accept", "application/json")
                    .build();
            Response response = HTTP_CLIENT.newCall(request).execute();
            String json = response.body().string();
            logger.info("response:{}", json);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public LicensePlate getLicensePlate(String imgPath) {
        // 请求url
        // String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/license_plate";
        try {
            byte[] imgData = FileUtil.readFileByBytes(imgPath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;
            String accessToken = "24.d8d5e74d1f31dc2b1172137cfb3bb039.2592000.1725694857.282335-95848044";
            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String result = HttpUtil.post(LICENSEPLATE_URL, accessToken, param);
            LicensePlate licensePlate = new Gson().fromJson(result, LicensePlate.class);
            return licensePlate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
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
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */
    private String getAccessToken() throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return new JSONObject(response.body().string()).getString("access_token");
    }
}
