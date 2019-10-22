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
public class StoreSuplierOrderDao extends CommonDao {

    public PageList<JSONObject> findStoreSuplierOrderList(MybatisPageQuery pageQuery) {
        List<JSONObject> list = mybatisReadonlyRepository.getCurrentSession()
                .selectList("mybatis.mappers.StoreSuplierOrderMapper.findStoreSuplierOrderList", pageQuery.getParams(), pageQuery);
        return new PageList(list, pageQuery.getTotal());
    }

    public List<JSONObject> orderCount(Integer storeId){
        Map<String, Object> map = new HashMap<>();
        map.put("storeId", storeId);
        return mybatisRepository.getCurrentSession().selectList("mybatis.mappers.StoreSuplierOrderMapper.orderCount",map);
    }

}
