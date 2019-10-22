package com.xxx.admin.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.dao.SuplierSalesOrderDao;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.MybatisPageQuery;
import com.xxx.core.query.PageList;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.*;
import com.xxx.user.Commo;
import com.xxx.utils.Arith;
import com.xxx.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

@Service
public class SuplierSalesOrderService extends CommonService {

    @Autowired
    private SuplierSalesOrderDao suplierSalesOrderDao;




    /**
     * @Description: 订单列表
     * @Author: Chen.zm
     * @Date: 2018/1/9 0009
     */
    public PageList<JSONObject> findList(MybatisPageQuery pageQuery) {
        return suplierSalesOrderDao.findList(pageQuery);
    }

    /**
     * @Description: 获取订单状态各数量
     * @Author: Chen.zm
     * @Date: 2018/1/9 0009
     */
    public JSONArray findOrderCount(Integer suplierId) {
        List<JSONObject> list = suplierSalesOrderDao.findOrderCount(suplierId);
        //将工单数量转为map形式
        Map<Integer, Integer> map = new HashMap<>();
        for (JSONObject json : list) {
            map.put(json.getInteger("producedStatus"), json.getInteger("orderCount"));
        }
        //生产状态 2：待生产  3：生产中   4：已发货  7：已完成
        JSONArray data = new JSONArray();
        for (int i=2; i<=7; i++) {
            JSONObject json = new JSONObject();
            json.put("type", i);
            json.put("name", Commo.parseSuplierOrderProducedStatusOffline(i));
            json.put("count", map.get(i) == null ? "0" : map.get(i).toString());
            data.add(json);
            if (i == 4) i = 6;//屏蔽5,6状态
        }
        return data;
    }

    /**
     * @Description: 订单详情
     * @Author: Chen.zm
     * @Date: 2018/1/9 0009
     */
    public SupSalesOrder getSupSalesOrderDetail(Integer id) {
        Criterion cri = Restrictions.eq("id", id);
        return (SupSalesOrder) getCurrentSession().createCriteria(SupSalesOrder.class)
                .add(cri)
                .setFetchMode("supSalesOrderDetail", FetchMode.JOIN)
                .setFetchMode("supSalesOrderDelivery", FetchMode.JOIN)
                .uniqueResult();
    }


}
