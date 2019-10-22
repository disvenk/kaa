package com.xxx.sales.service;


import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.SalesTeacher;
import com.xxx.model.business.SalesVedioInfo;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalesVedioInfoService extends CommonService {

    /**
     * @Description: 销售平台首页，显示视频名称和URL
     * @Author: Steven.Xiao
     * @Date: 2017/12/23
     */
    @CacheEvict(value = {"SalesVedioInfo"}, allEntries = true)
    public PageList<SalesVedioInfo> findHomePageVedioInfoList(PageQuery pageQuery, Integer vedioType) throws UpsertException, ResponseEntityException {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        cri = Restrictions.and(cri, Restrictions.eq("vedioType", vedioType));
        cri = Restrictions.and(cri, Restrictions.eq("isShow", true));
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "updateDate";
        PageList<SalesVedioInfo> list = hibernateReadonlyRepository.getList(SalesVedioInfo.class, pageQuery);
        return list;
    }

    /**
     * @Description: 显示该类型下面的视频列表
     * @Author: Steven.Xiao
     * @Date: 2017/12/23
     */
    @CacheEvict(value = {"SalesVedioInfo"}, allEntries = true)
    public PageList<SalesVedioInfo> findVedioInfoList(PageQuery pageQuery, Integer vedioType) throws UpsertException, ResponseEntityException {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        cri = Restrictions.and(cri, Restrictions.eq("vedioType", vedioType));
        cri = Restrictions.and(cri, Restrictions.eq("isShow", true));
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "updateDate";
        PageList<SalesVedioInfo> list = hibernateReadonlyRepository.getList(SalesVedioInfo.class, pageQuery);
        return list;
    }

    /**
     * @Description: 取得某视频下面，同类相关视频
     * @Author: Steven.Xiao
     * @Date: 2017/12/23
     */
    @CacheEvict(value = {"SalesVedioInfo"}, allEntries = true)
    public PageList<SalesVedioInfo> findRelatedVedioInfoList(PageQuery pageQuery, Integer id) throws UpsertException, ResponseEntityException {
        //取得该视频的相关类别
        SalesVedioInfo salesVedioInfo = get2(SalesVedioInfo.class, "id", id);
        if (salesVedioInfo == null)
            throw new ResponseEntityException(210, "视频不存在");
        //取得相关视频列表
        Criterion cri = Restrictions.eq("logicDeleted", false);
        cri = Restrictions.and(cri, Restrictions.eq("vedioType", salesVedioInfo.getVedioType()));
        cri = Restrictions.and(cri, Restrictions.ne("id", id));
        cri = Restrictions.and(cri, Restrictions.eq("isShow", true));
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "updateDate";
        PageList<SalesVedioInfo> list = hibernateReadonlyRepository.getList(SalesVedioInfo.class, pageQuery);
        return list;
    }

    /**
     * @Description: 取得视频的详情
     * @Author: Steven.Xiao
     * @Date: 2017/12/23
     */
    @Cacheable(value = {"SalesVedioInfo"})
    public SalesVedioInfo getSalesVedioInfo(Integer id) {
        return get2(SalesVedioInfo.class, "id", id);
    }

    /**
     * @Description: 每浏览一次详情，浏览次数加1
     * @Author: Steven.Xiao
     * @Date: 2017/12/23
     */
    @Cacheable(value = {"SalesVedioInfo"})
    public void updateSalesVedioViews(Integer id) throws UpsertException, ResponseEntityException {
        SalesVedioInfo salesVedioInfo = get2(SalesVedioInfo.class, "id", id);
        if (salesVedioInfo == null)
            throw new ResponseEntityException(220, "视频不存在");
        salesVedioInfo.setViews(salesVedioInfo.getViews() == null ? 0 : salesVedioInfo.getViews() + 1);
        upsert2(salesVedioInfo);
    }

    /**
     * @Description: 体验师信息收集
     * @Author: Steven.Xiao
     * @Date: 2017/12/23
     */
    @Cacheable(value = {"SalesTeacher"})
    public SalesTeacher AddSalesTeacher(String name,String mobile,String age) throws UpsertException, ResponseEntityException {
        SalesTeacher salesTeacher = new SalesTeacher();
        salesTeacher.setName(name);
        salesTeacher.setMobile(mobile);
        salesTeacher.setAge(age);
        return upsert2(salesTeacher);
    }

}
