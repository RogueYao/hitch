package com.yian.order.web;


import com.yian.commons.domin.vo.response.ResponseVO;
import com.yian.commons.groups.Group;
import com.yian.commons.initial.annotation.RequestInitial;
import com.yian.modules.vo.OrderVO;
import com.yian.order.handler.OrderHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
@Api(value = "订单操作Controller", tags = {"订单管理"})
@ApiResponses(@ApiResponse(code = 200, message = "处理成功"))
public class APIController {
    @Autowired
    private OrderHandler orderHandler;


    @ApiOperation(value = "订单列表", tags = {"订单管理"})
    @PostMapping("/list")
    @RequestInitial(groups = {Group.Select.class})
    public ResponseVO<OrderVO> list(@RequestBody OrderVO orderVO) {
        return orderHandler.list(orderVO);
    }

    @ApiOperation(value = "生成订单影子", tags = {"订单管理"})
    @PostMapping("/generateShadow")
    public ResponseVO<OrderVO> generateShadow() {
        return orderHandler.generateShadow();
    }


    @ApiOperation(value = "已支付订单列表", tags = {"订单管理"})
    @PostMapping("/paidList")
    public ResponseVO<OrderVO> paidList() {
        return orderHandler.paidList();
    }


    @ApiOperation(value = "同行乘客列表", tags = {"订单管理"})
    @PostMapping("/fellows")
    @RequestInitial(groups = {Group.Select.class})
    public ResponseVO<OrderVO> fellows(@RequestBody OrderVO orderVO) {
        return orderHandler.fellows(orderVO);
    }

    @ApiOperation(value = "根据乘客查看订单信息", tags = {"订单管理"})
    @PostMapping("/view/order/{tripid}")
    public ResponseVO<OrderVO> viewOrder(@PathVariable("tripid") String tripid) {
        return orderHandler.viewOrder(tripid);
    }


}
