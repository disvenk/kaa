package com.xxx.sales.dao;

import com.alibaba.fastjson.JSONObject;
import com.xxx.core.dao.CommonDao;
import com.xxx.core.query.MybatisPageQuery;
import com.xxx.core.query.PageList;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SalesProductDao extends CommonDao {

    public PageList<JSONObject> findSalesProductList(MybatisPageQuery pageQuery) {
        List<JSONObject> list = mybatisReadonlyRepository.getCurrentSession()
                .selectList("mybatis.mappers.SalesProductMapper.salesProductList", pageQuery.getParams(), pageQuery);
        return new PageList(list, pageQuery.getTotal());
    }

    public List<JSONObject> findProductLabel(Integer categoryId){
        Map<String, Object> map = new HashMap<>();
        map.put("categoryId", categoryId);
        return mybatisRepository.getCurrentSession().selectList("mybatis.mappers.SalesProductMapper.findProductLabel",map);
    }

    public JSONObject findOrderCount(Integer orderCount){
        Map<String, Object> map = new HashMap<>();
        map.put("orderCount", orderCount);
        return mybatisRepository.getCurrentSession().selectOne("mybatis.mappers.SalesProductMapper.findOrderCount",map);
    }


}
