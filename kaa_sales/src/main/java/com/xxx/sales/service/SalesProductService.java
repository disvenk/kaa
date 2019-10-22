package com.xxx.sales.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.*;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.*;
import com.xxx.sales.dao.SalesProductDao;
import com.xxx.sales.form.CategoryIdForm;
import com.xxx.sales.form.LabelIdForm;
import com.xxx.sales.form.SalesProductListForm;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SalesProductService extends CommonService {


    @Autowired
    private SalesProductDao salesProductDao;

    /**
     * @Description: 累加商品浏览量
     * @Author: Chen.zm
     * @Date: 2017/11/18 0018
     */
    @CacheEvict(value = {"SalesProduct"}, allEntries = true)
    public void addProductViews(Integer id) throws UpsertException{
        SalesProduct salesProduct = get2(SalesProduct.class, id);
        if (salesProduct != null) {
            salesProduct.setViews((salesProduct.getViews() == null ? 0 : salesProduct.getViews()) + 1);
            upsert2(salesProduct);
        }
    }

    /**
     * @Description: 获取商品详情
     * @Author: Chen.zm
     * @Date: 2017/11/18 0018
     */
    public SalesProduct productDetail(Integer id) {
        return (SalesProduct)getCurrentSession().createCriteria(SalesProduct.class)
                .add(Restrictions.eq("id", id))
                .setFetchMode("plaProduct", FetchMode.JOIN)
                .setFetchMode("salesProductPriceList", FetchMode.JOIN)
                .uniqueResult();
    }

    /**
     * @Description: 销售商品列表  近期上新  通过时间过滤
     * @Author: Chen.zm
     * @Date: 2017/11/17 0017
     */
    @Cacheable(value = {"SalesProduct,SalesProductPrice"})
    public PageList<SalesProduct> productNearList(PageQuery pageQuery, Date startDate, Date endDate) {
        Criterion cri = Restrictions.eq("status", 1);
        if (startDate != null && endDate != null)
            cri = Restrictions.and(cri, Restrictions.between("createdDate", startDate, endDate));
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "createdDate";
        PageList<SalesProduct> list = hibernateReadonlyRepository.getList(SalesProduct.class, pageQuery);
//        for (SalesProduct product : list) {
//            for (SalesProductPrice price : product.getSalesProductPriceList()) {
//            }
//        }
        return list;
    }

    /**
     * @Description: 获取首页楼层类别
     * @Author: Chen.zm
     * @Date: 2017/11/17 0017
     */
    public List<SalesHeadCategory> salesHeadcategoryList() {
        List<SalesHeadCategory> list = getCurrentSession().createCriteria(SalesHeadCategory.class)
                .add(Restrictions.eq("status", 1))
                .addOrder(Order.asc("sort"))
                .setFetchMode("salesHeadcategoryProductList", FetchMode.JOIN)
                .setFetchMode("salesHeadcategoryProductList.salesProduct", FetchMode.JOIN)
                .list();
        return ListUtils.removeDuplicateWithOrder(list);
    }

    /**
     * @Description: 获取首页banner列表
     * @Author: Chen.zm
     * @Date: 2017/11/17 0017
     */
    public List<SalesBanner> salesBannerList() {
        return getCurrentSession().createCriteria(SalesBanner.class)
                .add(Restrictions.eq("status", 1))
                .addOrder(Order.asc("sort"))
                .list();
    }

    /**
     * @Description: 获取首页banner包含的商品集合
     * @Author: Chen.zm
     * @Date: 2017/11/17 0017
     */
    public PageList<SalesBannerProduct> salesBannerProductList(PageQuery pageQuery, Integer bannerId) {
        Criterion cri = Restrictions.eq("bannerId", bannerId);
        JSONArray jsonArray = new JSONArray();
        ExtFilter filter = new ExtFilter("salesProduct.status", "numeric", "1", ExtFilter.ExtFilterComparison.ge, null);
        jsonArray.add(filter);
        pageQuery.filter = jsonArray.toJSONString();
        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "salesProduct,plaProduct";
        pageQuery.order = "asc";
        pageQuery.sort = "sort";
        return hibernateReadonlyRepository.getList(SalesBannerProduct.class, pageQuery);
    }


    /**
     * @Description: 获取销售商品列表
     * @Author: Chen.zm
     * @Date: 2017/11/17 0017
     */
    public PageList<SalesProduct> salesProductList(PageQuery pageQuery, SalesProductListForm form) {
        Criterion cri = Restrictions.eq("status", 1);
        if (form.categoryIdList.size() != 0) {
            List<Integer> categoryIds = new ArrayList<>();
            for (CategoryIdForm categoryIdForm : form.categoryIdList) categoryIds.add(categoryIdForm.categoryId);
            cri = Restrictions.and(cri, Restrictions.in("categoryId", categoryIds));
        }
        JSONArray jsonArray = new JSONArray();
        if (StringUtils.isNotBlank(form.nameOrCode)) {
            ExtFilter filter = new ExtFilter("plaProduct.productCode", "string", form.nameOrCode, ExtFilter.ExtFilterComparison.like, null);
            ExtFilter filter2 = new ExtFilter("name", "string", form.nameOrCode, ExtFilter.ExtFilterComparison.like, null);
            jsonArray.add(filter);
            jsonArray.add(JSONObject.parseObject("{\"operator\":\"or\"}"));
            jsonArray.add(filter2);
        }
        if (form.labelIdList.size() != 0) {
            StringBuilder labelIds = new StringBuilder();
            for (LabelIdForm labelIdForm : form.labelIdList) labelIds.append(labelIdForm.labelId).append(",");
            ExtFilter filter = new ExtFilter("salesProductLabelList.labelId", "list", labelIds.toString(), ExtFilter.ExtFilterComparison.in, null);
            jsonArray.add(filter);
        }
        if (form.startPrice != null) {
            ExtFilter filter = new ExtFilter("salesProductPriceList.offlinePrice", "numeric", form.startPrice.toString(), ExtFilter.ExtFilterComparison.ge, null);
            jsonArray.add(filter);
        }
        if (form.endPrice != null) {
            ExtFilter filter = new ExtFilter("salesProductPriceList.offlinePrice", "numeric", form.endPrice.toString(), ExtFilter.ExtFilterComparison.le, null);
            jsonArray.add(filter);
        }
        pageQuery.filter = jsonArray.toJSONString();
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        // 1:综合  2：销量  3：价格  4：最新
        String sortName = "sort";
        switch (form.sortType == null ? 0 : form.sortType) {
            case 1: sortName = "colligate";break;
            case 2: sortName = "sales";break;
//            case 3: sortName = "salesProductPriceList.offlinePrice";break; //会报错
            case 3: sortName = "price";break;
            case 4: sortName = "createdDate";break;
        }
        pageQuery.sort = sortName;
        PageList<SalesProduct> list = hibernateReadonlyRepository.getList(SalesProduct.class, pageQuery);
        return ListUtils.removeDuplicateWithOrder(list);
    }


    public PageList<JSONObject> findSalesProductList(MybatisPageQuery pageQuery) throws Exception {
        return salesProductDao.findSalesProductList(pageQuery);
    }


    public List<JSONObject> findProductLabel(Integer categoryId) throws Exception {
        return salesProductDao.findProductLabel(categoryId);
    }

    /**
     * @Description: 取得商品的图文详情
     * @Author: Steven.Xiao
     * @Date: 2017/12/7
     */
    public String getProductDescription(Integer pid) {
        SalesProductDescription salesProductDescription = get2(SalesProductDescription.class, "pid", pid);
        if(salesProductDescription==null) {
            return "";
        }
        return salesProductDescription.getDescription();
    }

}
