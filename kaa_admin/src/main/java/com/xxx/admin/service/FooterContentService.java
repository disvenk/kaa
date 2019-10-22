package com.xxx.admin.service;

import com.xxx.admin.form.FooterContentQueryForm;
import com.xxx.admin.form.FooterContentSaveForm;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.SalesVedioInfo;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FooterContentService extends CommonService {

    /**
     * @Description: 底部内容查询
     * @Author: disvenk.dai
     * @Date: 2017/12/23
     */
    @Cacheable(value={"SalesVedioInfo"})
    public PageList<SalesVedioInfo> findFooterContetnList(PageQuery pageQuery, FooterContentQueryForm form){
        //
        Criterion cri = Restrictions.eq("logicDeleted",false);
        if (StringUtils.isNotBlank(form.name) && form.name != null)
            cri = Restrictions.and(cri, Restrictions.like("title", form.name, MatchMode.ANYWHERE));
        if (form.kind != null)
            cri = Restrictions.and(cri, Restrictions.eq("vedioType", form.kind));
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "updateDate";
        PageList<SalesVedioInfo> list = hibernateReadonlyRepository.getList(SalesVedioInfo.class,pageQuery);
        return list;
    }

    /**
     * @Description: 底部内容增加
     * @Author: disvenk.dai
     * @Date: 2017/12/24
     */
    @CacheEvict(value={"SalesVedioInfo"},allEntries = true)
    public SalesVedioInfo saveFooterContent(FooterContentSaveForm form) throws UpsertException {
        if(form.id==null) {
            SalesVedioInfo salesVedioInfo = new SalesVedioInfo();
            salesVedioInfo.setTitle(form.name);
            if (form.vedioUrl != null && StringUtils.isNotBlank(form.vedioUrl)) {
                salesVedioInfo.setVedioUrl(form.vedioUrl);
            }
            salesVedioInfo.setVedioType(form.kind);
            salesVedioInfo.setDescription(form.description);
            salesVedioInfo.setShow(false);
            salesVedioInfo.setViews(form.watch);
            salesVedioInfo.setPictureUrl(form.picture.size() == 0 ? "" : form.picture.get(0).href);
            salesVedioInfo.setShortDescription(form.shortDesc);
            salesVedioInfo.setUpdateDate(new Date());


            return upsert2(salesVedioInfo);
        }else{
            SalesVedioInfo salesVedioInfo = getSalesVedioInfo(form.id);
            salesVedioInfo.setTitle(form.name);
            if(form.vedioUrl!=null && StringUtils.isNotBlank(form.vedioUrl)){
                salesVedioInfo.setVedioUrl(form.vedioUrl);
            }
            salesVedioInfo.setVedioType(form.kind);
            salesVedioInfo.setViews(form.watch);
            salesVedioInfo.setDescription(form.description);
            salesVedioInfo.setPictureUrl(form.picture.size() == 0 ? "" : form.picture.get(0).href);
            salesVedioInfo.setShortDescription(form.shortDesc);
            salesVedioInfo.setUpdateDate(new Date());


            return upsert2(salesVedioInfo);
        }
    }

    /**
     * @Description: 底部内容查询
     * @Author: disvenk.dai
     * @Date: 2017/12/24
     */
    @Cacheable(value = {"SalesVedioInfo"})
    public SalesVedioInfo getSalesVedioInfo(Integer id) {
        return get2(SalesVedioInfo.class,"id",id);
    }

    /**
     * @Description: 修改内容展示状态
     * @Author: disvenk.dai
     * @Date: 2017/12/24
     */
    @CacheEvict(value = {"SalesVedioInfo"}, allEntries = true)
    public void updateIsShow(Integer id, Boolean isShow) throws ResponseEntityException, UpsertException{
        SalesVedioInfo SalesVedioInfo = get2(SalesVedioInfo.class, id);
        if (SalesVedioInfo == null)
            throw new ResponseEntityException(120, "内容不存在");
        SalesVedioInfo.setShow(isShow);
        upsert2(SalesVedioInfo);
    }

    /**
     * @Description: 删除内容
     * @Author: disvenk.dai
     * @Date: 2017/12/14
     */
    @CacheEvict(value = {"SalesVedioInfo"}, allEntries = true)
    public void removeCmsContent(Integer id) throws ResponseEntityException, UpsertException {
        SalesVedioInfo SalesVedioInfo = get2(SalesVedioInfo.class,id);
        if (SalesVedioInfo == null) {
            throw new ResponseEntityException(120, "内容不存在");
        }
        SalesVedioInfo.setLogicDeleted(true);
        upsert2(SalesVedioInfo);
    }
}
