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
public class SuplierOrderDao extends CommonDao {

    public PageList<JSONObject> findSuplierOrderList(MybatisPageQuery pageQuery) {
        List<JSONObject> list = mybatisReadonlyRepository.getCurrentSession()
                .selectList("mybatis.mappers.SuplierOrderMapper.findSuplierOrderList", pageQuery.getParams(), pageQuery);
        return new PageList(list, pageQuery.getTotal());
    }

    public List<JSONObject> producedStatusCount(Integer orderType){
        Map<String, Object> map = new HashMap<>();
        map.put("orderType", orderType);
        return mybatisRepository.getCurrentSession().selectList("mybatis.mappers.SuplierOrderMapper.producedStatusCount",map);
    }

    public JSONObject findOrderCount(Integer orderCount){
        Map<String, Object> map = new HashMap<>();
        map.put("orderCount", orderCount);
        return mybatisRepository.getCurrentSession().selectOne("mybatis.mappers.SuplierOrderMapper.findOrderCount",map);
    }


}
