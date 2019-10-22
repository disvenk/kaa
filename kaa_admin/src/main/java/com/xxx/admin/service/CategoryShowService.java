package com.xxx.admin.service;

import com.xxx.admin.form.CategoryIdsSaveForm;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.PlaProductCategory;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

@Service
public class CategoryShowService extends CommonService{

    /**
     * @Description:获取分类列表信息
     * @Author: hanchao
     * @Date: 2017/11/22 0022
     */
    @Cacheable(value = {"PlaProductCategory"})
    public PageList<PlaProductCategory> getPlaProductCategoryListAll(PageQuery pageQuery,Integer categoryId, Integer parentId) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (categoryId != null)
            cri = Restrictions.and(cri, Restrictions.eq("id", categoryId));
        if (parentId != null) {
            cri = Restrictions.and(cri, Restrictions.eq("parentId", parentId));
        } else {
            cri = Restrictions.and(cri, Restrictions.isNull("parentId"));
        }
        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "plaProductCategory";
        pageQuery.order = "asc";
        pageQuery.sort = "sort";
        PageList<PlaProductCategory> list = hibernateReadonlyRepository.getList(PlaProductCategory.class, pageQuery);
        return list;
    }

    /**
     * @Description:新增分类
     * @Author: hanchao
     * @Date: 2017/11/24 0024
     */
    @CacheEvict(value = {"PlaProductCategory"}, allEntries = true)
    public PlaProductCategory insertNewCategoryAddManage(CategoryIdsSaveForm form) throws UpsertException, ResponseEntityException, ParseException {
        PlaProductCategory pla = new PlaProductCategory();
        pla.setName(form.name);
        pla.setRemarks(form.remark);
        pla.setSupplierDay(form.suplierDay);
        pla.setUpdateDate(new Date(System.currentTimeMillis()));
        return upsert2(pla);
    }
    /**
     * @Description:商品添加下级分类
     * @Author: hanchao
     * @Date: 2017/11/23 0023
     */
    @CacheEvict(value = {"PlaProductCategory"}, allEntries = true)
    public PlaProductCategory insertCategoryManage(CategoryIdsSaveForm form) throws UpsertException, ResponseEntityException, ParseException {
        PlaProductCategory pla = get2(PlaProductCategory.class, "id", form.id);
        if (pla == null)
            throw new ResponseEntityException(120, "商品分类不存在");
        PlaProductCategory pla1 = new PlaProductCategory();
        pla1.setName(form.name);
        pla1.setRemarks(form.remark);
        pla1.setSupplierDay(form.suplierDay);
        pla1.setUpdateDate(new Date(System.currentTimeMillis()));
        pla1.setParentId(form.id);
        return upsert2(pla1);
    }

    /**
     * @Description:商品一级和二级分类编辑保存
     * @Author: hanchao
     * @Date: 2017/11/23 0023
     */
    @CacheEvict(value = {"PlaProductCategory"}, allEntries = true)
    public PlaProductCategory updateCategoryManageParentId(CategoryIdsSaveForm form) throws UpsertException, ResponseEntityException, ParseException {
        PlaProductCategory pla = getPlaProductCategory(form.id, form.parentId);
        pla.setName(form.name);
        pla.setSupplierDay(form.suplierDay);
        pla.setRemarks(form.remark);
        pla = upsert2(pla);
        return pla;
    }

    /**
     * @Description:根据id获取商品分类编辑详情
     * @Author: hanchao
     * @Date: 2017/11/23 0023
     */
    @Cacheable(value = {"PlaProductCategory"})
    public  PlaProductCategory getPlaProductCategory(Integer id,Integer parentId) throws ResponseEntityException {
        PlaProductCategory pla = get2(PlaProductCategory.class,"id", id != null ? id : parentId);
        if (pla == null) {
            throw new ResponseEntityException(120, "分类不存在");
        }
        return pla;
    }

    /**
     * @Description:商品分类根据id删除二级分类
     * @Author: hanchao
     * @Date: 2017/11/22 0022
     */
    @CacheEvict(value = {"PlaProductCategory"}, allEntries = true)
    public void updateCategoryId(Integer id) throws ResponseEntityException, UpsertException {
        PlaProductCategory pla = get2(PlaProductCategory.class,"id",id);
        if (pla == null) {
            throw new ResponseEntityException(120, "分类不存在");
        }
        if(pla.getParentId() == null ){
            throw new ResponseEntityException(130, "不能直接删除一级分类");
        }
        pla.setLogicDeleted(true);
        upsert2(pla);
    }

    /**
     * @Description:商品分类根据id删除一级分类
     * @Author: hanchao
     * @Date: 2017/11/22 0022
     */
    @CacheEvict(value = {"PlaProductCategory"}, allEntries = true)
    public void updateCategoryParentId(Integer id) throws ResponseEntityException, UpsertException {
        PlaProductCategory pla = (PlaProductCategory) getCurrentSession().createCriteria(PlaProductCategory.class)
                .add(Restrictions.and(Restrictions.eq("id", id)))
                .setFetchMode("children", FetchMode.JOIN)
                .uniqueResult();
        if (pla == null)
            throw new ResponseEntityException(120, "分类不存在");
        if (pla.getChildren() != null && pla.getChildren().size() != 0)
            throw new ResponseEntityException(130, "不能直接删除一级分类");
        pla.setLogicDeleted(true);
        upsert2(pla);
    }


}
