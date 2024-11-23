package com.heima.storage.service;

import com.heima.modules.po.PaymentPO;
import com.heima.storage.mapper.PaymentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Service
public class  PaymentAPIService {

    @Autowired
    private PaymentMapper paymentMapper;

    /**
     * 新增订单
     *
     * @param record
     * @return
     */
    public PaymentPO add(PaymentPO record) {
        PaymentPO existPO = selectByOrderId(record.getOrderId());
        if (null == existPO) {
            paymentMapper.insert(record);
        }
        return record;
    }

    public void update(PaymentPO record) {
        paymentMapper.updateByPrimaryKeySelective(record);
    }


    /**
     * 查询订单列表
     *
     * @param record
     * @return
     */
    public List<PaymentPO> selectlist(PaymentPO record) {
        return paymentMapper.selectList(record);
    }


    /**
     * 根据ID查看订单
     *
     * @param id
     * @return
     */
    public PaymentPO selectByID(String id) {
        return paymentMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据ID查看订单
     *
     * @param id
     * @return
     */
    public PaymentPO selectByOrderId(String id) {
        return paymentMapper.selectByOrderId(id);
    }


}
