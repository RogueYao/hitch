package com.yian.storage.mapper;


import com.yian.modules.po.VehiclePO;

import java.util.List;

public interface VehicleMapper {
    int deleteByPrimaryKey(String id);

    int insert(VehiclePO record);

    int insertSelective(VehiclePO record);

    VehiclePO selectByPrimaryKey(String id);

    List<VehiclePO> selectList(VehiclePO record);

    int updateByPrimaryKeySelective(VehiclePO record);

    int updateByPrimaryKey(VehiclePO record);

    VehiclePO selectByPhone(String phone);
}