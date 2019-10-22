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
public class DesignerProductManageService extends CommonService {

    @Autowired
    private UploadFileService uploadFileService;

    @Autowired
    private BaseDataService baseDataService;


    /**
     * @Description:根据id获取设计师商品详情
     * @Author: hanchao
     * @Date: 2017/10/26 0026
     */
    @Cacheable(value = {"DesignerProduct"})
    public DesignerProduct getDesignerProduct(Integer id) {
        DesignerProduct designerProduct = (DesignerProduct) getCurrentSession().createCriteria(DesignerProduct.class)
                .add(Restrictions.eq("id", id))
                .setFetchMode("DesignerProductPicture", FetchMode.JOIN)
                .uniqueResult();
        return designerProduct;
    }

    /**
     * @Description:根据平台商品id获取设计师商品详情(判断商品库商品是否已经添加到设计师商品)
     * @Author: hanchao
     * @Date: 2017/10/26 0026
     */
    @Cacheable(value = {"DesignerProduct"})
    public DesignerProduct getAdminProduct(Integer id) {
        DesignerProduct designerProduct = (DesignerProduct) getCurrentSession().createCriteria(DesignerProduct.class)
                .add(Restrictions.eq("platProductId", id))
                .uniqueResult();
        return designerProduct;
    }

    /**
     * @Description:根据id获取平台商品库商品详情
     * @Author: hanchao
     * @Date: 2017/10/26 0026
     */
    @Cacheable(value = {"PlaProduct,PlaProductLabel,PlaProductPrice,PlaProductPicture,PlaProductSaleschannel"})
    public PlaProduct getPlaProduct(Integer id) {
        PlaProduct plaProduct = (PlaProduct) getCurrentSession().createCriteria(PlaProduct.class)
                .add(Restrictions.eq("id", id))
                .setFetchMode("plaProductPictureList", FetchMode.JOIN)
                .uniqueResult();
        if(plaProduct != null) {
            for (PlaProductLabel plaProductLabel : plaProduct.getPlaProductLabelList()) {
            }
            for (PlaProductPrice plaProductPrice : plaProduct.getPlaProductPriceList()) {
            }
            for (PlaProductSaleschannel plaProductSaleschannel : plaProduct.getPlaProductSaleschannelList()) {
            }
        }
        return plaProduct;
    }


    /**
     * @Description:设计师商品上下架
     * @Author: hanchao
     * @Date: 2017/11/17 0017
     */
    @CacheEvict(value = {"DesignerProduct"}, allEntries = true)
    public void updateDesignerProductStatus(Integer id,Integer status) throws ResponseEntityException, UpsertException {
        DesignerProduct des = get2(DesignerProduct.class,"id",id);
        if (des == null) {
            throw new ResponseEntityException(120, "商品不存在");
        }
        /*0:未上架
        1:已上架*/
        if(status != null){
            if(status == 1){
                des.setStatus(0);
            }else{
                des.setStatus(1);
            }
        }
        upsert2(des);
    }

    /**
     * @Description:设计师商品删除
     * @Author: hanchao
     * @Date: 2017/11/17 0017
     */
    @CacheEvict(value = {"DesignerProduct"}, allEntries = true)
    public void updateDesignerProductStatus(Integer id) throws ResponseEntityException, UpsertException {
        DesignerProduct des = get2(DesignerProduct.class,"id",id);
        if (des == null) {
            throw new ResponseEntityException(120, "商品不存在");
        }
        des.setPlatProductId(null);
        des.setLogicDeleted(true);
        upsert2(des);
    }

