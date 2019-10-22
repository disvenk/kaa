package com.xxx.admin.service;

import com.alibaba.fastjson.JSONArray;
import com.xxx.admin.form.HeadCategoryAddForm;
import com.xxx.admin.form.HeadCategoryEditForm;
import com.xxx.admin.form.IdForm;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.ExtFilter;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.PlaProductCategory;
import com.xxx.model.business.SalesHeadCategory;
import com.xxx.model.business.SalesHeadCategoryProduct;
import com.xxx.model.business.SalesProduct;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SalesHeadCategoryService extends CommonService {

    /**
     * @Description: 首页运营的商品板块新增
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @Cacheable(value = {"SalesHeadCategory"})
    public SalesHeadCategory addSalesHeadCategory(HeadCategoryAddForm form) throws UpsertException, ResponseEntityException {
        SalesHeadCategory category = new SalesHeadCategory();
        category.setName(form.name);
        category.setDescription(form.description);
        category.setSort(form.sort);
        category.setUpdateDate(new Date());
        category.setStatus(1);
        return upsert2(category);
    }

    /**
     * @Description: 首页运营商品板块位查询列表
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @Cacheable(value = {"SalesHeadCategory"})
    public PageList<SalesHeadCategory> findSalesHeadCategoryList(PageQuery pageQuery, String name) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(name)) {
            cri = Restrictions.and(cri, Restrictions.like("name", name,MatchMode.ANYWHERE));
        }
        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "SalesHeadCategory";
        pageQuery.limit = 10;
        pageQuery.order = "asc";
        pageQuery.sort = "sort";
        PageList<SalesHeadCategory> list = hibernateReadonlyRepository.getList(SalesHeadCategory.class, pageQuery);
        return list;
    }

    /**
     * @Description: 取得运营位详情
     * @Author: Steven.Xiao
     * @Date: 2017/11/21
     */
    @Cacheable(value = {"SalesHeadCategory"})
    public SalesHeadCategory getSalesHeadCategory(Integer id) throws UpsertException, ResponseEntityException {
        SalesHeadCategory category = (SalesHeadCategory) getCurrentSession().createCriteria(SalesHeadCategory.class)
                .add(Restrictions.eq("id", id))
                .setFetchMode("salesHeadCategoryProductList", FetchMode.JOIN)
                .uniqueResult();
        return category;
    }

    /**
     * @Description: 删除运营位的商品板块
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @Cacheable(value = {"SalesHeadCategory"})
    public void deleteSalesHeadCategory(Integer id) throws UpsertException, ResponseEntityException {
        SalesHeadCategory category = getSalesHeadCategory(id);
//        delete(category.getSalesHeadCategoryProductList());
        delete(category);

        //同时，删除运营的商品
        String hql = "delete SalesHeadCategoryProduct where headcategoryId =:id";
        getCurrentSession().createQuery(hql).setInteger("id", id).executeUpdate();

    }

    /**
     * @Description: 控制运营位板块  显示/隐藏
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @Cacheable(value = {"SalesHeadCategory"})
    public SalesHeadCategory setDisabledSalesHeadCategory(Integer id, Integer status) throws UpsertException, ResponseEntityException {
        SalesHeadCategory category = get2(SalesHeadCategory.class, "id", id);
        category.setStatus(status);
        return upsert2(category);
    }

    /**
     * @Description: 运营的商品板块 编辑保存
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @Cacheable(value = {"SalesHeadCategory"})
    public SalesHeadCategory saveEditSalesHeadCategory(HeadCategoryEditForm form) throws UpsertException, ResponseEntityException {
        SalesHeadCategory category = get2(SalesHeadCategory.class, "id", form.id);
        category.setName(form.name);
        category.setDescription(form.description);
        category.setSort(form.sort);
        return upsert2(category);
    }

    /**
     * @Description: 查询 运营位的商品列表
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @Cacheable(value = {"SalesHeadCategoryProduct"})
    public PageList<SalesHeadCategoryProduct> findSalesHeadCategoryProductList(PageQuery pageQuery, Integer categoryId) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (categoryId != null)
            cri = Restrictions.and(cri, Restrictions.eq("headcategoryId", categoryId));
        pageQuery.hibernateCriterion = cri;
//        pageQuery.hibernateFetchFields = "SalesHeadCategoryProduct";
        pageQuery.limit = 10;
        pageQuery.order = "desc";
        pageQuery.sort = "sort";
        PageList<SalesHeadCategoryProduct> list = hibernateReadonlyRepository.getList(SalesHeadCategoryProduct.class, pageQuery);
        return list;
    }

    /**
     * @Description: 删除 运营位 的商品
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @Cacheable(value = {"SalesHeadCategory"})
    public void deleteSalesHeadCategoryProduct(Integer id) throws UpsertException, ResponseEntityException {
        SalesHeadCategoryProduct product = get2(SalesHeadCategoryProduct.class, "id", id);
        delete(product);
    }

    /**
     * @Description: 查询平台商品库列表，增加到相应的 运营位
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @Cacheable(value = {"SalesProduct"})
    public PageList<SalesProduct> findSalesProductList(PageQuery pageQuery, String productName, Integer categoryId, Integer headCategoryId, String productCode) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(productName))
            cri = Restrictions.and(cri, Restrictions.like("name", productName, MatchMode.ANYWHERE));
        if (categoryId != null)
            cri = Restrictions.and(cri, Restrictions.in("categoryId", getAllCategoryIdByParentId(categoryId)));

        //过滤掉已添加过的商品
        List<Integer> Prolist  = getSalesHeadCategoryProductIdList(headCategoryId);
        if (headCategoryId != null && Prolist.size() > 0)
            cri = Restrictions.and(cri, Restrictions.not(Restrictions.in("id", Prolist)));

        //跨表取条件
        JSONArray jsonArray = new JSONArray();
        if (StringUtils.isNotBlank(productCode)) {
            ExtFilter filter = new ExtFilter("plaProduct.productCode", "string", productCode, ExtFilter.ExtFilterComparison.like, null);
            jsonArray.add(filter);
        }
        pageQuery.filter = jsonArray.toJSONString();
        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "plaProduct,plaProductCategory";
        pageQuery.limit = 10;
        pageQuery.order = "desc";
        pageQuery.sort = "sort";
        PageList<SalesProduct> list = hibernateReadonlyRepository.getList(SalesProduct.class, pageQuery);
        return list;
    }

    /**
     * @Description: 取得该运营位的商品列表
     * @Author: Steven.Xiao
     * @Date: 2017/11/21
     */
    private List<Integer> getSalesHeadCategoryProductIdList(Integer headCategoryId) {
        Criterion cri = Restrictions.eq("headcategoryId", headCategoryId);
        List<Integer> list = new ArrayList<>();
        List<SalesHeadCategoryProduct> productList = getCurrentSession().createCriteria(SalesHeadCategoryProduct.class).add(cri).list();
        for (SalesHeadCategoryProduct product : productList) {
            list.add(product.getProductId());
        }
        return list;
    }

    /**
     * @Description: 查询 某分类及该分类下面的所有子分类；
     * @Author: Steven.Xiao
     * @Date: 2017/11/2
     */
    @Cacheable(value = {"PlaProductCategory"})
    public List<Integer> getAllCategoryIdByParentId(Integer id) {
        List<Integer> list = new ArrayList<>();
        list.add(id);
        Criterion cri = Restrictions.eq("parentId", id);
        List<PlaProductCategory> categories = getCurrentSession().createCriteria(PlaProductCategory.class).add(cri).list();
        for (PlaProductCategory category : categories) {
            list.addAll(getAllCategoryIdByParentId(category.getId()));
        }
        return list;
    }

    /**
     * @Description: 将销售平台中商品，增加到运营位商品中
     * @Author: Steven.Xiao
     * @Date: 2017/11/21
     */
    @Cacheable(value = {"SalesHeadCategory"})
    public void addSalesHeadCategoryProduct(Integer categoryId, List<IdForm> productIds) throws UpsertException, ResponseEntityException {
        for (IdForm form : productIds) {
            SalesHeadCategoryProduct product = new SalesHeadCategoryProduct();
            product.setHeadcategoryId(categoryId);
            product.setProductId(form.id);
            upsert2(product);
        }
    }
}
