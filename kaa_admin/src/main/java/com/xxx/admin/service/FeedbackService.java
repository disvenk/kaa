package com.xxx.admin.service;

import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.Feedback;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService extends CommonService {

    /**
     * @Description: 删除反馈意见
     * @Author: Steven.Xiao
     * @Date: 2017/11/29
     */
    @CacheEvict(value = {"Feedback"}, allEntries = true)
    public Feedback deleteFeedback(Integer id) throws ResponseEntityException, UpsertException{
        Feedback feedback=get2(Feedback.class,"id",id);
        feedback.setLogicDeleted(true);
        return upsert2(feedback);
    }

    /**
     * @Description: 查询意见反馈
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @Cacheable(value = {"findFeedbackList"})
    public PageList<Feedback> findFeedbackList(String name,String telephone,PageQuery pageQuery) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(name))
            cri = Restrictions.and(cri, Restrictions.like("name",name,MatchMode.ANYWHERE));
        if (StringUtils.isNotBlank(telephone))
            cri = Restrictions.and(cri, Restrictions.like("telephone",telephone,MatchMode.ANYWHERE));

        pageQuery.hibernateCriterion = cri;
        pageQuery.limit = 10;
        pageQuery.order = "desc";
        pageQuery.sort = "createdDate";
        PageList<Feedback> list = hibernateReadonlyRepository.getList(Feedback.class, pageQuery);
        return list;
    }

}
