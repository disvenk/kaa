package com.xxx.admin.service;


import com.alibaba.fastjson.JSONArray;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.ExtFilter;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.user.service.BaseDataService;
import com.xxx.user.service.UploadFileService;
import com.xxx.admin.form.*;
import com.xxx.model.business.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalesProductManageService extends CommonService {

    @Autowired
    private UploadFileService uploadFileService;

    @Autowired
    private BaseDataService baseDataService;

    /**
     * @Description:根据id获取销售平台商品详情
     * @Author: hanchao
     * @Date: 2017/10/26 0026
     */
    @Cacheable(value = {"SalesProduct"})
    public SalesProduct getSalesProduct(Integer id) {
        SalesProduct salesProduct = (SalesProduct) getCurrentSession().createCriteria(SalesProduct.class)
                .add(Restrictions.eq("id", id))
                .setFetchMode("SalesProductPicture", FetchMode.JOIN)
                .uniqueResult();
        return salesProduct;
    }

    /**
     * @Description:根据商品库商品id获取销售平台商品详情(判断商品库商品是否已经添加到销售平台商品)
     * @Author: hanchao
     * @Date: 2017/10/26 0026
     * BUG：2018.1.8 判断唯一性，一定要注意： .add(Restrictions.eq("logicDeleted", false))
     */
    @Cacheable(value = {"SalesProduct"})
    public SalesProduct getAdminProduct(Integer id) {
        SalesProduct salesProduct = (SalesProduct) getCurrentSession().createCriteria(SalesProduct.class)
                .add(Restrictions.eq("platProductId", id))
                .add(Restrictions.eq("logicDeleted", false))
                .setFetchMode("SalesProductPicture", FetchMode.JOIN)
                .uniqueResult();
        return salesProduct;
    }

    /**
     * @Description:销售平台商品删除和上下架
     * @Author: hanchao
     * @Date: 2017/11/17 0017
     */
    @CacheEvict(value = {"SalesProduct"}, allEntries = true)
    public void updateSalesProductStatus(Integer id,Integer status) throws ResponseEntityException, UpsertException {
        SalesProduct sal = get2(SalesProduct.class,"id",id);
        if (sal == null) {
            throw new ResponseEntityException(120, "商品不存在");
        }
        sal.setStatus(status == 1 ? 0 : 1 );
        upsert2(sal);
    }

    /**
     * @Description:销售平台商品删除
     * @Author: hanchao
     * @Date: 2017/11/17 0017
     */
    @CacheEvict(value = {"SalesProduct"}, allEntries = true)
    public void deleteSalesProduct(Integer id) throws ResponseEntityException, UpsertException {
        SalesProduct sal = get2(SalesProduct.class,"id",id);
        if (sal == null) {
            throw new ResponseEntityException(120, "商品不存在");
        }
        sal.setLogicDeleted(true);
        sal.setPlatProductId(null);
        upsert2(sal);
    }

    /**
     * @Description:商品库商品添加到销售平台商品
     * @Author: hanchao
     * @Date: 2017/11/17 0017
     */
    @CacheEvict(value = {"SalesProduct"}, allEntries = true)
    public SalesProduct insertsalesProducManage(IdForm form) throws UpsertException,ResponseEntityException {
        PlaProduct pla = get2(PlaProduct.class, form.id);
        if (pla == null)
            throw new ResponseEntityException(120, "商品不存在");

        SalesProduct sal = new SalesProduct();//销售平台商品
        sal.setName(pla.getName());
        sal.setVedioUrl(pla.getVedioUrl());
        sal.setBrand(pla.getBrand());
        sal.setCategoryId(pla.getCategoryId());
        sal.setColligate(pla.getColligate());//综合排序
        sal.setSales(pla.getSales());//销量
        sal.setViews(pla.getViews());//浏览量
        sal.setPrice(pla.getPrice());//建议零售价
        sal.setSuplierDay(pla.getSuplierDay());//单件供货周期
        sal.setRemarks(pla.getRemarks());
        sal.setPlatProductId(pla.getId());//PlatProductId 平台商品id
        //2017.12.07 图文详情字段隔离
//        sal.setDescription(pla.getDescription());

        sal.setStatus(1);//商品上架状态,默认上架
        sal.setUpdateDate(pla.getUpdateDate());
        sal = upsert2(sal);

        //2017.12.07 图文详情字段隔离
        SalesProductDescription salesProductDescription=get2(SalesProductDescription.class,"pid",sal.getId());
        if(salesProductDescription==null)
        {
            SalesProductDescription salesProductDescription1=new SalesProductDescription();
            salesProductDescription1.setDescription(getPlatProductDescription(pla.getId()));
            salesProductDescription1.setPid(sal.getId());
            upsert2(salesProductDescription1);
        }
        else
        {
            salesProductDescription.setDescription(getPlatProductDescription(pla.getId()));
            upsert2(salesProductDescription);
        }

        //商品
        double minPrice = 0.0;
        double maxPrice = 0.0;
        for (PlaProductPrice plaProductPrice : pla.getPlaProductPriceList()){
            SalesProductPrice salesProductPrice = new SalesProductPrice();
            salesProductPrice.setPid(sal.getId());
            salesProductPrice.setColor(plaProductPrice.getColor());
            salesProductPrice.setSize(plaProductPrice.getSize());
            salesProductPrice.setStock(plaProductPrice.getStock());//库存
            salesProductPrice.setOnlinePrice(plaProductPrice.getOnlinePrice());//线上销售价
            salesProductPrice.setOfflinePrice(plaProductPrice.getOfflinePrice());//线下销售价
            salesProductPrice.setRemarks(plaProductPrice.getRemarks());//规格备注
            //stoProductPrice.setPrice(plaProductPrice.getOfflinePrice());//门店销售价格
            upsert2(salesProductPrice);

            if (minPrice == 0.0 || minPrice > plaProductPrice.getOfflinePrice()) minPrice = plaProductPrice.getOfflinePrice();
            if (maxPrice == 0.0 || maxPrice < plaProductPrice.getOfflinePrice()) maxPrice = plaProductPrice.getOfflinePrice();
        }
        sal.setMinPrice(minPrice);
        sal.setMaxPrice(maxPrice);

        //商品与图片
        if(StringUtils.isNotBlank(pla.getPlaProductPictureList().size() == 0 ? "" : pla.getPlaProductPictureList().get(0).getHref())){
            sal.setHref(pla.getPlaProductPictureList().size() == 0 ? "" : pla.getPlaProductPictureList().get(0).getHref());//设置主图
        }
        boolean isMainpic = true; //标记主图
        for (PlaProductPicture plaProductPicture : pla.getPlaProductPictureList()){
            SalesProductPicture salesProductPicture = new SalesProductPicture();
            if(StringUtils.isNotBlank(plaProductPicture.getHref())){
                salesProductPicture.setHref(plaProductPicture.getHref());
            }
            salesProductPicture.setPid(sal.getId());
            salesProductPicture.setSort(1.0);
            salesProductPicture.setMainpic(false);
            if (isMainpic) {
                salesProductPicture.setMainpic(true);
            }
            isMainpic = false;
            upsert2(salesProductPicture);
        }

        //商品标签
        for (PlaProductLabel plaProductLabel : pla.getPlaProductLabelList()){
            SalesProductLabel salesProductLabel = new SalesProductLabel();
            salesProductLabel.setPid(sal.getId());
            salesProductLabel.setLabelNmae(plaProductLabel.getLabelNmae());
            salesProductLabel.setLabelId(plaProductLabel.getLabelId());
            upsert2(salesProductLabel);
        }

        return upsert2(sal);
    }


    /**
     * @Description:销售平台从商品库新增商品(筛选列表显示)
     * @Author: hanchao
     * @Date: 2017/11/17 0017
     */
    @Cacheable(value = {"PlaProduct"})
    public PageList<PlaProduct> findSalesProductManageList(PageQuery pageQuery, List<Integer> plaProductCategory, String labelIds) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (plaProductCategory.size() != 0)
            cri = Restrictions.and(cri, Restrictions.in("categoryId", plaProductCategory));
        if (StringUtils.isNotBlank(labelIds)) {
            ExtFilter filter = new ExtFilter("plaProductLabelList.labelId", "list", labelIds, ExtFilter.ExtFilterComparison.in, null);
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(filter);
            pageQuery.filter = jsonArray.toJSONString();
        }
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<PlaProduct> list = hibernateReadonlyRepository.getList(PlaProduct.class, pageQuery);
        for (PlaProduct plaProduct : list) {
           for (PlaProductPrice plaProductPrice : plaProduct.getPlaProductPriceList()){}
        }
        return list;
    }

    /**
     * @Description:销售平台商品编辑详情保存
     * @Author: hanchao
     * @Date: 2017/11/17 0017
     */
    @CacheEvict(value = {"SalesProduct"}, allEntries = true)
    public SalesProduct updateSalesProduct(SalesProductManageForm form) throws UpsertException,ResponseEntityException {
        SalesProduct sal= get2(SalesProduct.class, "id", form.id);
        if (sal == null)
            throw new ResponseEntityException(120, "商品不存在");
        if (StringUtils.isNotBlank(form.vedioUrl)) {
            sal.setVedioUrl(form.vedioUrl);
        }
        sal.setName(form.name);
        sal.setBrand(form.brand);
        sal.setCategoryId(form.categoryId);
        /*货号设为只读状态,不用保存*/
        /*sal.getPlaProduct().setPno(sal.getPlaProduct() == null ? "" : sal.getPlaProduct().getPno());*/
        sal.setColligate(form.colligate);//综合排序
        sal.setSales(form.sales);
        sal.setViews(form.views);
        sal.setStatus(form.status);//状态,1上架,0下架
        sal.setRemarks(form.remarks);//详情备注
        /*sal.setPlatProductId(form.platProductId);//平台id*/

        //2017.12.07 图文详情隔离
//        sal.setDescription(form.description);

        sal = upsert2(sal);

        //2017.12.07 图文详情隔离
        SalesProductDescription salesProductDescription=get2(SalesProductDescription.class,"pid",form.id);
        if(salesProductDescription!=null) {
            salesProductDescription.setDescription(form.description);
            upsert2(salesProductDescription);
        }
        else {
            SalesProductDescription salesProductDescription1=new SalesProductDescription();
            salesProductDescription1.setDescription(form.description);
            salesProductDescription1.setPid(form.id);
            upsert2(salesProductDescription1);
        }

        //商品明细
        String hql = "delete SalesProductPrice where pid = :pid";
        getCurrentSession().createQuery(hql).setInteger("pid", form.id).executeUpdate();
        double minPrice = 0.0;
        double maxPrice = 0.0;
        for (SalesProductManagePriceListForm salesProductPriceList : form.salesProductPriceList){
            SalesProductPrice salesProductPrice = new SalesProductPrice();
            salesProductPrice.setPid(sal.getId());
            salesProductPrice.setColor(salesProductPriceList.color);
            salesProductPrice.setSize(salesProductPriceList.size);
            salesProductPrice.setOfflinePrice(salesProductPriceList.offlinePrice);
            salesProductPrice.setStock(salesProductPriceList.stock);
            salesProductPrice.setRemarks(salesProductPriceList.specRemark);
            upsert2(salesProductPrice);

            if (minPrice == 0.0 || minPrice > salesProductPriceList.offlinePrice) minPrice = salesProductPriceList.offlinePrice;
            if (maxPrice == 0.0 || maxPrice < salesProductPriceList.offlinePrice) maxPrice = salesProductPriceList.offlinePrice;
        }
        sal.setMinPrice(minPrice);
        sal.setMaxPrice(maxPrice);

        //清除之前的标签
        hql = "delete SalesProductLabel where pid = :pid";
        getCurrentSession().createQuery(hql).setInteger("pid", form.id).executeUpdate();
        for (SalesProductManageLabelListForm salesProductLabelList : form.salesProductLabelList){
            SalesProductLabel salesProductLabel = new SalesProductLabel();
            salesProductLabel.setLabelId(salesProductLabelList.labelId);
            salesProductLabel.setLabelNmae(salesProductLabelList.labelName);
            salesProductLabel.setPid(sal.getId());
            upsert2(salesProductLabel);
        }

        //商品与图片
        sal.setHref(form.salesProductPictureList.size() == 0 ? "" : form.salesProductPictureList.get(0).href);
        //清除原先的图片
        hql = "delete SalesProductPicture where pid = :pid";
        getCurrentSession().createQuery(hql).setInteger("pid", form.id).executeUpdate();
        boolean isMainpic = true; //标记主图
        for (SalesProductManagePictureListForm storeProductPictureList : form.salesProductPictureList){
            SalesProductPicture salesProductPicture = new SalesProductPicture();
            salesProductPicture.setHref(storeProductPictureList.href);
            salesProductPicture.setPid(sal.getId());
            salesProductPicture.setSort(1.0);
            salesProductPicture.setMainpic(false);
            if (isMainpic) {
                salesProductPicture.setMainpic(true);
            }
            isMainpic = false;
            upsert2(salesProductPicture);
        }

        return upsert2(sal);
    }

    /**
     * @Description:销售平台的商品列表
     * @Author: hanchao
     * @Date: 2017/11/16 0016
     */
    @Cacheable(value = {"SalesProduct"})
    public PageList<SalesProduct> findSalesProductList(PageQuery pageQuery, String name, Integer categoryId, String productCode) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(name))
            cri = Restrictions.and(cri, Restrictions.like("name", name, MatchMode.ANYWHERE));
        if (categoryId != null)
            cri = Restrictions.and(cri, Restrictions.in("categoryId", baseDataService.getAllCategoryIdByParentId(categoryId)));
        if (StringUtils.isNotBlank(productCode)) {
            ExtFilter filter = new ExtFilter("plaProduct.productCode", "string", productCode, ExtFilter.ExtFilterComparison.like, null);
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(filter);
            pageQuery.filter = jsonArray.toJSONString();
        }
        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "plaProductCategory";
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<SalesProduct> list = hibernateReadonlyRepository.getList(SalesProduct.class, pageQuery);
        for (SalesProduct salesProduct : list) {
            for (SalesProductPrice salesProductPrice : salesProduct.getSalesProductPriceList()) {}
        }
        return list;
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

    /**
     * @Description: 取得商品的图文详情
     * @Author: Steven.Xiao
     * @Date: 2017/12/7
     */

    public String getPlatProductDescription(Integer pid) {
        PlaProductDescription plaProductDescription = get2(PlaProductDescription.class, "pid", pid);
        if(plaProductDescription==null) {
            return "";
        }
        return plaProductDescription.getDescription();
    }

}
