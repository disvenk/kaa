package com.xxx.user.dao;

import com.alibaba.fastjson.JSONObject;
import com.xxx.core.dao.CommonDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class AccountDao extends CommonDao {

    public JSONObject supplierProductInit(Integer suplierId){
        Map<String, Object> map = new HashMap<>();
        map.put("suplierId", suplierId);
        return mybatisRepository.getCurrentSession().selectOne("mybatis.mappers.AccountMapper.supplierProductInit",map);
    }



}
