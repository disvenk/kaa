package com.xxx.admin.service;

import com.alibaba.fastjson.JSONArray;
import com.xxx.admin.form.BannerAddForm;
import com.xxx.admin.form.BannerSaveEditForm;
import com.xxx.admin.form.IdForm;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.ExtFilter;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.user.service.BaseDataService;
import com.xxx.user.service.UploadFileService;
import com.xxx.model.business.SalesBanner;
import com.xxx.model.business.SalesBannerProduct;
import com.xxx.model.business.SalesProduct;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BannerService extends CommonService {

    @Autowired
    private UploadFileService uploadFileService;
    @Autowired
    private BaseDataService baseDataService;

    /**
     * @Description: Banner新增
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @Cacheable(value = {"SalesBanner"})
    public SalesBanner addBanner(BannerAddForm form) throws UpsertException, ResponseEntityException {
        SalesBanner banner = new SalesBanner();
        banner.setName(form.name);
        banner.setPicaddress(uploadFileService.saveOssUploadFileByBase64(form.href).toString());
        banner.setDescription(form.description);
        banner.setSort(form.sort);
        banner.setStatus(1);
        banner.setUpdateDate(new Date());
       return upsert2(banner);
    }

    /**
     * @Description: 首页运营Banner位查询列表
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @Cacheable(value = {"SalesBanner"})
    public PageList<SalesBanner> findBannerList(PageQuery pageQuery,String name) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(name)) {
            cri = Restrictions.and(cri, Restrictions.like("name", name,MatchMode.ANYWHERE));
        }
        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "SalesBanner";
        pageQuery.limit = 10;
        pageQuery.order = "asc";
        pageQuery.sort = "sort";
        PageList<SalesBanner> list = hibernateReadonlyRepository.getList(SalesBanner.class, pageQuery);
        return list;
    }

    /**
     * @Description: 删除Banner位
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @Cacheable(value = {"SalesBanner"})
    public void deleteBanner(Integer id) throws UpsertException, ResponseEntityException {
        SalesBanner banner=get2(SalesBanner.class,"id",id);
        delete(banner);

        //删除Banner位的商品
        //同时，删除运营的商品
        String hql = "delete SalesBannerProduct where headcategoryId =:id";
        getCurrentSession().createQuery(hql).setInteger("id", id).executeUpdate();
    }
    
    /**
     * @Description: 控制banner显示/隐藏
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @Cacheable(value = {"SalesBanner"})
    public SalesBanner setDisabledBanner(Integer id,Integer status) throws UpsertException, ResponseEntityException {
        SalesBanner banner=get2(SalesBanner.class,"id",id);
        banner.setStatus(status);
        return upsert2(banner);
    }

    /**
     * @Description: 取得banner
     * @Author: Steven.Xiao
     * @Date: 2017/11/21
     */

    @Cacheable(value = {"SalesBanner"})
    public SalesBanner getBanner(Integer id) throws UpsertException, ResponseEntityException {
        SalesBanner banner=get2(SalesBanner.class,"id",id);
        return banner;
    }

    /**
     * @Description: Banner编辑保存
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @Cacheable(value = {"SalesBanner"})
    public SalesBanner saveEditBanner(BannerSaveEditForm form) throws UpsertException, ResponseEntityException {
        SalesBanner banner=get2(SalesBanner.class,"id",form.id);
        banner.setName(form.name);
        banner.setDescription(form.description);
        banner.setPicaddress(uploadFileService.saveOssUploadFileByBase64(form.href).toString());
        banner.setSort(form.sort);
        return upsert2(banner);
    }

    /**
     * @Description: 查询Banner位商品列表
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @Cacheable(value = {"SalesBannerProduct"})
    public PageList<SalesBannerProduct> findBannerProductList(PageQuery pageQuery, Integer bannerId) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (bannerId!=null)
            cri = Restrictions.and(cri, Restrictions.eq("bannerId",bannerId));
        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "salesProduct";
        pageQuery.limit = 10;
        pageQuery.order = "desc";
        pageQuery.sort = "sort";
        PageList<SalesBannerProduct> list = hibernateReadonlyRepository.getList(SalesBannerProduct.class, pageQuery);
        return list;
    }

    /**
     * @Description: 删除Banner位的商品
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @Cacheable(value = {"SalesBanner"})
    public void deleteBannerProduct(Integer id) throws UpsertException, ResponseEntityException {
        SalesBannerProduct product=get2(SalesBannerProduct.class,"id",id);
        delete(product);
    }

    /**
     * @Description: 查询平台商品库列表，增加到相应的Banner位
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @Cacheable(value = {"SalesBannerProduct"})
    public PageList<SalesProduct> findSalesProductList(PageQuery pageQuery, String productName, Integer categoryId, Integer bannerId, String productCode) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(productName))
            cri = Restrictions.and(cri, Restrictions.like("name", productName, MatchMode.ANYWHERE));
        if (categoryId!=null)
            cri = Restrictions.and(cri, Restrictions.in("categoryId", baseDataService.getAllCategoryIdByParentId(categoryId)));

        //过滤已添加的商品
        List<Integer> proList = getBannerProductIdList(bannerId);
        if (bannerId != null && proList.size() > 0)
            cri = Restrictions.and(cri, Restrictions.not(Restrictions.in("id", proList)));

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
    private List<Integer> getBannerProductIdList(Integer bannerId) {
        Criterion cri = Restrictions.eq("bannerId", bannerId);
        List<Integer> list = new ArrayList<>();
        List<SalesBannerProduct> productList = getCurrentSession().createCriteria(SalesBannerProduct.class).add(cri).list();
        for (SalesBannerProduct product : productList) {
            list.add(product.getProductId());
        }
        return list;
    }

    /**
     * @Description: 将销售平台中商品，增加到运营位商品中
     * @Author: Steven.Xiao
     * @Date: 2017/11/21
     */
    @Cacheable(value = {"SalesBannerProduct"})
    public void addSalesBannerProduct(Integer bannerId, List<IdForm> productIds) throws UpsertException, ResponseEntityException {
        for (IdForm form : productIds) {
            SalesBannerProduct product = new SalesBannerProduct();
            product.setBannerId(bannerId);
            product.setProductId(form.id);
            upsert2(product);
        }
    }
}
