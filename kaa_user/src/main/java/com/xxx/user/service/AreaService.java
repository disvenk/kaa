package com.xxx.user.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.SysArea;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AreaService extends CommonService {

    /**
     * @Description: 获取所有省份
     * @Author: Chen.zm
     * @Date: 2017/10/30 0030
     */
    @Cacheable(value = {"SysArea"})
    public List<SysArea> findProvince() {
        Criterion cri = Restrictions.eq("type", 1);
        return getCurrentSession().createCriteria(SysArea.class)
                .add(cri)
                .addOrder(Order.asc("id"))
                .list();
    }

    /**
     * @Description: 获取所有省市
     * @Author: Chen.zm
     * @Date: 2017/10/30 0030
     */
    @Cacheable(value = {"SysArea"})
    public List<SysArea> findCity(String code) {
        Criterion cri = Restrictions.eq("parentCode", code);
        return getCurrentSession().createCriteria(SysArea.class)
                .add(cri)
                .addOrder(Order.asc("id"))
                .list();
    }


    /**
     * @Description: 获取省市区
     * @Author: Chen.zm
     * @Date: 2017/11/27 0027
     */
    @Cacheable(value = {"SysArea"})
    public JSONArray findProvinceCityZone() {
        List<SysArea> list = getCurrentSession().createCriteria(SysArea.class)
                .addOrder(Order.asc("id"))
                .list();
        Map<String, JSONObject> areaMap = new LinkedHashMap<>();
        Map<String, String> codeMap = new LinkedHashMap<>();
        for (SysArea area : list) {
            if (area.getType() == 1) {
                JSONObject json = new JSONObject();
                json.put("id", area.getId());
                json.put("code", area.getCode());
                json.put("name", area.getName());
                json.put("sub", new JSONArray());
                areaMap.put(area.getCode(), json);
                continue;
            }
            if (area.getType() == 2) {
                JSONObject json = new JSONObject();
                json.put("id", area.getId());
                json.put("code", area.getCode());
                json.put("name", area.getName());
                json.put("sub", new JSONArray());
                ((JSONArray) areaMap.get(area.getParentCode()).get("sub")).add(json);
                codeMap.put(area.getCode(), area.getParentCode());
                continue;
            }
            if (area.getType() == 3) {
                JSONObject json = new JSONObject();
                json.put("id", area.getId());
                json.put("code", area.getCode());
                json.put("name", area.getName());
                for (Object city : (JSONArray) areaMap.get(codeMap.get(area.getParentCode())).get("sub")) {
                    if (((JSONObject) city).get("code").equals(area.getParentCode())) {
                        ((JSONArray) ((JSONObject) city).get("sub")).add(json);
                    }
                }
                continue;
            }
        }
        //封装数据
        JSONArray data = new JSONArray();
        for (JSONObject json : areaMap.values()) {
            data.add(json);
        }
        return data;
    }

}
