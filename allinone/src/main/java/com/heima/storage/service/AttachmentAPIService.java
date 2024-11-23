package com.heima.storage.service;

import com.heima.modules.po.AttachmentPO;
import com.heima.storage.mapper.AttachmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Service
public class AttachmentAPIService {

    @Autowired
    private AttachmentMapper attachmentMapper;

    /**
     * 账户注册
     *
     * @param record
     * @return
     */
    public AttachmentPO register(AttachmentPO record) {
        attachmentMapper.insert(record);
        return record;
    }

    /**
     * 列表查询
     *
     * @param record
     * @return
     */
    List<AttachmentPO> selectList(AttachmentPO record) {
        return attachmentMapper.selectList(record);
    }

    /**
     * 账户修改
     *
     * @param record
     */

    public void update(AttachmentPO record) {
        attachmentMapper.updateByPrimaryKeySelective(record);
    }


    /**
     * 根据ID获取用户信息
     *
     * @param id
     * @return
     */
    public AttachmentPO selectByID(String id) {
        return attachmentMapper.selectByPrimaryKey(id);
    }
}
