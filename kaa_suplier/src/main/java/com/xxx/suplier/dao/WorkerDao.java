package com.xxx.suplier.dao;


import com.alibaba.fastjson.JSONObject;
import com.xxx.core.dao.CommonDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class WorkerDao extends CommonDao{

    public JSONObject findWorkerOrder(Integer workerId, String dateStr, String dateEnd){
        Map<String, Object> map = new HashMap<>();
        map.put("workerId", workerId);
        map.put("dateStr", dateStr);
        map.put("dateEnd", dateEnd);
        return mybatisRepository.getCurrentSession().selectOne("mybatis.mappers.WorkerMapper.findWorkerOrder",map);
    }


    public List<JSONObject> findWorkerMonthOrder(Integer workerId, String dateStr, String dateEnd){
        Map<String, Object> map = new HashMap<>();
        map.put("workerId", workerId);
        map.put("dateStr", dateStr);
        map.put("dateEnd", dateEnd);
        return mybatisRepository.getCurrentSession().selectList("mybatis.mappers.WorkerMapper.findWorkerMonthOrder",map);
    }

    public List<JSONObject> findWorkerSupOrder(Integer workerId, String dateStr, String dateEnd){
        Map<String, Object> map = new HashMap<>();
        map.put("workerId", workerId);
        map.put("dateStr", dateStr);
        map.put("dateEnd", dateEnd);
        return mybatisRepository.getCurrentSession().selectList("mybatis.mappers.WorkerMapper.findWorkerSupOrder",map);
    }
}
