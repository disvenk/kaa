package com.xxx.admin.dao;


import com.alibaba.fastjson.JSONObject;
import com.xxx.core.dao.CommonDao;
import com.xxx.core.query.MybatisPageQuery;
import com.xxx.core.query.PageList;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SuplierSalesOrderDao extends CommonDao{


    public PageList<JSONObject> findList(MybatisPageQuery pageQuery) {
        List<JSONObject> list = mybatisReadonlyRepository.getCurrentSession()
                .selectList("mybatis.mappers.SuplierSalesOrderMapper.findList", pageQuery.getParams(), pageQuery);
        return new PageList(list, pageQuery.getTotal());
    }

    public List<JSONObject> findOrderCount(Integer suplierId){
        Map<String, Object> map = new HashMap<>();
        map.put("suplierId", suplierId);
        return mybatisRepository.getCurrentSession().selectList("mybatis.mappers.SuplierSalesOrderMapper.findOrderCount",map);
    }


}
