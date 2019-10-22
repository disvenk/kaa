package com.xxx.store.service;


import com.alibaba.fastjson.JSONObject;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.StoProductDescription;
import com.xxx.store.dao.StoreHomeDao;
import com.xxx.user.service.BaseDataService;
import com.xxx.model.business.PlaProductCategory;
import com.xxx.model.business.StoProduct;
import com.xxx.model.business.StoProductPicture;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreHomeService extends CommonService {

    @Autowired
    private BaseDataService baseDataService;
    @Autowired
    private StoreHomeDao storeHomeDao;

    /**
     * @Description:将商品浏览量加1
     * @Author: hanchao
     * @Date: 2017/12/15 0015
     */
    @Cacheable(value = {"StoProduct"})
    public StoProduct addView(Integer id, Integer view) throws UpsertException, ResponseEntityException {
        StoProduct sto = get2(StoProduct.class, "id", id);
        sto.setViews(view + 1);
        return upsert2(sto);
    }

    /**
     * @Description: 获取商品主分类
     * @Author: Chen.zm
     * @Date: 2017/10/27 0027
     */
    @Cacheable(value = {"PlaProductCategory"})
    public List<JSONObject> findProductCategoryList(Integer storeId) {
        return storeHomeDao.findProductCategoryList(storeId);
    }

    /**
     * @Description: 获取商品列表
     * @Author: Chen.zm
     * @Date: 2017/10/27 0027
     */
    @Cacheable(value = {"StoProduct,StoProductPicture"})
    public PageList<StoProduct> findStoreProductList(PageQuery pageQuery, Integer storeId, Integer categoryId, Integer sortType, Double startPrice, Double endPrice) {
        Criterion cri = Restrictions.eq("storeId", storeId);
        cri = Restrictions.and(cri, Restrictions.eq("status", 1));
        if (categoryId != null)
//            cri = Restrictions.and(cri, Restrictions.in("categoryId", baseDataService.getAllCategoryIdByParentId(categoryId)));
            cri = Restrictions.and(cri, Restrictions.in("categoryId", categoryId));
        if (startPrice != null)
            cri = Restrictions.and(cri, Restrictions.ge("price", startPrice));
        if (endPrice != null)
            cri = Restrictions.and(cri, Restrictions.le("price", endPrice));
        //定义排序方式  1:综合  2：销量  3：价格  4：最新
        String sortName = "id";
        if (sortType != null) {
            switch (sortType) {
                case 1: sortName = "colligate"; break;
                case 2: sortName = "sales"; break;
                case 3: sortName = "price"; break;
                case 4: sortName = "createdDate"; break;
            }
        }
        pageQuery.hibernateCriterion = cri;
//        pageQuery.hibernateFetchFields = "plaProductCategory";
        pageQuery.order = "desc";
        pageQuery.sort = sortName;
        PageList<StoProduct> list = hibernateReadonlyRepository.getList(StoProduct.class, pageQuery);
        return list;
    }



    /**
     * @Description: 获取商品详情
     * @Author: Chen.zm
     * @Date: 2017/10/27 0027
     */
    @Cacheable(value = {"StoProduct,StoProductPicture,StoProductPrice"})
    public StoProduct findStoreProductDetail(Integer storeId, Integer productId) {
        Criterion cri = Restrictions.eq("storeId", storeId);
        cri = Restrictions.and(cri, Restrictions.eq("id", productId));
        StoProduct stoProduct = (StoProduct) getCurrentSession().createCriteria(StoProduct.class)
                .add(cri)
                .setFetchMode("stoProductPriceList", FetchMode.JOIN)
                .setFetchMode("stoProductPriceList.stoProductSpec", FetchMode.JOIN)
//                .setFetchMode("stoProductPictureList", FetchMode.JOIN)
                .setFetchMode("plaProduct", FetchMode.JOIN)
                .setFetchMode("plaProductCategory", FetchMode.JOIN)
                .uniqueResult();
        for (StoProductPicture picture : stoProduct.getStoProductPictureList()) { }
        return stoProduct;
    }

    /**
     * @Description: 取得商品的图文详情
     * @Author: Steven.Xiao
     * @Date: 2017/12/7
     */

    public String getProductDescription(Integer pid) {
        StoProductDescription stoProductDescription = get2(StoProductDescription.class, "pid", pid);
        if(stoProductDescription==null) {
            return "";
        }
        return stoProductDescription.getDescription();
    }



}
