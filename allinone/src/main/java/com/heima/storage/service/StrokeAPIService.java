package com.heima.storage.service;

import com.heima.modules.po.StrokePO;
import com.heima.storage.mapper.StrokeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Service
public class StrokeAPIService {

    @Autowired
    private StrokeMapper strokeMapper;

    /**
     * 发布行程
     *
     * @param strokePO
     * @return
     */
    public StrokePO publish(StrokePO strokePO) {
        strokeMapper.insert(strokePO);
        return strokePO;
    }

    @CacheEvict(cacheNames = "com.heima.modules.po.StrokePO", key = "#strokePO.id")
    public void update(StrokePO strokePO) {
        strokeMapper.updateByPrimaryKeySelective(strokePO);
    }


    /**
     * 查询行程列表
     *
     * @param record
     * @return
     */
    public List<StrokePO> selectlist(StrokePO record) {
        return strokeMapper.selectList(record);
    }




    /**
     * 根据ID查看行程细节
     *
     * @param id
     * @return
     */

    public StrokePO selectByID(String id) {
        return strokeMapper.selectByPrimaryKey(id);
    }
}
