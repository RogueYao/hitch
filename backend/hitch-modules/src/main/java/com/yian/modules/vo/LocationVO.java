package com.yian.modules.vo;

import com.yian.commons.domin.vo.VO;
import com.yian.modules.po.LocationPO;

import java.util.Date;

/**
 * 位置PO
 */
public class LocationVO implements VO {
    /**
     * 行程ID
     */
    private String trapId;
    /**
     * 经度
     */
    private String lng;
    /**
     * 维度
     */
    private String lat;

    /**
     * 时间
     */
    private Date time;


    public String getTrapId() {
        return trapId;
    }

    public void setTrapId(String trapId) {
        this.trapId = trapId;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public Class getPO() {
        return LocationPO.class;
    }
}
