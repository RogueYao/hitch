package com.yian.storage.mapper;

import com.yian.modules.po.StrokePO;

import java.util.List;


public interface StrokeMapper {
    int deleteByPrimaryKey(String id);

    int insert(StrokePO record);

    int insertSelective(StrokePO record);

    StrokePO selectByPrimaryKey(String id);

    List<StrokePO> selectList(StrokePO record);

    int updateByPrimaryKeySelective(StrokePO record);

    int updateByPrimaryKey(StrokePO record);
    StrokePO selectByPublishId(String id);
}