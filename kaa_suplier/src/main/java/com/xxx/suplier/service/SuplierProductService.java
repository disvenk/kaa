package com.xxx.suplier.service;


import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.*;
import com.xxx.suplier.form.*;
import com.xxx.user.service.UploadFileService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SuplierProductService extends CommonService {

    @Autowired
    private ProductBaseService productBaseService;
    @Autowired
    private ProductProcedureService productProcedureService;

    /**
     * @Description: 获取供应商商品列表
     * @Author: Chen.zm
     * @Date: 2017/11/3 0003
     */
    public PageList<SupProduct> findSupProductList(PageQuery pageQuery, Integer suplierId, String pno, String name) {
        Criterion cri = Restrictions.eq("suplierId", suplierId);
        if (StringUtils.isNotBlank(pno))
            cri = Restrictions.and(cri, Restrictions.like("pno", pno, MatchMode.ANYWHERE));
        if (StringUtils.isNotBlank(name))
            cri = Restrictions.and(cri, Restrictions.like("name", name, MatchMode.ANYWHERE));
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<SupProduct> list = hibernateReadonlyRepository.getList(SupProduct.class, pageQuery);
        for (SupProduct supProduct : list) {
            for (SupProductPrice price : supProduct.getSupProductPriceList()) {
            }
        }
        return list;
    }


    /**
     * @Description: 获取供应商商量详情
     * @Author: Chen.zm
     * @Date: 2017/11/3 0003
     */
    public SupProduct findSupProductDateil(Integer suplierId, Integer id, String pno) {
        Criterion cri = Restrictions.eq("suplierId", suplierId);
        if (StringUtils.isNotBlank(pno))
            cri = Restrictions.and(cri, Restrictions.eq("pno", pno));
        if (id != null)
            cri = Restrictions.and(cri, Restrictions.eq("id", id));

        SupProduct supProduct = (SupProduct) getCurrentSession().createCriteria(SupProduct.class)
                .add(cri)
                .add(Restrictions.eq("logicDeleted", false))
//                .setFetchMode("supProductPriceList", FetchMode.JOIN)
                .uniqueResult();
        return supProduct;
    }

    /**
     * @Description: 逻辑删除供应商商品
     * @Author: Chen.zm
     * @Date: 2017/11/4 0004
     */
    public void removeProduct(Integer id, Integer suplierId) throws UpsertException,ResponseEntityException {
        SupProduct supProduct = get2(SupProduct.class, "id", id, "suplierId", suplierId);
        if (supProduct == null)
            throw new ResponseEntityException(120, "商品不存在");
        supProduct.setLogicDeleted(true);
        upsert2(supProduct);
    }

    /**
     * @Description: 获取供应商商品原材料
     * @Author: Chen.zm
     * @Date: 2018/1/8 0008
     */
    public List<SupProductMaterial> findSupProductMaterialList(Integer pid) {
        Criterion cri = Restrictions.eq("pid", pid);
        List<SupProductMaterial> list = getCurrentSession().createCriteria(SupProductMaterial.class)
                .add(cri)
                .list();
        return list;
    }


    /**
     * @Description: 获取供应商商品工序
     * @Author: Chen.zm
     * @Date: 2018/1/8 0008
     */
    public List<SupProductProcedure> findSupProductProcedureList(Integer pid) {
        Criterion cri = Restrictions.eq("pid", pid);
        List<SupProductProcedure> list = getCurrentSession().createCriteria(SupProductProcedure.class)
                .add(cri)
                .list();
        return list;
    }


    /**
     * @Description: 保存供应商商品信息
     * @Author: Chen.zm
     * @Date: 2017/11/10 0010
     */
    public void saveSuplierProduct(Integer supplierId, SuplierProductForm form) throws UpsertException,ResponseEntityException {
        SupProduct product;
        if (form.id == null) { //处理商品id为空的情况
            product = new SupProduct();
            product.setSuplierId(supplierId);
            product.setUpdateDate(new Date());
        } else {
            product = get2(SupProduct.class, "id", form.id, "suplierId", supplierId);
            if (product == null)
                throw new ResponseEntityException(120, "商品不存在");
        }
        //校验货号是否存在
        SupProduct sup = get2(SupProduct.class, "pno",form.pno);
        if (sup != null && sup.getId().intValue() != form.id)
            throw new ResponseEntityException(130, "供应商商品编号已存在");
        product.setName(form.name);
        product.setPno(form.pno);
        product.setCategoryId(form.categoryId);
        product.setMaterial(form.material);
        product.setTechnics(form.technics);
        product.setDescription(form.description);
        product.setShoulder(form.shoulder);
        product.setBust(form.bust);
        product.setWaist(form.waist);
        product.setHipline(form.hipline);
        product.setHeight(form.height);
        product.setWeight(form.weight);
        product.setThroatheight(form.throatheight);
        product.setPrice(form.price);
        product.setRemarks(form.remarks);
        product.setHref(form.productPictureList.size() != 0 ? form.productPictureList.get(0).href : "");
        product = upsert2(product);

        //保存图片
        String hq = "delete SupProductPicture where pid =:pid";
        getCurrentSession().createQuery(hq).setInteger("pid", product.getId()).executeUpdate();
        for (ProductPictureForm pictureForm : form.productPictureList) {
            SupProductPicture picture = new SupProductPicture();
            picture.setPid(product.getId());
            picture.setHref(pictureForm.href);
            picture.setMainpic(false);
            if (product.getHref() == pictureForm.href) {
                picture.setMainpic(true);
            }
            upsert2(picture);
        }

        //保存颜色尺寸
        String hql = "delete SupProductPrice where pid =:pid";
        getCurrentSession().createQuery(hql).setInteger("pid", product.getId()).executeUpdate();
        for (ProductPriceForm priceForm : form.productPriceList) {
            SupProductPrice productPrice = new SupProductPrice();
            productPrice.setPid(product.getId());
            productPrice.setColorId(priceForm.colorId);
            productPrice.setSizeId(priceForm.sizeId);
            upsert2(productPrice);
        }

        //保存原材料
        hql = "delete SupProductMaterial where pid =:pid";
        getCurrentSession().createQuery(hql).setInteger("pid", product.getId()).executeUpdate();
        for (ProductMaterialForm materialForm : form.materialList) {
            //校验原材料是否存在
            if (materialForm.materialId == null) {
                CategorySaveForm f = new CategorySaveForm();
                f.name = materialForm.materialName;
                f.type = 4;
                materialForm.materialId = productBaseService.addProductBase(f).getId();
            }

            SupProductMaterial material = new SupProductMaterial();
            material.setPid(product.getId());
            material.setMaterialId(materialForm.materialId);
            material.setPrice(materialForm.price);
            material.setCount(materialForm.count);
            material.setUnit(materialForm.unit);
            upsert2(material);
        }

        //保存工序
        hql = "delete SupProductProcedure where pid =:pid";
        getCurrentSession().createQuery(hql).setInteger("pid", product.getId()).executeUpdate();
        for (ProductProcedureForm procedureForm : form.procedureList) {
            //校验工序是否存在
            if (procedureForm.procedureId == null) {
                ProcedureSaveForm f = new ProcedureSaveForm();
                f.name = procedureForm.procedureName;
                f.price = procedureForm.price == null ? null : procedureForm.price.toString();
                procedureForm.procedureId = productProcedureService.addProductProcedure(f).getId();
            }
            SupProductProcedure procedure = new SupProductProcedure();
            procedure.setPid(product.getId());
            procedure.setProcedureId(procedureForm.procedureId);
            procedure.setPrice(procedureForm.price);
            upsert2(procedure);
        }
    }

}
