package com.xxx.suplier.service;

import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.SupProductBase;
import com.xxx.suplier.form.CategorySaveForm;
import com.xxx.suplier.form.SupProductBaseForm;
import com.xxx.user.security.CurrentUser;
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
public class ProductBaseService extends CommonService {

    /**
     * @Description:下拉列表
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @Cacheable(value = {"SupProductBaseForm"})
    public List<SupProductBase> findCategoryListCombox(Integer type) throws ResponseEntityException {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        cri = Restrictions.and(cri, Restrictions.eq("suplierId", CurrentUser.get().getSuplierId()));
        cri = Restrictions.and(cri, Restrictions.eq("type", type));
        List<SupProductBase> list = getCurrentSession().createCriteria(SupProductBase.class)
                .add(cri)
                .list();
        return list;
    }

    /**
     * @Description:查询列表
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @Cacheable(value = {"SupProductBaseForm"})
    public PageList<SupProductBase> findCategoryList(PageQuery pageQuery,Integer type,String name) throws ResponseEntityException {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(name) && name != null) {
            cri = Restrictions.and(cri, Restrictions.like("name", name, MatchMode.ANYWHERE));
        }
        cri = Restrictions.and(cri, Restrictions.eq("suplierId", CurrentUser.get().getSuplierId()));
        cri = Restrictions.and(cri, Restrictions.eq("type", type));
        pageQuery.hibernateCriterion = cri;
//        pageQuery.hibernateFetchFields = "plaProductCategory";
        pageQuery.order = "desc";
        pageQuery.sort = "updateDate";
        PageList<SupProductBase> list = hibernateReadonlyRepository.getList(SupProductBase.class, pageQuery);
        return list;
    }

    /**
     * @Description:新增和编辑
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @CacheEvict(value = "SupProductBaseForm",allEntries = true)
    public SupProductBase addProductBase(CategorySaveForm form) throws UpsertException, ResponseEntityException {
        SupProductBase supProductBase = null;
        if(form.id!=null){
            supProductBase = get2(SupProductBase.class,form.id);

        }else {
            supProductBase = new SupProductBase();
            supProductBase.setType(form.type);
            supProductBase.setSuplierId(CurrentUser.get().getSuplierId());
        }
        supProductBase.setName(form.name);
        supProductBase.setRemarks(form.remarks);
        supProductBase.setUpdateDate(new Date());
        return upsert2(supProductBase);
    }

    /**
     * @Description:批量新增
     * @Author: disvenk.dai
     * @Date: 2018/1/10
     */
    @CacheEvict(value = "SupProductBaseForm",allEntries = true)
    public SupProductBase addProductBaseMany(CategorySaveForm form) throws UpsertException, ResponseEntityException {
        List<SupProductBaseForm> list = form.list;
        for (SupProductBaseForm supProductBaseForm: list) {
            SupProductBase supProductBase = supProductBase = new SupProductBase();
            supProductBase.setType(supProductBaseForm.type);
            supProductBase.setSuplierId(CurrentUser.get().getSuplierId());
            supProductBase.setName(supProductBaseForm.name);
            supProductBase.setRemarks(supProductBaseForm.remarks);
            supProductBase.setUpdateDate(new Date());
            upsert2(supProductBase);
        }

        return null;
    }

    /**
     * @Description:查询唯一
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @Cacheable(value = "SupProductBaseForm")
    public Boolean checkUnique(CategorySaveForm form) throws ResponseEntityException {
        SupProductBase supProductBase = get2(SupProductBase.class,"name",form.name,"type",form.type,"suplierId",
                CurrentUser.get().getSuplierId());
        if(supProductBase !=null)
            return true;
        return false;
    }

    /**
     * @Description:编辑详情
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @Cacheable(value = "SupProductBaseForm")
    public SupProductBase checkBaseDetail(CategorySaveForm form) throws ResponseEntityException {
        SupProductBase supProductBase = get2(SupProductBase.class,"id",form.id,"suplierId",CurrentUser.get().getSuplierId());
        if(supProductBase==null)
            throw new ResponseEntityException(220,"该记录不存在");
        return supProductBase;
    }

    /**
     * @Description:删除分类
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @CacheEvict(value = "SupProductBaseForm",allEntries = true)
    public SupProductBase deleteBase(CategorySaveForm form) throws ResponseEntityException, UpsertException {
        SupProductBase supProductBase = get2(SupProductBase.class,form.id);
       if(supProductBase==null)
           throw new ResponseEntityException(220,"该记录不存在");
        supProductBase.setLogicDeleted(true);
        return upsert2(supProductBase);
    }
}
