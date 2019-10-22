package com.xxx.store.dao;


import com.alibaba.fastjson.JSONObject;
import com.xxx.core.dao.CommonDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class StoreHomeDao extends CommonDao{

    public List<JSONObject> findProductCategoryList(Integer storeId){
        Map<String, Object> map = new HashMap<>();
        map.put("storeId", storeId);
        return mybatisRepository.getCurrentSession().selectList("mybatis.mappers.StoreHomeMapper.findProductCategoryList",map);
    }



}
