package com.heima.storage.service;

import com.heima.modules.po.OrderPO;
import com.heima.storage.mapper.OrderMapper;
import com.heima.storage.mapper.OrderShadowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Service
public class OrderAPIService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderShadowMapper orderShadowMapper;

    /**
     * 新增订单
     *
     * @param record
     * @return
     */
    public OrderPO add(OrderPO record) {
        orderMapper.insert(record);
        return record;
    }


    public OrderPO addShadow(OrderPO record) {
        orderShadowMapper.insert(record);
        return record;
    }

    public void update(OrderPO record) {
        orderMapper.updateByPrimaryKeySelective(record);
    }


    /**
     * 查询订单列表
     *
     * @param record
     * @return
     */
    public List<OrderPO> selectlist(OrderPO record) {
        return orderMapper.selectList(record);
    }

    /**
     * 查询订单列表
     *
     * @param record
     * @return
     */
    public List<OrderPO> selectAvailableList(OrderPO record) {
        return orderMapper.selectAvailableList(record);
    }

    /**
     * 根据ID查看订单
     *
     * @param id
     * @return
     */
    public OrderPO selectByID(String id) {
        return orderMapper.selectByPrimaryKey(id);
    }


    public List<OrderPO> selectPaidList(OrderPO orderPO) {
        return orderMapper.selectPaidList(orderPO);
    }

    /**
     * 根据行程ID查询订单
     *
     * @param tripid
     * @return
     */
    public OrderPO selectByTripid(String tripid) {
        OrderPO orderPO = new OrderPO();
        orderPO.setPassengerStrokeId(tripid);
        List<OrderPO> orderPOList = selectlist(orderPO);
        if (null != orderPOList && !orderPOList.isEmpty()) {
            return orderPOList.get(0);
        }
        return null;
    }

}
