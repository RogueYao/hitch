package com.heima.storage.service;

import com.heima.modules.po.VehiclePO;
import com.heima.storage.mapper.VehicleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Service
public class VehicleAPIService {

    @Autowired
    private VehicleMapper vehicleMapper;

    /**
     * 新增订单
     *
     * @param record
     * @return
     */
    public VehiclePO add(VehiclePO record) {
        vehicleMapper.insert(record);
        return record;
    }

    public void update(VehiclePO record) {
        vehicleMapper.updateByPrimaryKeySelective(record);
    }


    /**
     * 查询订单列表
     *
     * @param record
     * @return
     */
    public List<VehiclePO> selectlist(VehiclePO record) {
        return vehicleMapper.selectList(record);
    }


    /**
     * 根据ID查看订单
     *
     * @param id
     * @return
     */
    public VehiclePO select(String id) {
        return vehicleMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据手机号码查询用户信息
     *
     * @param phone
     * @return
     */
    public VehiclePO selectByPhone(String phone){
        return vehicleMapper.selectByPhone(phone);
    }
}
