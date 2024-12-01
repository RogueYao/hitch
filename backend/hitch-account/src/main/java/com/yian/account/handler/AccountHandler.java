package com.yian.account.handler;

import com.yian.account.service.AccountAPIService;
import com.yian.account.service.AuthenticationAPIService;
import com.yian.account.service.VehicleAPIService;
import com.yian.commons.domin.vo.response.ResponseVO;
import com.yian.commons.entity.SessionContext;
import com.yian.commons.enums.BusinessErrors;
import com.yian.commons.exception.BusinessRuntimeException;
import com.yian.commons.helper.RedisSessionHelper;
import com.yian.commons.template.SessionTemplate;
import com.yian.commons.utils.CommonsUtils;
import com.yian.commons.utils.RequestUtils;
import com.yian.commons.utils.SnowflakeIdWorker;
import com.yian.modules.po.AccountPO;
import com.yian.modules.po.AuthenticationPO;
import com.yian.modules.po.VehiclePO;
import com.yian.modules.vo.AccountVO;
import com.yian.modules.vo.AuthenticationVO;
import com.yian.modules.vo.VehicleVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountHandler {
    private final static Logger logger = LoggerFactory.getLogger(AccountHandler.class);
    private SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);

    @Autowired
    private RedisSessionHelper redisSessionHelper;

    @Autowired
    private SessionTemplate sessionTemplate;

    @Autowired
    private AccountAPIService accountAPIService;

    @Autowired
    private AuthenticationAPIService authenticationAPIService;

    @Autowired
    private VehicleAPIService vehicleAPIService;

    @Autowired
    private AiHelper aiHelper;

    /**
     * 用户注册
     *
     * @param accountVO
     * @return
     */
    public ResponseVO<AccountVO> register(AccountVO accountVO) {
        if (StringUtils.isAnyEmpty(accountVO.getUsername(), accountVO.getPhone(), accountVO.getPassword())) {
            throw new BusinessRuntimeException(BusinessErrors.PARAM_CANNOT_EMPTY);
        }
        AccountPO accountPO = accountAPIService.checkLogin(CommonsUtils.toPO(accountVO));
        if (null != accountPO) {
            throw new BusinessRuntimeException(BusinessErrors.DATA_DUPLICATION);
        }
        accountVO.setPassword(CommonsUtils.encodeMD5(accountVO.getPassword()));
        accountVO.setId(String.valueOf(idWorker.nextId()));
        //默认身份：乘客，车主认证后 为1
        accountVO.setRole(0);
        //默认用户未实名认证 认证通过后改为1
        accountVO.setStatus(0);
        //添加用户认证信息
        AccountPO result = accountAPIService.register(CommonsUtils.toPO(accountVO));
        return ResponseVO.success(result);
    }

    /**
     * 用户密码修改
     *
     * @param accountVO
     * @return
     */
    public ResponseVO<AccountVO> modifyPassword(AccountVO accountVO) {
        //获取当前登录用户的id
        String userId = accountVO.getCurrentUserId();

        AccountPO accountPO = accountAPIService.getAccountByID(userId);
        if (!accountPO.getPassword().equals(CommonsUtils.encodeMD5(accountVO.getPassword())))
            throw new BusinessRuntimeException(BusinessErrors.DATA_STATUS_ERROR, "原密码错误");

        if (accountPO.getPassword().equals(CommonsUtils.encodeMD5(accountVO.getNewPassword())))
            throw new BusinessRuntimeException(BusinessErrors.DATA_STATUS_ERROR, "新密码不能与原密码相同");

        accountPO.setPassword(CommonsUtils.encodeMD5(accountVO.getNewPassword()));
        accountAPIService.update(accountPO);
        //TODO:任务1-修改密码-1day
        //获取当前用户在数据库里的信息
        //旧密码加密，对比数据库，防止输入错误
        //新密码加密，对比旧密码，不允许相同
        //校验通过，将新密码写入数据库，修改成功


        return ResponseVO.success(null, "修改密码成功");
    }

    /**
     * 修改用户信息
     *
     * @param accountVO
     * @return
     */
    public ResponseVO<AccountVO> modify(AccountVO accountVO) {
        AccountPO accountPO = accountAPIService.getAccountByID(accountVO.getCurrentUserId());
        if (null == accountPO) {
            throw new BusinessRuntimeException(BusinessErrors.DATA_NOT_EXIST, "用户信息不存在");
        }
        AccountPO po = CommonsUtils.toPO(accountVO);
        po.setId(accountPO.getId());
        accountAPIService.update(po);
        return ResponseVO.success(null, "修改用户信息成功");
    }


    /**
     * 生成Token
     *
     * @param accountVO
     * @return
     */
    public ResponseVO accountLogin(AccountVO accountVO) {
        AccountVO vo = verifyAccountLogin(accountVO);
        SessionContext sessionContext = redisSessionHelper.createSession(vo, vo.getId(), vo.getUsername(), vo.getUseralias(), null);
        vo.setToken(sessionContext.getSessionID());
        return ResponseVO.success(vo);
    }

    /**
     * 获取用户基本信息
     *
     * @param
     * @return
     */
    public ResponseVO<AccountVO> userinfo() {
        AccountPO accountPO = getCurrentAccountPO();
        return ResponseVO.success(accountPO);
    }


    /**
     * 获取用户认证信息
     *
     * @param
     * @return
     */
    public ResponseVO<AuthenticationVO> getAuthenticationInfo() {
        AccountPO accountPO = getCurrentAccountPO();
        AuthenticationPO authenticationPO = getAuthenticationPO(accountPO);
        if (null == authenticationPO) {
            throw new BusinessRuntimeException(BusinessErrors.DATA_NOT_EXIST, "用户认证信息不存在");
        }
        return ResponseVO.success(authenticationPO);
    }

    /**
     * 修改用户认证信息
     *
     * @param authenticationVO
     * @return
     */
    public ResponseVO<AuthenticationVO> modifyAuthentication(AuthenticationVO authenticationVO) {
        AccountPO accountPO = getCurrentAccountPO();
        AuthenticationPO authenticationPO = getAuthenticationPO(accountPO);
        if (null == authenticationPO) {
            throw new BusinessRuntimeException(BusinessErrors.DATA_NOT_EXIST, "用户认证信息不存在");
        }
        AuthenticationPO updatePo = CommonsUtils.toPO(authenticationVO);
        updatePo.setId(authenticationPO.getId());
        authenticationAPIService.update(updatePo);
        return ResponseVO.success(updatePo);
    }

    /**
     * 身份认证接口
     *
     * @return
     */
    public ResponseVO<AuthenticationVO> identityAuth() {
        AccountPO accountPO = getCurrentAccountPO();
        AuthenticationPO authenticationPO = getAuthenticationPO(accountPO);
        if (null == authenticationPO) {
            throw new BusinessRuntimeException(BusinessErrors.DATA_NOT_EXIST, "用户认证信息不存在");
        }
        String cardIdFrontPhotoAddr = authenticationPO.getCardIdFrontPhoto();
        if (StringUtils.isEmpty(cardIdFrontPhotoAddr)) {
            throw new BusinessRuntimeException(BusinessErrors.DATA_NOT_EXIST, "身份证正面照片不存在");
        }

        //TODO:任务2.2-个人实名认证
        //【可选作业】：调百度完成身份证识别，将识别信息更新到数据库对应字段
        //文档（身份证识别）：https://cloud.baidu.com/doc/OCR/s/rk3h7xzck
        //文档（h5人脸实名认证接口）：https://ai.baidu.com/ai-doc/FACE/skxie72kp
        //备注：真实的实名认证需要企业身份，个人无法使用。


        //真实业务需要设置Account用户真实姓名，这里直接用用户名
        accountPO.setUseralias(accountPO.getUsername());
        accountPO.setStatus(1); //状态改成已认证
        //更新Redis缓存
        sessionTemplate.updateSessionUseralias(accountPO.getId(), accountPO.getUseralias());
        accountAPIService.update(accountPO);
        authenticationAPIService.update(authenticationPO);
        return ResponseVO.success(authenticationPO);
    }


    /**
     * 获取车辆认证信息
     *
     * @return
     */
    public ResponseVO<VehicleVO> getVehicleInfo() {
        AccountPO accountPO = getCurrentAccountPO();
        VehiclePO vehiclePO = getVehiclePO(accountPO);
        if (null == vehiclePO) {
            throw new BusinessRuntimeException(BusinessErrors.DATA_NOT_EXIST, "用户认证信息不存在");
        }
        return ResponseVO.success(vehiclePO);
    }


    /**
     * 修改车辆信息
     *
     * @param vehicleVO
     * @return
     */
    public ResponseVO<VehicleVO> modifyVehicle(VehicleVO vehicleVO) {
        AccountPO accountPO = getCurrentAccountPO();
        VehiclePO vehiclePO = getVehiclePO(accountPO);
        if (null == vehiclePO) {
            throw new BusinessRuntimeException(BusinessErrors.DATA_NOT_EXIST, "车辆认证信息不存在");
        }
        VehiclePO updatePo = CommonsUtils.toPO(vehicleVO);
        updatePo.setId(vehiclePO.getId());
        vehicleAPIService.update(updatePo);
        return ResponseVO.success(updatePo);
    }


    /**
     * 车牌识别
     *
     * @return
     */
    public ResponseVO<VehicleVO> vehicleAuth() {
        AccountPO accountPO = getCurrentAccountPO();
        VehiclePO vehiclePO = getVehiclePO(accountPO);
        try {
            //TODO:任务2.1-车辆信息验证入口-2day
            String license = aiHelper.getOwnerAuth(vehiclePO);
            vehiclePO.setCarNumber(license);
            accountPO.setRole(1);
            accountAPIService.update(accountPO);
            vehicleAPIService.update(vehiclePO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.error(e.getMessage());
        }
        return ResponseVO.success(vehiclePO);
    }


    private VehiclePO getVehiclePO(AccountPO accountPO) {
        return vehicleAPIService.selectByPhone(accountPO.getPhone());
    }

    /**
     * 获取当前登录用户
     *
     * @return
     */
    private AccountPO getCurrentAccountPO() {
        String userId = RequestUtils.getCurrentUserId();
        if (StringUtils.isEmpty(userId)) {
            throw new BusinessRuntimeException(BusinessErrors.AUTHENTICATION_ERROR);
        }
        AccountPO accountPO = accountAPIService.getAccountByID(userId);
        if (null == accountPO) {
            throw new BusinessRuntimeException(BusinessErrors.DATA_NOT_EXIST, "用户信息不存在");
        }
        return accountPO;
    }

    /**
     * 获取用户认证对象
     *
     * @param accountPO
     * @return
     */

    private AuthenticationPO getAuthenticationPO(AccountPO accountPO) {
        if (null == accountPO) {
            throw new BusinessRuntimeException(BusinessErrors.DATA_NOT_EXIST, "用户信息不存在");
        }
        return authenticationAPIService.selectByPhone(accountPO.getPhone());
    }

    /**
     * 验证账户登录
     *
     * @param accountVO
     * @return
     */
    private AccountVO verifyAccountLogin(AccountVO accountVO) {
        //用户或者手机号不能为空
        if (StringUtils.isAllEmpty(accountVO.getUsername(), accountVO.getPhone())) {
            throw new BusinessRuntimeException(BusinessErrors.PARAM_CANNOT_EMPTY);
        }
        //密码不能为空
        if (StringUtils.isEmpty(accountVO.getPassword())) {
            throw new BusinessRuntimeException(BusinessErrors.PARAM_CANNOT_EMPTY, "密码不能为空");
        }
        //查询用户登录
        AccountPO accountPO = accountAPIService.checkLogin(CommonsUtils.toPO(accountVO));
        //非空校验
        if (null == accountPO) {
            throw new BusinessRuntimeException(BusinessErrors.DATA_NOT_EXIST);
        }
        //验证密码
        if (!CommonsUtils.encodeMD5(accountVO.getPassword()).equals(accountPO.getPassword())) {
            logger.warn("password error! account="+accountPO.getUseralias());
            throw new BusinessRuntimeException(BusinessErrors.PARAM_CANNOT_EMPTY, "用户名或者密码错误");
        }
        return (AccountVO) CommonsUtils.toVO(accountPO);
    }

    /**
     * 验证Token
     *
     * @param sessionID
     * @return
     */
    public ResponseVO verifyToken(String sessionID) {
        SessionContext sessionContext = redisSessionHelper.getSession(sessionID);
        if (null == sessionContext) {
            throw new BusinessRuntimeException(BusinessErrors.TOKEN_IS_INVALID);
        }
        if (StringUtils.isEmpty(sessionContext.getAccountID())) {
            throw new BusinessRuntimeException(BusinessErrors.TOKEN_IS_INVALID);
        }
        AccountPO accountPO = accountAPIService.getAccountByID(sessionContext.getAccountID());
        if (null == accountPO) {
            throw new BusinessRuntimeException(BusinessErrors.DATA_NOT_EXIST);
        }

        return ResponseVO.success(accountPO);
    }

}