    /**
     * @Description:商品库商品添加到设计师管理商品
     * @Author: hanchao
     * @Date: 2017/11/17 0017
     */
    @CacheEvict(value = {"DesignerProduct"}, allEntries = true)
    public DesignerProduct insertDesignerProducManage(IdForm form) throws UpsertException,ResponseEntityException {
        PlaProduct pla = get2(PlaProduct.class, form.id);
        if (pla == null)
            throw new ResponseEntityException(120, "商品不存在");
        DesignerProduct des = new DesignerProduct();//设计师平管理商品
        des.setName(pla.getName());
        des.setVedioUrl(pla.getVedioUrl());
        des.setBrand(pla.getBrand());
        des.setCategoryId(pla.getCategoryId());
        des.setColligate(pla.getColligate());//综合排序
        des.setSales(pla.getSales());//销量
        des.setViews(pla.getViews());//浏览量
        des.setPrice(pla.getPrice());//建议零售价
        des.setSuplierDay(pla.getSuplierDay());//单件供货周期
        des.setRemarks(pla.getRemarks());
        des.setPlatProductId(pla.getId());//PlatProductId 平台商品id
        //2017.12.07 图文详情字段隔离
//        des.setDescription(pla.getDescription());
        des.setDescription(getProductDescription(pla.getId()));

        des.setStatus(1);//商品上架状态,默认上架
        des.setUpdateDate(pla.getUpdateDate());
        des = upsert2(des);

        //商品明细
        for (PlaProductPrice plaProductPrice : pla.getPlaProductPriceList()){
            DesignerProductPrice designerProductPrice = new DesignerProductPrice();
            designerProductPrice.setPid(des.getId());
            designerProductPrice.setColor(plaProductPrice.getColor());
            designerProductPrice.setSize(plaProductPrice.getSize());
            designerProductPrice.setStock(plaProductPrice.getStock());//库存
            designerProductPrice.setOnlinePrice(plaProductPrice.getOnlinePrice());//线上销售价
            designerProductPrice.setOfflinePrice(plaProductPrice.getOfflinePrice());//线下销售价
            designerProductPrice.setRemarks(plaProductPrice.getRemarks());//规格备注
            //stoProductPrice.setPrice(plaProductPrice.getOfflinePrice());//门店销售价格
            upsert2(designerProductPrice);
        }

        //商品与图片
        des.setHref(pla.getPlaProductPictureList().size() == 0 ? "" : pla.getPlaProductPictureList().get(0).getHref());//设置主图
        boolean isMainpic = true; //标记主图
        for (PlaProductPicture plaProductPicture : pla.getPlaProductPictureList()){
            DesignerProductPicture designerProductPicture = new DesignerProductPicture();
            designerProductPicture.setHref(plaProductPicture.getHref());
            designerProductPicture.setPid(des.getId());
            designerProductPicture.setSort(1.0);
            designerProductPicture.setMainpic(false);
            if (isMainpic) {
                designerProductPicture.setMainpic(true);
            }
            isMainpic = false;
            upsert2(designerProductPicture);
        }

        //商品标签
        for (PlaProductLabel plaProductLabel : pla.getPlaProductLabelList()){
            DesignerProductLabel designerProductLabel = new DesignerProductLabel();
            designerProductLabel.setPid(des.getId());
            designerProductLabel.setLabelNmae(plaProductLabel.getLabelNmae());
            designerProductLabel.setLabelId(plaProductLabel.getLabelId());
            upsert2(designerProductLabel);
        }

        return upsert2(des);
    }


    /**
     * @Description:销售平台从商品库新增商品(筛选列表显示)
     * @Author: hanchao
     * @Date: 2017/11/17 0017
     */
    @Cacheable(value = {"PlaProduct"})
    public PageList<PlaProduct> findDesignerProductManageList(PageQuery pageQuery, List<Integer> plaProductCategory, String labelIds) {
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
        return list;
    }

