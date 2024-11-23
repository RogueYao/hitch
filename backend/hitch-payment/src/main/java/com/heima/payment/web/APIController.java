package com.heima.payment.web;

import com.heima.commons.domin.vo.response.ResponseVO;
import com.heima.commons.groups.Group;
import com.heima.commons.initial.annotation.RequestInitial;
import com.heima.commons.utils.RequestUtils;
import com.heima.modules.po.AccountPO;
import com.heima.modules.po.OrderPO;
import com.heima.modules.vo.OrderVO;
import com.heima.modules.vo.PaymentVO;
import com.heima.payment.handler.PaymentHandler;
import com.heima.payment.service.AccountAPIService;
import com.heima.payment.service.OrderAPIService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/")
@Api(value = "支付操作Controller", tags = {"支付管理"})
@ApiResponses(@ApiResponse(code = 200, message = "处理成功"))
public class  APIController {
    @Autowired
    private PaymentHandler paymentHandler;
    @Autowired
    private OrderAPIService orderAPIService;
    @Autowired
    private AccountAPIService accountAPIService;


    /**
     * 面对面支付
     *
     * @return
     */
    @ApiOperation(value = "调取支付信息", tags = {"支付管理"})
    @PostMapping("/payment")
    @RequestInitial(groups = Group.Create.class)
    public ResponseVO<PaymentVO> payment(@RequestBody PaymentVO paymentVO) throws Exception {
        OrderPO orderPO = orderAPIService.selectByID(paymentVO.getOrderId());
        AccountPO accountPO = accountAPIService.getAccountByID(orderPO.getDriverId());
        paymentVO.setCodeUrl(accountPO.getPaycode());
        return ResponseVO.success(paymentVO);
    }

    @ApiOperation(value = "确认到款", tags = {"支付管理"})
    @PostMapping("/confirmPay/{orderId}")
    @RequestInitial(groups = Group.Create.class)
    public ResponseVO<PaymentVO> confirmPay(@PathVariable String orderId) throws Exception {
        OrderPO orderPO = orderAPIService.selectByID(orderId);
        if (orderPO == null){
            return ResponseVO.error("订单不存在！");
        }
        String userId = RequestUtils.getCurrentUserId();
        //数据安全：校验是不是改的自己的订单，防止篡改数据
        if (userId!=null && !userId.equals(orderPO.getDriverId())){
            return ResponseVO.error("该订单不属于你！");
        }
        orderPO.setStatus(2);
        orderAPIService.update(orderPO);
        return ResponseVO.success("订单确认成功！");
    }



    @ApiOperation(value = "支付查询接口API", tags = {"支付管理"})
    @PostMapping("/query")
    @RequestInitial(groups = Group.Select.class)
    public ResponseVO<OrderVO> orderQuery(@RequestBody PaymentVO paymentVO) throws Exception {
        OrderPO orderPO = orderAPIService.selectByID(paymentVO.getOrderId());
        return ResponseVO.success(orderPO);
    }


}
