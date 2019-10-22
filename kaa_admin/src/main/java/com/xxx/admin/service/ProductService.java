package com.xxx.admin.service;


import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.user.service.BaseDataService;
import com.xxx.user.service.UploadFileService;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.RandomUtils;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService extends CommonService {

    @Autowired
    private UploadFileService uploadFileService;

    @Autowired
    private BaseDataService baseDataService;

    /**
     * @Description:获取供应商名称
     * @Author: hanchao
     * @Date: 2017/11/3 0003
     */
    @Cacheable(value = {"SupSuplier"})
    public List<SupSuplier> findSuplierNameList() {
        //2017.11.23 审核成功且有效的
        Criterion cri = Restrictions.eq("logicDeleted", false);
        cri = Restrictions.and(cri, Restrictions.eq("approveStatus", 1));
        return  getCurrentSession().createCriteria(SupSuplier.class)
                .add(cri)
                .list();
    }

    /**
     * @Description:根据id获取商品详情
     * @Author: hanchao
     * @Date: 2017/10/26 0026
     */
    @Cacheable(value = {"PlaProduct,PlaProductLabel,PlaProductPrice,PlaProductPicture,PlaProductSaleschannel"})
    public PlaProduct getPlaProduct(Integer id) {
        PlaProduct plaProduct = (PlaProduct) getCurrentSession().createCriteria(PlaProduct.class)
                .add(Restrictions.eq("id", id))
                .setFetchMode("plaProductCategory", FetchMode.JOIN)
                .uniqueResult();
        if(plaProduct != null) {
            for (PlaProductLabel plaProductLabel : plaProduct.getPlaProductLabelList()) {
            }
            for (PlaProductPrice plaProductPrice : plaProduct.getPlaProductPriceList()) {
            }
            for (PlaProductSaleschannel plaProductSaleschannel : plaProduct.getPlaProductSaleschannelList()) {
            }
            for (PlaProductPicture plaProductPicture : plaProduct.getPlaProductPictureList()) {
            }
        }
        return plaProduct;
    }

    /**
     * @Description: 取得商品的图文详情
     * @Author: Steven.Xiao
     * @Date: 2017/12/7
     */

    public String getProductDescription(Integer pid) {
        PlaProductDescription plaProductDescription = get2(PlaProductDescription.class, "pid", pid);
        if(plaProductDescription==null) {
            return "";
        }
        return plaProductDescription.getDescription();
    }


//    /**
//     * @Description:商品属性新增
//     * @Author: hanchao
//     * @Date: 2017/11/1 0001
//     */
//    @CacheEvict(value = {"PlaProductSpec"}, allEntries = true)
//    public PlaProductSpec insertPropertyProduct(String sizeDefine,Integer sizeId,String colorDefine,Integer colorId) throws UpsertException,ResponseEntityException {
//        Criterion cri = Restrictions.eq("colorId", colorId);
//        cri = Restrictions.and(cri, Restrictions.eq("sizeId", sizeId));
//        PlaProductSpec plaProductSpec = (PlaProductSpec) getCurrentSession().createCriteria(PlaProductSpec.class).add(cri).uniqueResult();
//        if(plaProductSpec == null) {
//            plaProductSpec = new PlaProductSpec();
//            plaProductSpec.setColorDefine(colorDefine);
//            plaProductSpec.setSizeDefine(sizeDefine);
//            plaProductSpec.setColorId(colorId);
//            plaProductSpec.setSizeId(sizeId);
//            plaProductSpec = upsert2(plaProductSpec);
//        }
//        return plaProductSpec;
//
//    }


    /**
     * @Description:商品库删除商品
     * @Author: hanchao
     * @Date: 2017/10/27 0027
     */
    @CacheEvict(value = {"PlaProduct"}, allEntries = true)
    public void updateProductId(Integer id) throws UpsertException {
        PlaProduct pla = get2(PlaProduct.class, id);
        pla.setLogicDeleted(true);
        upsert2(pla);
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
     * @Description:商品库新增货号判断是否重复
     * @Author: hanchao
     * @Date: 2017/11/9 0009
     */
    @Cacheable(value = {"Plaproduct"})
    public PlaProduct selectPno1(String pno) throws ResponseEntityException{
        PlaProduct plaProduct = (PlaProduct) getCurrentSession().createCriteria(PlaProduct.class)
                .add(Restrictions.eq("pno", pno))
                .uniqueResult();
        return plaProduct;
    }

    /**
     * @Description:商品库编辑货号判断是否重复
     * @Author: hanchao
     * @Date: 2017/11/9 0009
     */
    @Cacheable(value = {"Plaproduct"})
    public PlaProduct selectPno(String pno,Integer id) throws ResponseEntityException{
        PlaProduct plaProduct = (PlaProduct) getCurrentSession().createCriteria(PlaProduct.class)
                .add(Restrictions.eq("pno", pno))
                .add(Restrictions.ne("id", id))
                .uniqueResult();
        return plaProduct;
    }

    /**
     * @Description:商品库新增商品
     * @Author: hanchao
     * @Date: 2017/10/26 0026
     */
    @CacheEvict(value = {"PlaProduct"}, allEntries = true)
    public PlaProduct insertProduct(ProductForm form) throws UpsertException,ResponseEntityException,ParseException {
        PlaProduct pla = new PlaProduct();
        pla.setName(form.name);
        if (StringUtils.isNotBlank(form.vedioUrl)) {
            pla.setVedioUrl(form.vedioUrl);
        }
//        pla.setPno(form.pno);
        pla.setDesigner(form.isDesigner);
        pla.setUpdateDate(DateTimeUtils.parseDate(form.updateDate, "yyyy-MM-dd hh:mm:ss"));
        pla.setRemarks(form.remarks);

        //2017.12.07 商品详情字段，隔离
//        pla.setDescription(form.description);

        pla.setPrice(form.prices==null?0:form.prices);
        pla.setColligate(form.colligate);
        pla.setSales(form.sales);
        pla.setViews(form.views);
        pla.setCategoryId(form.categoryId);
        pla.setBrand(form.brand);
        pla.setAdd(false);
        pla.setSuplierDay(form.suplierDay);
//        pla.setSuplierId(form.productSupplierId);
        pla.setMaterial(form.material);
        pla.setTechnics(form.technics);

        pla.setHref(form.productPictureList.size() == 0 ? "" : form.productPictureList.get(0).href);
        pla.setProductCode(createProductCode(form.categoryId));
        pla = upsert2(pla); //引用最新的对象  获取id
        boolean isMainpic = true; //标记主图
        for (ProductPictureListForm productPicture : form.productPictureList) {
            PlaProductPicture plaProductPicture = new PlaProductPicture();
            plaProductPicture.setHref(productPicture.href);
            plaProductPicture.setPid(pla.getId());
            plaProductPicture.setSort(1.0);
            plaProductPicture.setMainpic(false);
            if (isMainpic) {
                plaProductPicture.setMainpic(true);
            }
            isMainpic = false;
            upsert2(plaProductPicture);
        }

        //2017.12.07 商品详情字段，隔离
        PlaProductDescription plaProductDescription=new PlaProductDescription();
        plaProductDescription.setPid(pla.getId());
        plaProductDescription.setDescription(form.description);
        upsert2(plaProductDescription);

        //标签改成集合
        for (ProductLabelListForm plaProductLables : form.productLabelList){
            PlaProductLabel plaProductLabel = new PlaProductLabel();
            plaProductLabel.setLabelId(plaProductLables.labelId);
            plaProductLabel.setLabelNmae(plaProductLables.labelName);
            plaProductLabel.setPid(pla.getId());
            upsert2(plaProductLabel);
        }

        //销售渠道
        for(ProductSchannelListForm productSchannelListForm : form.productSchannelList){
            PlaProductSaleschannel saleschannel = new PlaProductSaleschannel();
            saleschannel.setSaleschannelId(productSchannelListForm.saleschannelId);
            saleschannel.setSaleschannelName(productSchannelListForm.saleschannelName);
            saleschannel.setPid(pla.getId());
            upsert2(saleschannel);
        }

        //价格
        double minPrice = 0.0;
        double maxPrice = 0.0;
        for (ProductSupplierListForm productSupplierListForm : form.productSupplierList){
            PlaProductPrice price = new PlaProductPrice();
            price.setPid(pla.getId());
            price.setColor(productSupplierListForm.color);
            price.setSize(productSupplierListForm.size);
            price.setRemarks(productSupplierListForm.categoryRemark);//商品规格备注
//            price.setPrice(productSupplierListForm.price);
            price.setStock(productSupplierListForm.stock);
            price.setOnlinePrice(productSupplierListForm.onlinePrice);
            price.setOfflinePrice(productSupplierListForm.offlinePrice);
            upsert2(price);

            if (minPrice == 0.0 || minPrice > productSupplierListForm.offlinePrice) minPrice = productSupplierListForm.offlinePrice;
            if (maxPrice == 0.0 || maxPrice < productSupplierListForm.offlinePrice) maxPrice = productSupplierListForm.offlinePrice;
        }
        pla.setMinPrice(minPrice);
        pla.setMaxPrice(maxPrice);
        return pla;
    }

    /**
     * @Description: 商品编号的生成规则：一级分类00+二级分类000+5码流水00000==商品编号
     * @Author: Steven.Xiao
     * @Date: 2017/11/16
     */

    public String createProductCode(Integer categoryId) {
        //2017.11.16 steven 增加商品编号的产生规则
        PlaProductCategory plaProductCategory = get2(PlaProductCategory.class, "id", categoryId);
        for (int i=0;i<20;i++) {
            String productCode = plaProductCategory.getCategoryCode() + RandomUtils.randomFixedLength(5);
            PlaProduct plaProduct = get2(PlaProduct.class, "productCode", productCode);
            if (plaProduct == null) {
                return productCode;
            }
        }
        return "";
    }

    /**
     * @Description:商品编辑保存
     * @Author: hanchao
     * @Date: 2017/10/26 0026
     */
    @CacheEvict(value = {"PlaProduct"}, allEntries = true)
    public PlaProduct updateProduct(ProductForm form) throws UpsertException,ResponseEntityException,ParseException {
        PlaProduct pla = get2(PlaProduct.class, form.id);
        if (pla == null)
            throw new ResponseEntityException(120, "商品不存在");
        pla.setName(form.name);
        if (StringUtils.isNotBlank(form.vedioUrl)) {
            pla.setVedioUrl(form.vedioUrl);
        }
//        pla.setPno(form.pno);
        pla.setDesigner(form.isDesigner);
        pla.setUpdateDate(DateTimeUtils.parseDate(form.updateDate, "yyyy-MM-dd hh:mm:ss"));
        pla.setRemarks(form.remarks);

        //2017.12.07 商品详情字段，隔离
//        pla.setDescription(form.description);

        pla.setPrice(form.prices);
        pla.setColligate(form.colligate);
        pla.setSales(form.sales);
        pla.setViews(form.views);
        pla.setCategoryId(form.categoryId);
        pla.setBrand(form.brand);
//        pla.setSuplierId(form.productSupplierId);//供应商id
        pla.setMaterial(form.material);
        pla.setTechnics(form.technics);
        pla.setSuplierDay(form.suplierDay);
        pla.setHref(form.productPictureList.size() == 0 ? "" : form.productPictureList.get(0).href);
        pla = upsert2(pla); //引用最新的对象  获取id

        //2017.12.07 商品详情字段，隔离
        PlaProductDescription plaProductDescription=get2(PlaProductDescription.class,"pid",form.id);
        if(plaProductDescription!=null) {
            plaProductDescription.setDescription(form.description);
            upsert2(plaProductDescription);
        }
        else {
            PlaProductDescription plaProductDescription1 = new PlaProductDescription();
            plaProductDescription.setDescription(form.description);
            plaProductDescription.setPid(form.id);
            upsert2(plaProductDescription);
        }

        //设置主图
        //清除原先的图片
        String hql = "delete PlaProductPicture where pid =:pid";
        getCurrentSession().createQuery(hql).setInteger("pid", pla.getId()).executeUpdate();
        boolean isMainpic = true; //标记主图
        for (ProductPictureListForm productPicture : form.productPictureList) {
            PlaProductPicture plaProductPicture = new PlaProductPicture();
            plaProductPicture.setHref(productPicture.href);
            plaProductPicture.setPid(pla.getId());
            plaProductPicture.setSort(1.0);
            plaProductPicture.setMainpic(false);
            if (isMainpic) {
                plaProductPicture.setMainpic(true);
            }
            isMainpic = false;
            upsert2(plaProductPicture);
        }

        //标签改成集合
        //删除之前的标签
        hql = "delete PlaProductLabel where pid =:pid";
        getCurrentSession().createQuery(hql).setInteger("pid", pla.getId()).executeUpdate();
        for (ProductLabelListForm plaProductLables : form.productLabelList){
            PlaProductLabel plaProductLabel = new PlaProductLabel();
            plaProductLabel.setLabelId(plaProductLables.labelId);
            plaProductLabel.setLabelNmae(plaProductLables.labelName);
            plaProductLabel.setPid(pla.getId());
            upsert2(plaProductLabel);
        }

        //销售渠道
        //删除之前的标签
        hql = "delete PlaProductSaleschannel where pid =:pid";
        getCurrentSession().createQuery(hql).setInteger("pid", pla.getId()).executeUpdate();
        for(ProductSchannelListForm productSchannelListForm : form.productSchannelList){
            PlaProductSaleschannel saleschannel = new PlaProductSaleschannel();
            saleschannel.setSaleschannelId(productSchannelListForm.saleschannelId);
            saleschannel.setSaleschannelName(productSchannelListForm.saleschannelName);
            saleschannel.setPid(pla.getId());
            upsert2(saleschannel);
        }

        //价格
        //删除之前的标签
        double minPrice = 0.0;
        double maxPrice = 0.0;
        hql = "delete PlaProductPrice where pid =:pid";
        getCurrentSession().createQuery(hql).setInteger("pid", pla.getId()).executeUpdate();
        for (ProductSupplierListForm productSupplierListForm : form.productSupplierList){
            PlaProductPrice plaProductSupplier = new PlaProductPrice();
            plaProductSupplier.setRemarks(productSupplierListForm.categoryRemark);
            plaProductSupplier.setColor(productSupplierListForm.color);
            plaProductSupplier.setSize(productSupplierListForm.size);
            plaProductSupplier.setPid(pla.getId());
//            plaProductSupplier.setPrice(productSupplierListForm.price);
            plaProductSupplier.setStock(productSupplierListForm.stock);
            plaProductSupplier.setOnlinePrice(productSupplierListForm.onlinePrice);
            plaProductSupplier.setOfflinePrice(productSupplierListForm.offlinePrice);
            upsert2(plaProductSupplier);

            if (minPrice == 0.0 || minPrice > productSupplierListForm.offlinePrice) minPrice = productSupplierListForm.offlinePrice;
            if (maxPrice == 0.0 || maxPrice < productSupplierListForm.offlinePrice) maxPrice = productSupplierListForm.offlinePrice;
        }
        pla.setMinPrice(minPrice);
        pla.setMaxPrice(maxPrice);
        return upsert2(pla);
    }


    /**
     * @Description: 获取平台商品列表
     * @Author: Chen.zm
     * @Date: 2017/10/25 0025
     */
    @Cacheable(value = {"PlaProduct,PlaProductPrice,PlaProductPicture"})
    public PageList<PlaProduct> findProductList(PageQuery pageQuery, Boolean isMoren, String name, Integer categoryId,String pno,Boolean designer) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(name) && name!=null)
            cri = Restrictions.and(cri, Restrictions.like("name", name, MatchMode.ANYWHERE));
        if (categoryId != null)
           // cri = Restrictions.and(cri, Restrictions.in("categoryId", baseDataService.getAllCategoryIdByParentId(categoryId)));
            cri = Restrictions.and(cri, Restrictions.eq("categoryId", categoryId));
        if(StringUtils.isNotBlank(pno) && pno!=null)
            cri = Restrictions.and(cri, Restrictions.like("productCode", pno,MatchMode.ANYWHERE));
        if(isMoren != null)
            cri = Restrictions.and(cri, Restrictions.eq("isAdd", isMoren));
        if(designer != null)
            cri = Restrictions.and(cri, Restrictions.eq("isDesigner", designer));
        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "plaProductCategory";
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<PlaProduct> list = hibernateReadonlyRepository.getList(PlaProduct.class, pageQuery);
        for (PlaProduct plaProduct : list) {
            for (PlaProductPrice plaProductPrice : plaProduct.getPlaProductPriceList()) {}
            for (PlaProductSaleschannel picture : plaProduct.getPlaProductSaleschannelList());
        }
        return list;
    }


    /**
     * @Description: 取分类的供货周期
     * @Author: Steven.Xiao
     * @Date: 2017/12/12
     */
    public String getCategorySupplierDay(Integer categoryId)
    {
        PlaProductCategory plaProductCategory=get2(PlaProductCategory.class,"id",categoryId);
        if(plaProductCategory!=null) {
            return plaProductCategory.getSupplierDay();
        }
        return "1";
    }


    /**
     * @Description: 设置商品是否默认添加至门店
     * @Author: Chen.zm
     * @Date: 2017/12/21 0021
     */
    @CacheEvict(value = {"PlaProduct"}, allEntries = true)
    public void productIsAdd(Integer id, Boolean isAdd) throws UpsertException, ResponseEntityException{
        PlaProduct pla = get2(PlaProduct.class, id);
        if (pla == null)
            throw new ResponseEntityException(120, "商品不存在");
        pla.setAdd(isAdd);
        upsert2(pla);
    }



    /**
     * @Description: 添加默认商品至指定门店
     * @Author: Chen.zm
     * @Date: 2017/12/21 0021
     */
    @CacheEvict(value = {"StoProduct"}, allEntries = true)
    public void insertStoreProduct(Integer storeId) throws UpsertException,ResponseEntityException {
        List<PlaProduct> plaProducts = getCurrentSession().createCriteria(PlaProduct.class)
                .add(Restrictions.eq("isAdd", true))
                .setFetchMode("plaProductDescription", FetchMode.JOIN)
                .list();
        for (PlaProduct pla : plaProducts) {
            StoProduct sto = new StoProduct();//门店商品
            sto.setName(pla.getName());
            sto.setVedioUrl(pla.getVedioUrl());
            sto.setRemarks(pla.getRemarks());
            sto.setPlatProductId(pla.getId());
            sto.setColligate(pla.getColligate());//排序
            sto.setCategoryId(pla.getCategoryId());
            sto.setSales(pla.getSales());//销量
            sto.setStatus(1);
            sto.setViews(pla.getViews());//浏览量
            sto.setPrice(pla.getPrice());//建议零售价(线下指导价)
            sto.setUpdateDate(pla.getUpdateDate());
            sto.setStoreId(storeId);
            sto.setBrand(pla.getBrand());
            sto.setSuplierDay(pla.getSuplierDay());//单件供货周期
            sto=upsert2(sto);

            StoProductDescription stoProductDescription = new StoProductDescription();
            stoProductDescription.setDescription(pla.getPlaProductDescription().getDescription());
            stoProductDescription.setPid(sto.getId());
            upsert2(stoProductDescription);

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
            upsert2(sto);
        }

    }



}