    /**
     * @Description:设计师商品编辑详情保存
     * @Author: hanchao
     * @Date: 2017/11/17 0017
     */
    @CacheEvict(value = {"DesignerProduct"}, allEntries = true)
    public DesignerProduct updateDesignerProduct(DesignerProductManageForm form) throws UpsertException,ResponseEntityException {
        DesignerProduct des = get2(DesignerProduct.class, "id", form.id);
        if (des == null)
            throw new ResponseEntityException(120, "商品不存在");
        if (StringUtils.isNotBlank(form.vedioUrl)) {
            des.setVedioUrl(form.vedioUrl);
        }
        des.setName(form.name);
        des.setBrand(form.brand);
        des.setCategoryId(form.categoryId);
        /*货号设为只读状态,不用保存*/
        /*sal.getPlaProduct().setPno(sal.getPlaProduct() == null ? "" : sal.getPlaProduct().getPno());*/
        des.setColligate(form.colligate);//综合排序
        des.setSales(form.sales);
        des.setViews(form.views);
        des.setStatus(form.status);//状态,1上架,0下架
        des.setRemarks(form.remarks);//详情备注
        /*sal.setPlatProductId(form.platProductId);//平台id*/
        des.setDescription(form.description);
        des = upsert2(des);

        //商品明细
        String hql = "delete DesignerProductPrice where pid = :pid";
        getCurrentSession().createQuery(hql).setInteger("pid", form.id).executeUpdate();
        for (DesignerProductManagePriceListForm designerProductPriceList : form.designerProductPriceList){
            DesignerProductPrice designerProductPrice = new DesignerProductPrice();
            designerProductPrice.setPid(des.getId());
            designerProductPrice.setColor(designerProductPriceList.color);
            designerProductPrice.setSize(designerProductPriceList.size);
            designerProductPrice.setOfflinePrice(designerProductPriceList.offlinePrice);
            designerProductPrice.setStock(designerProductPriceList.stock);
            designerProductPrice.setRemarks(designerProductPriceList.specRemark);
            upsert2(designerProductPrice);
        }

        //清除之前的标签
        hql = "delete DesignerProductLabel where pid = :pid";
        getCurrentSession().createQuery(hql).setInteger("pid", form.id).executeUpdate();
        for (DesignerProductManageLabelListForm designerProductLabelList : form.designerProductLabelList){
            DesignerProductLabel designerProductLabel = new DesignerProductLabel();
            designerProductLabel.setLabelId(designerProductLabelList.labelId);
            designerProductLabel.setLabelNmae(designerProductLabelList.labelName);
            designerProductLabel.setPid(des.getId());
            upsert2(designerProductLabel);
        }

        //商品与图片
        des.setHref(form.designerProductPictureList.size() == 0 ? "" : form.designerProductPictureList.get(0).href);
        //清除原先的图片
        hql = "delete DesignerProductPicture where pid = :pid";
        getCurrentSession().createQuery(hql).setInteger("pid", form.id).executeUpdate();
        boolean isMainpic = true; //标记主图
        for (DesignerProductManagePictureListForm designerProductPictureList : form.designerProductPictureList){
            DesignerProductPicture designerProductPicture = new DesignerProductPicture();
            designerProductPicture.setHref(designerProductPictureList.href);
            designerProductPicture.setPid(des.getId());
            designerProductPicture.setSort(1.0);
            designerProductPicture.setMainpic(false);
            if (isMainpic) {
                designerProductPicture.setMainpic(true);
            }
            isMainpic = false;
            upsert2(designerProductPicture);
        }

        return upsert2(des);
    }

    /**
     * @Description:设计师管理的商品列表
     * @Author: hanchao
     * @Date: 2017/11/16 0016
     */
    @Cacheable(value = {"DesignerProduct"})
    public PageList<DesignerProduct> findDesignerProductList(PageQuery pageQuery, String name, Integer categoryId) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(name))
            cri = Restrictions.and(cri, Restrictions.like("name", name, MatchMode.ANYWHERE));
        if (categoryId != null)
            cri = Restrictions.and(cri, Restrictions.in("categoryId", baseDataService.getAllCategoryIdByParentId(categoryId)));
        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "plaProductCategory";
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<DesignerProduct> list = hibernateReadonlyRepository.getList(DesignerProduct.class, pageQuery);
        for (DesignerProduct designerProduct : list) {
            for (DesignerProductPrice designerProductPrice : designerProduct.getDesignerProductPriceList()) {}
        }
        return list;
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

}
