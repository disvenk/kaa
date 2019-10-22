package com.xxx.suplier.dao;


import com.alibaba.fastjson.JSONObject;
import com.xxx.core.dao.CommonDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SuplierDao extends CommonDao{

    public JSONObject findHomeInfo(Integer suplierId){
        Map<String, Object> map = new HashMap<>();
        map.put("suplierId", suplierId);
        return mybatisRepository.getCurrentSession().selectOne("mybatis.mappers.SuplierMapper.findHomeInfo",map);
    }

    public JSONObject orderCount(Integer suplierId, String startDate, String endDate){
        Map<String, Object> map = new HashMap<>();
        map.put("suplierId", suplierId);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        return mybatisRepository.getCurrentSession().selectOne("mybatis.mappers.SuplierMapper.orderCount",map);
    }

    public List<JSONObject> producedStatusCount(Integer suplierId, Integer orderType){
        Map<String, Object> map = new HashMap<>();
        map.put("suplierId", suplierId);
        map.put("orderType", orderType);
        return mybatisRepository.getCurrentSession().selectList("mybatis.mappers.SuplierMapper.producedStatusCount",map);
    }

    public JSONObject supplierHomeInfo(Integer suplierId){
        Map<String, Object> map = new HashMap<>();
        map.put("suplierId", suplierId);
        return mybatisRepository.getCurrentSession().selectOne("mybatis.mappers.SuplierMapper.supplierHomeInfo",map);
    }

    public List<JSONObject> supOrderDeliveryList(Integer suplierId, String deliveryDateStr, String deliveryDateEnd){
        Map<String, Object> map = new HashMap<>();
        map.put("suplierId", suplierId);
        map.put("deliveryDateStr", deliveryDateStr);
        map.put("deliveryDateEnd", deliveryDateEnd);
        return mybatisRepository.getCurrentSession().selectList("mybatis.mappers.SuplierMapper.supOrderDeliveryList",map);
    }

    public List<JSONObject> supOrderCustomerList(Integer suplierId, String dateStr, String dateEnd){
        Map<String, Object> map = new HashMap<>();
        map.put("suplierId", suplierId);
        map.put("dateStr", dateStr);
        map.put("dateEnd", dateEnd);
        return mybatisRepository.getCurrentSession().selectList("mybatis.mappers.SuplierMapper.supOrderCustomerList",map);
    }

    public List<JSONObject> supOrderWorkerList(Integer suplierId, String dateStr, String dateEnd){
        Map<String, Object> map = new HashMap<>();
        map.put("suplierId", suplierId);
        map.put("dateStr", dateStr);
        map.put("dateEnd", dateEnd);
        return mybatisRepository.getCurrentSession().selectList("mybatis.mappers.SuplierMapper.supOrderWorkerList",map);
    }

}
