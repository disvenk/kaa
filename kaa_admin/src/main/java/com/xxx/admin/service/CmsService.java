package com.xxx.admin.service;

import com.xxx.admin.form.CmsContentAddForm;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.CmsContent;
import com.xxx.model.business.CmsMenu;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CmsService extends CommonService {
    /**
     * @Description: 查询公告列表
     * @Author: disvenk.dai
     * @Date: 2017/12/19
     */
    @Cacheable(value = {"CmsContent"})
    public PageList<CmsContent> findCmsContentList(PageQuery pageQuery, String name) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(name) && name != null)
            cri = Restrictions.and(cri, Restrictions.like("title", name, MatchMode.ANYWHERE));
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

    /**
     * @Description: 新增公告
     * @Author: disvenk.dai
     * @Date: 2017/12/19
     */
    @CacheEvict(value = {"CmsContent"}, allEntries = true)
    public CmsContent saveCmsContent(CmsContentAddForm form) throws UpsertException {
        CmsContent cmsContent = new CmsContent();
        cmsContent.setTitle(form.title);
        cmsContent.setMenuId(form.menuId.toString());
        cmsContent.setContentLabel(form.contentLabel);
        cmsContent.setContent(form.content);
        cmsContent.setShow(false);
        List<CmsContent> list = hibernateReadonlyRepository.getAll(cmsContent.getClass());
        cmsContent.setUpdateDate(new Date());
        return upsert2(cmsContent);
    }

    /**
     * @Description:更改公告
     * @Author: disvenk.dai
     * @Date: 2017/12/19
     */
    @CacheEvict(value = {"CmsContent"}, allEntries = true)
    public CmsContent updateCmsContent(CmsContentAddForm form) throws UpsertException,ResponseEntityException{
        CmsContent cmsContent = getCmsContent(form.id);
        if (cmsContent == null)
            throw new ResponseEntityException(120, "公告不存在");
        cmsContent.setTitle(form.title);
        cmsContent.setMenuId(form.menuId);
        cmsContent.setContentLabel(form.contentLabel);
        cmsContent.setContent(form.content);
       // cmsContent.setUpdateDate(new Date());
        return upsert2(cmsContent);
    }

    /**
     * @Description: 删除公告
     * @Author: disvenk.dai
     * @Date: 2017/12/19
     */
    @CacheEvict(value = {"CmsContent"}, allEntries = true)
    public void removeCmsContent(Integer id) throws ResponseEntityException, UpsertException {
       CmsContent cmsContent = getCmsContent(id);
        if (cmsContent == null) {
            throw new ResponseEntityException(120, "公告不存在");
        }
        cmsContent.setLogicDeleted(true);
        upsert2(cmsContent);
    }

    /**
     * @Description: 修改公告展示状态
     * @Author: disvenk.dai
     * @Date: 2017/19/20
     */
    @CacheEvict(value = {"CmsContent"}, allEntries = true)
    public void updateIsShow(Integer id, Boolean isShow) throws ResponseEntityException, UpsertException{
        CmsContent cmsContent = get2(CmsContent.class, id);
        if (cmsContent == null)
            throw new ResponseEntityException(120, "公告不存在");
        cmsContent.setShow(isShow);
        upsert2(cmsContent);
    }
}
