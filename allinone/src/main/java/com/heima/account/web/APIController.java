package com.heima.account.web;

import com.heima.account.handler.AccountHandler;
import com.heima.commons.constant.HtichConstants;
import com.heima.commons.domin.vo.response.ResponseVO;
import com.heima.commons.enums.BusinessErrors;
import com.heima.commons.exception.BusinessRuntimeException;
import com.heima.commons.groups.Group;
import com.heima.commons.initial.annotation.RequestInitial;
import com.heima.commons.utils.CommonsUtils;
import com.heima.modules.vo.AccountVO;
import com.heima.modules.vo.AuthenticationVO;
import com.heima.modules.vo.VehicleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController("account")
@RequestMapping("/account/api/")
@Api(value = "账户操作Controller", tags = {"账户管理"})
@ApiResponses(@ApiResponse(code = 200, message = "处理成功"))
public class APIController {

    @Autowired
    private AccountHandler accountHandler;

    private final static Logger logger = LoggerFactory.getLogger(APIController.class);

    @ApiOperation(value = "用户注册接口", tags = {"账户管理"})
    @PostMapping("/register")
    @RequestInitial(groups = {Group.Create.class})
    public ResponseVO<AccountVO> register(@Validated(Group.Create.class) @RequestBody AccountVO accountVO) {
        return accountHandler.register(accountVO);
    }

    @ApiOperation(value = "用户登录接口", tags = {"账户管理"})
    @PostMapping("/login")
    public ResponseVO<AccountVO> login(@Validated(Group.Select.class) @RequestBody AccountVO accountVO, HttpServletRequest request) {
        AccountVO vo = accountHandler.verifyAccountLogin(accountVO);
        request.getSession().setAttribute("user",vo);
        return ResponseVO.success(vo);
    }

    @ApiOperation(value = "修改密码接口", tags = {"账户管理"})
    @PostMapping("/modifyPassword")
    @RequestInitial
    public ResponseVO<AccountVO> modifyPassword(@RequestBody AccountVO accountVO) {
        return accountHandler.modifyPassword(accountVO);
    }


    @ApiOperation(value = "修改用户信息接口", tags = {"账户管理"})
    @PostMapping("/modify")
    @RequestInitial
    public ResponseVO<AccountVO> modify(@RequestBody AccountVO accountVO) {
        return accountHandler.modify(accountVO);
    }


    @ApiOperation(value = "获取用户基本信息", tags = {"账户管理"})
    @PostMapping("/userinfo")
    @RequestInitial
    public ResponseVO<AccountVO> userinfo() {
        return accountHandler.userinfo();
    }

    @ApiOperation(value = "获取用户认证信息", tags = {"账户管理"})
    @PostMapping("/getAuthenticationInfo")
    @RequestInitial(groups = {Group.Select.class})
    public ResponseVO<AuthenticationVO> getAuthenticationInfo() {
        return accountHandler.getAuthenticationInfo();
    }

    @ApiOperation(value = "用户资料修改接口", tags = {"账户管理"})
    @PostMapping("/modifyAuthentication")
    public ResponseVO<AuthenticationVO> modifyAuthentication(@RequestBody AuthenticationVO authenticationVO) {
        return accountHandler.modifyAuthentication(authenticationVO);
    }

    @ApiOperation(value = "获取车辆认证信息", tags = {"账户管理"})
    @PostMapping("/getVehicleInfo")
    public ResponseVO<VehicleVO> getVehicleInfo() {
        return accountHandler.getVehicleInfo();
    }

    @ApiOperation(value = "车辆资料修改接口", tags = {"账户管理"})
    @PostMapping("/modifyVehicle")
    public ResponseVO<VehicleVO> modifyVehicle(@RequestBody VehicleVO vehicleVO) {
        return accountHandler.modifyVehicle(vehicleVO);
    }

    @ApiOperation(value = "验证Token", tags = {"账户管理"})
    @PostMapping("/verifyToken")
    public ResponseVO verifyToken(HttpServletRequest request) {
        AccountVO vo = (AccountVO) request.getSession().getAttribute("user");
        if (vo == null){
            throw new BusinessRuntimeException(BusinessErrors.DATA_NOT_EXIST);
        }
        return ResponseVO.success(CommonsUtils.toPO(vo));
    }

    @ApiOperation(value = "身份认证接口", tags = {"账户管理"})
    @PostMapping("/identityAuth")
    public ResponseVO<AuthenticationVO> identityAuth() {
        return accountHandler.identityAuth();
    }

    @ApiOperation(value = "车辆认证接口", tags = {"账户管理"})
    @PostMapping("/vehicleAuth")
    public ResponseVO<VehicleVO> vehicleAuth() {
        return accountHandler.vehicleAuth();
    }


}
