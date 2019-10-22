package com.xxx.sales.service;


import com.alibaba.fastjson.JSONArray;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.ExtFilter;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.sales.form.IdForm;
import com.xxx.sales.form.StoreProductForm;
import com.xxx.sales.form.StoreProductPictureListForm;
import com.xxx.sales.form.StoreProductSupplierListForm;
import com.xxx.user.service.UploadFileService;
import com.xxx.model.business.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StoreProductManageService extends CommonService {

    @Autowired
    private UploadFileService uploadFileService;

    /**
     * @Description:获取分类名称
     * @Author: hanchao
     * @Date: 2017/10/26 0026
     */
    @Cacheable(value = {"PlaProductCategory"})
    public List<PlaProductCategory> findProductCategoryList() {
       // Criterion cri = Restrictions.isNull("parentId" );
        return getCurrentSession().createCriteria(PlaProductCategory.class)
                .add(Restrictions.eq("logicDeleted", false))
                .addOrder(Order.asc("sort"))
                .list();
    }

    /**
     * @Description:根据id获取商品详情
     * @Author: hanchao
     * @Date: 2017/10/26 0026
     */
    @Cacheable(value = {"StoProduct,StoProductPicture,PlaProductCategory,StoProductPrice"})
    public StoProduct getStoProduct(Integer id, Integer storeId) {
        StoProduct stoProduct = (StoProduct) getCurrentSession().createCriteria(StoProduct.class)
                .add(Restrictions.and(Restrictions.eq("id", id),Restrictions.eq("storeId", storeId)))
                .setFetchMode("stoProductPictureList", FetchMode.JOIN)
                .uniqueResult();
        if(stoProduct != null) {
            for (StoProductPrice stoProductPrice : stoProduct.getStoProductPriceList()) {
            }
        }
        return stoProduct;
    }

    /**
     * @Description:根据id获取门店的平台商品库id
     * @Author: hanchao
     * @Date: 2017/10/26 0026
     */
    @Cacheable(value = {"StoProduct"})
    public StoProduct getplatProductIdStatus(Integer id, Integer storeId) {
        StoProduct stoProduct = (StoProduct) getCurrentSession().createCriteria(StoProduct.class)
                .add(Restrictions.and(Restrictions.eq("storeId", storeId),Restrictions.eq("platProductId", id)))
                .uniqueResult();
        return stoProduct;
    }

    @Cacheable(value = {"StoProduct"})
    public List<StoProduct> findStoProductList(Integer storeId) {
        return getCurrentSession().createCriteria(StoProduct.class)
                .add(Restrictions.eq("storeId", storeId))
                .list();
    }


    /**
     * @Description:根据id获取平台商品详情
     * @Author: hanchao
     * @Date: 2017/10/26 0026
     */
    @Cacheable(value = {"PlaProduct,PlaProductPrice,PlaProductPicture"})
    public PlaProduct getPlaProduct(Integer id) {
        PlaProduct plaProduct = (PlaProduct) getCurrentSession().createCriteria(PlaProduct.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
        if(plaProduct!=null) {
            for (PlaProductPrice plaProductPrice : plaProduct.getPlaProductPriceList()) {
            }
            for (PlaProductPicture plaProductPicture : plaProduct.getPlaProductPictureList()) {
            }
        }

        return plaProduct;
    }

    /**
     * @Description:商品管理删除商品
     * @Author: hanchao
     * @Date: 2017/10/27 0027
     */
    @CacheEvict(value = {"StoProduct"}, allEntries = true)
    public void updateStoreProductId(Integer id, Integer storeId) throws UpsertException,ResponseEntityException {
        StoProduct sto = get2(StoProduct.class, "id", id, "storeId", storeId);
        if (sto == null)
            throw new ResponseEntityException(120, "商品不存在");
        sto.setLogicDeleted(true);
        sto.setPlatProductId(null);
        upsert2(sto);
    }
    
    /**
     * @Description:商品库商品添加到展示商品(门店)
     * @Author: hanchao
     * @Date: 2017/10/31 0031
     */
    @CacheEvict(value = {"StoProduct"}, allEntries = true)
    public StoProduct insertStoreProductManage(IdForm form, Integer storeId) throws UpsertException,ResponseEntityException {
        PlaProduct pla = get2(PlaProduct.class, form.id);
        if (pla == null)
            throw new ResponseEntityException(120, "商品不存在");

        StoProduct sto = new StoProduct();//门店商品
        sto.setName(pla.getName());
        sto.setVedioUrl(pla.getVedioUrl());
        sto.setRemarks(pla.getRemarks());
        sto.setPlatProductId(pla.getId());//PlatProductId 平台商品id
        //2017.12.07 图文详情字段隔离
//        sto.setDescription(pla.getDescription());


        sto.setColligate(pla.getColligate());//排序
        sto.setCategoryId(pla.getCategoryId());
        sto.setSales(pla.getSales());//销量
        sto.setStatus(1);

        sto.setViews(pla.getViews());//浏览量
        sto.setPrice(pla.getPrice());//建议零售价(线下指导价)
        sto.setUpdateDate(pla.getUpdateDate());
        sto.setStoreId(storeId);//将商品id和门店id设置一样,没有默认
        sto.setBrand(pla.getBrand());
        sto.setSuplierDay(pla.getSuplierDay());//单件供货周期
        sto=upsert2(sto);

        //2017.12.07 图文详情字段隔离
        StoProductDescription stoProductDescription=get2(StoProductDescription.class,"pid",sto.getId());
        if(stoProductDescription==null)
        {
            StoProductDescription stoProductDescription1=new StoProductDescription();
            stoProductDescription1.setDescription(getPlatProductDescription(pla.getId()));
            stoProductDescription1.setPid(sto.getId());
            upsert2(stoProductDescription1);
        }
        else
        {
            stoProductDescription.setDescription(getPlatProductDescription(pla.getId()));
            upsert2(stoProductDescription);
        }

        //商品
        double minPrice = 0.0;
        double maxPrice = 0.0;
        for (PlaProductPrice plaProductPrice : pla.getPlaProductPriceList()){
            StoProductPrice stoProductPrice = new StoProductPrice();
            stoProductPrice.setPid(sto.getId());
            stoProductPrice.setColor(plaProductPrice.getColor());
            stoProductPrice.setSize(plaProductPrice.getSize());
            stoProductPrice.setStock(plaProductPrice.getStock());//库存
            stoProductPrice.setOfflinePrice(plaProductPrice.getOfflinePrice());//门店销售价格
            //stoProductPrice.setPrice(plaProductPrice.getOfflinePrice());//门店销售价格
            upsert2(stoProductPrice);

            if (minPrice == 0.0 || minPrice > plaProductPrice.getOfflinePrice()) minPrice = plaProductPrice.getOfflinePrice();
            if (maxPrice == 0.0 || maxPrice < plaProductPrice.getOfflinePrice()) maxPrice = plaProductPrice.getOfflinePrice();
        }
        sto.setMinPrice(minPrice);
        sto.setMaxPrice(maxPrice);

        //商品与图片
        sto.setHref(pla.getPlaProductPictureList().size() == 0 ? "" : pla.getPlaProductPictureList().get(0).getHref());//设置主图
        boolean isMainpic = true; //标记主图
        for (PlaProductPicture plaProductPicture : pla.getPlaProductPictureList()){
            StoProductPicture stoProductPicture = new StoProductPicture();
            stoProductPicture.setHref(plaProductPicture.getHref());
            stoProductPicture.setPid(sto.getId());
            stoProductPicture.setSort(1.0);
            stoProductPicture.setMainpic(false);
            if (isMainpic) {
                stoProductPicture.setMainpic(true);
            }
            isMainpic = false;
            upsert2(stoProductPicture);
        }
        return upsert2(sto);
    }



    /**
     * @Description:商品管理新增(列表筛选显示)
     * @Author: hanchao
     * @Date: 2017/10/30 0030
     */
    @Cacheable(value = {"PlaProduct,PlaProductPicture,PlaProductPrice,PlaProductCategory"})
    public PageList<PlaProduct> findProductList(PageQuery pageQuery, List<Integer> plaProductCategory, String labelIds, Integer sortType, Double startPrice, Double endPrice,String productCode) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
//        Criterion cri = Restrictions.eq("storeId", storeId);
//        Criterion cri = Restrictions.and(cri, Restrictions.eq("status", 1));
        if (plaProductCategory.size() != 0)
            cri = Restrictions.and(cri, Restrictions.in("categoryId", plaProductCategory));
        if (startPrice != null)
            cri = Restrictions.and(cri, Restrictions.ge("price", startPrice));
        if (endPrice != null)
            cri = Restrictions.and(cri, Restrictions.le("price", endPrice));
        if (StringUtils.isNotBlank(productCode))
            cri = Restrictions.and(cri, Restrictions.like("productCode", productCode,MatchMode.ANYWHERE));
        JSONArray jsonArray = new JSONArray();
        ExtFilter ef = new ExtFilter("plaProductSaleschannelList.saleschannelId", "numeric", "61", ExtFilter.ExtFilterComparison.eq, null);
        jsonArray.add(ef);
        if (StringUtils.isNotBlank(labelIds)) {
            ExtFilter filter = new ExtFilter("plaProductLabelList.labelId", "list", labelIds, ExtFilter.ExtFilterComparison.in, null);
            jsonArray.add(filter);
        }
        pageQuery.filter = jsonArray.toJSONString();
        //定义排序方式  1:综合  2,最新上线  3：销量  4：价格  5 ,周期
        String sortName = "id";
        if (sortType != null) {
            switch (sortType) {
                case 1: sortName = "colligate";break;//降序
                case 2: sortName = "createdDate";break;
//                case 5: sortName = "plaProductSupplierList.suplierDay";break;
                case 3: sortName = "sales";break;
                case 4: sortName = "price";break;//降序
            }
        }
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = sortName;
        PageList<PlaProduct> list = hibernateReadonlyRepository.getList(PlaProduct.class, pageQuery);
        return list;
    }


    /**
     * @Description:商品编辑详情提交
     * @Author: hanchao
     * @Date: 2017/10/26 0026
     */
    @CacheEvict(value = {"StoProduct"}, allEntries = true)
    public StoProduct   updateStoreProduct(StoreProductForm form, Integer storeId) throws UpsertException,ResponseEntityException {
        StoProduct sto= get2(StoProduct.class, "id", form.id, "storeId", storeId);
        if (sto == null)
            throw new ResponseEntityException(120, "商品不存在");
        sto.setName(form.name);
        if (StringUtils.isNotBlank(form.vedioUrl)) {
            sto.setVedioUrl(form.vedioUrl);
        }
        sto.setRemarks(form.remarks);//详情备注
        sto.setPlatProductId(form.platProductId);//商品id

        //2017.12.07 图文详情字段隔离
//        sto.setDescription(form.description);
        StoProductDescription stoProductDescription=get2(StoProductDescription.class,"pid",sto.getId());
        if(stoProductDescription==null)
        {
            StoProductDescription stoProductDescription1=new StoProductDescription();
            stoProductDescription1.setDescription(form.description);
            stoProductDescription1.setPid(sto.getId());
            upsert2(stoProductDescription1);
        }
        else
        {
            stoProductDescription.setDescription(form.description);
            upsert2(stoProductDescription);
        }
        sto.setColligate(form.colligate);//综合排序
        sto.setSales(form.sales);
        sto.setViews(form.views);
        sto.setPrice(form.price);//建议零售价
        sto.setCategoryId(form.categoryId);
        sto.setStatus(form.status);//状态,1上架,0下架
        sto.setBrand(form.brand);
        sto=upsert2(sto);

        double minPrice = 0.0;
        double maxPrice = 0.0;
        //商品与供应商
        String hql = "delete StoProductPrice where pid =:pid";
        getCurrentSession().createQuery(hql).setInteger("pid", sto.getId()).executeUpdate();
        for (StoreProductSupplierListForm storeProductSupplierListForms : form.storeProductSupplierList){
            StoProductPrice stoProductPrice = new StoProductPrice();
            stoProductPrice.setPid(sto.getId());
            stoProductPrice.setColor(storeProductSupplierListForms.color);
            stoProductPrice.setSize(storeProductSupplierListForms.size);
            stoProductPrice.setOfflinePrice(storeProductSupplierListForms.offlinePrice);
            stoProductPrice.setStock(storeProductSupplierListForms.stock);
            //sto.setPrice(storeProductSupplierListForms.price);//建议零售价
            upsert2(stoProductPrice);
            if (minPrice == 0.0 || minPrice > storeProductSupplierListForms.offlinePrice) minPrice = storeProductSupplierListForms.offlinePrice;
            if (maxPrice == 0.0 || maxPrice < storeProductSupplierListForms.offlinePrice) maxPrice = storeProductSupplierListForms.offlinePrice;
        }
        sto.setMinPrice(minPrice);
        sto.setMaxPrice(maxPrice);

        //商品与图片
        sto.setHref(form.storeProductPictureList.size() == 0 ? "" : form.storeProductPictureList.get(0).href);
        //清除原先的图片
        hql = "delete StoProductPicture where pid = :pid";
        getCurrentSession().createQuery(hql).setInteger("pid", sto.getId()).executeUpdate();
        boolean isMainpic = true; //标记主图
        for (StoreProductPictureListForm storeProductPictureList : form.storeProductPictureList){
            StoProductPicture stoProductPicture=new StoProductPicture();
            stoProductPicture.setHref(storeProductPictureList.href);
            stoProductPicture.setPid(sto.getId());
            stoProductPicture.setSort(1.0);
            stoProductPicture.setMainpic(false);
            if (isMainpic) {
                stoProductPicture.setMainpic(true);
            }
            isMainpic = false;
            upsert2(stoProductPicture);
        }

        return upsert2(sto);
    }

    /**
     * @Description:获取商品列表
     * @Author: hanchao
     * @Date: 2017/11/6 0006
     * 11.24: 将分页显示为:12
     */
    @Cacheable(value = {"StoProduct,StoProductSpec,StoProductPrice,StoProductSpec,StoProductPicture"})
    public PageList<StoProduct>  findProductList(Integer storeId, PageQuery pageQuery, String name, Integer categoryId) {
        Criterion cri = Restrictions.eq("storeId", storeId);
        if (StringUtils.isNotBlank(name))
            cri = Restrictions.and(cri, Restrictions.like("name", name, MatchMode.ANYWHERE));
        if (categoryId != null)
            cri = Restrictions.and(cri, Restrictions.in("categoryId", getAllCategory(categoryId)));
        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "plaProductCategory,plaProduct";
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<StoProduct> list = hibernateReadonlyRepository.getList(StoProduct.class, pageQuery);
        return list;
    }

    /**
     * @Description: 查询该分类下面的所有子分类；
     * @Author: Steven.Xiao
     * @Date: 2017/11/2
     */
    @Cacheable(value = {"PlaProductCategory"})
    public List<Integer> getAllCategory(Integer id) {
        List<Integer> list = new ArrayList<>();
        list.add(id);
        Criterion cri = Restrictions.eq("parentId", id);
        List<PlaProductCategory> categories = getCurrentSession().createCriteria(PlaProductCategory.class).add(cri).list();
        for (PlaProductCategory category : categories) {
            list.addAll(getAllCategory(category.getId()));
        }
        return list;
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
