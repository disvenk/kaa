package com.xxx.admin.service;


import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SuplierProductService extends CommonService {


    /**
     * @Description: 获取供应商商品列表
     * @Author: Chen.zm
     * @Date: 2017/11/3 0003
     */
    public PageList<SupProduct> findSupProductList(PageQuery pageQuery, Integer suplierId, String pno, String name) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (suplierId != null)
            cri = Restrictions.and(cri, Restrictions.eq("suplierId", suplierId));
        if (StringUtils.isNotBlank(pno))
            cri = Restrictions.and(cri, Restrictions.like("pno", pno, MatchMode.ANYWHERE));
        if (StringUtils.isNotBlank(name))
            cri = Restrictions.and(cri, Restrictions.like("name", name, MatchMode.ANYWHERE));
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<SupProduct> list = hibernateReadonlyRepository.getList(SupProduct.class, pageQuery);
        for (SupProduct supProduct : list) {
            for (SupProductPrice price : supProduct.getSupProductPriceList()) {
            }
        }
        return list;
    }



}
