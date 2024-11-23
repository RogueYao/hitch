package com.yian.storage.mapper;


import com.yian.modules.po.AttachmentPO;

import java.util.List;

public interface AttachmentMapper {
    int deleteByPrimaryKey(String id);

    int insert(AttachmentPO record);

    int insertSelective(AttachmentPO record);

    AttachmentPO selectByPrimaryKey(String id);

    AttachmentPO selectByMd5(String md5);

    List<AttachmentPO> selectList(AttachmentPO record);

    int updateByPrimaryKeySelective(AttachmentPO record);

    int updateByPrimaryKey(AttachmentPO record);

}