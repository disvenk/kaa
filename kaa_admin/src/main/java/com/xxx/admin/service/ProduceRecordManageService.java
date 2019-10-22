package com.xxx.admin.service;


import com.alibaba.fastjson.JSONArray;
import com.xxx.core.query.ExtFilter;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.SupOrder;
import com.xxx.model.business.SupOrderProductionLog;
import com.xxx.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

@Service
public class ProduceRecordManageService extends CommonService {

    /**
     * @Description:获取工厂工人维护列表
     * @Author: hanchao
     * @Date: 2017/12/4 0004
     */
    @Cacheable(value = {"SupOrder,SupOrderProductionLog,SupOrderDetail"})
    public PageList<SupOrder> findProduceRecordList(PageQuery pageQuery, String orderNo, String supplierProductCode, String procedureType, Integer producedStatus,String productCode,Integer timeStatus,String supplierName) throws ParseException {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if(StringUtils.isNotBlank(orderNo))
            cri = Restrictions.and(cri, Restrictions.like("orderNo", orderNo, MatchMode.ANYWHERE));
        JSONArray jsonArray = new JSONArray();
        if (StringUtils.isNotBlank(supplierProductCode)) {
            ExtFilter filter = new ExtFilter("supOrderDetail.plaProduct.pno", "string", supplierProductCode, ExtFilter.ExtFilterComparison.like, null);
            jsonArray.add(filter);
        }
        if (StringUtils.isNotBlank(productCode)) {
            ExtFilter filter = new ExtFilter("supOrderDetail.plaProduct.productCode", "string", productCode, ExtFilter.ExtFilterComparison.like, null);
            jsonArray.add(filter);
        }
        if(timeStatus != null){
            Date date = new Date((new Date().getTime() - 3600 * 24 * 1000));
            ExtFilter filter = new ExtFilter("supOrderProductionLog.productionDate", "date", DateTimeUtils.parseStr(date), ExtFilter.ExtFilterComparison.le, null);
            jsonArray.add(filter);
        }
        if (StringUtils.isNotBlank(procedureType)) {
            ExtFilter filter = new ExtFilter("supOrderProductionLog.procedureType", "string", procedureType, ExtFilter.ExtFilterComparison.eq, null);
            jsonArray.add(filter);
        }
        if (StringUtils.isNotBlank(supplierName)) {
            ExtFilter filter = new ExtFilter("supSuplier.name", "string", supplierName, ExtFilter.ExtFilterComparison.eq, null);
            jsonArray.add(filter);
        }
        if(producedStatus != null)
            cri = Restrictions.and(cri, Restrictions.eq("producedStatus", producedStatus));
        pageQuery.filter = jsonArray.toJSONString();
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<SupOrder> list = hibernateReadonlyRepository.getList(SupOrder.class, pageQuery);
        for (SupOrder supOrder : list) {
            for (SupOrderProductionLog supOrderProductionLog : supOrder.getSupOrderProductionLogList());
        }
        return list;
    }


}
