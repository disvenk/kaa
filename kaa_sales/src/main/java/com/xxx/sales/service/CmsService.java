package com.xxx.sales.service;

import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.CmsContent;
import com.xxx.model.business.CmsMenu;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CmsService extends CommonService {
    /**
     * @Description: 查询公告列表
     * @Author: disvenk.dai
     * @Date: 2017/12/19
     */
    @Cacheable(value = {"CmsContent"})
    public PageList<CmsContent> findCmsContentList(PageQuery pageQuery) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        cri = Restrictions.and(cri, Restrictions.eq("isShow",true));
        pageQuery.hibernateCriterion = cri;
//        pageQuery.hibernateFetchFields = "plaProductCategory";
        pageQuery.order = "desc";
        pageQuery.sort = "updateDate";
        PageList<CmsContent> list = hibernateReadonlyRepository.getList(CmsContent.class, pageQuery);
        return list;
    }

    /**
     * @Description: 查询菜单列表
     * @Author: disvenk.dai
     * @Date: 2017/12/20
     */
    @Cacheable(value = {"CmsMenu"})
    public List<CmsMenu> findCmsMenuList() {
        Criterion cri = Restrictions.eq("logicDeleted", false);
       List<CmsMenu> list = hibernateReadonlyRepository.getAll(CmsMenu.class);
        return list;
    }

    /**
     * @Description: 根据id获取类型菜单
     * @Author: disvenk.dai
     * @Date: 2017/12/19
     */
    @Cacheable(value = {"CmsMenu"})
    public CmsMenu getCmsMenu(Integer id) {
        return get2(CmsMenu.class,"id",id);
    }

    /**
     * @Description: 根据id获取
     * @Author: disvenk.dai
     * @Date: 2017/12/19
     */
    @Cacheable(value = {"CmsContent"})
    public CmsContent getCmsContent(Integer id) {
        return get2(CmsContent.class,"id",id);
    }
}
