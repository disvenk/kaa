package com.xxx.sales.service;


import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.DesDesigner;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class DesignerService extends CommonService {

    /**
     * @Description: 获取设计师列表
     * @Author: Chen.zm
     * @Date: 2017/11/22 0022
     */
    @Cacheable(value = {"DesDesigner"})
    public PageList<DesDesigner> designerList(PageQuery pageQuery) {
//        Criterion cri = Restrictions.eq("status", 1);
//        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "createdDate";
        PageList<DesDesigner> list = hibernateReadonlyRepository.getList(DesDesigner.class, pageQuery);
        return list;
    }



}
