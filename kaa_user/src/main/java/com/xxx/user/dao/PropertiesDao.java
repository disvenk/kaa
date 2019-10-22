package com.xxx.user.dao;

import com.alibaba.fastjson.JSONObject;
import com.xxx.core.dao.CommonDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;


@Repository("PropertiesDao")
public class PropertiesDao extends CommonDao {

    /**
     * 获取工单号：
     *      供应商编码 + 流水号
     */
    public String generateSupOrderNumber(String supplierCode) {
        Map map = new HashMap<String, Long>();
        map.put("result", null);
        mybatisRepository.getCurrentSession().selectOne("mybatis.mappers.PropertiesMapper.generateSupOrderNumber", map);
        return  supplierCode + map.get("result");
    }

    /**
     * 获取盒子购买电订单号
     */
    public String generateBoxPayOrderNumber() {
        Map map = new HashMap<String, Long>();
        map.put("result", null);
        mybatisRepository.getCurrentSession().selectOne("mybatis.mappers.PropertiesMapper.generateBoxPayOrderNumber", map);
        return  "" + map.get("result");
    }

    /**
     * 获取盒子使用记录号
     */
    public String generateBoxUseLogNumber() {
        Map map = new HashMap<String, Long>();
        map.put("result", null);
        mybatisRepository.getCurrentSession().selectOne("mybatis.mappers.PropertiesMapper.generateBoxUseLogNumber", map);
        return  "" + map.get("result");
    }

}
