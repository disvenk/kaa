package com.xxx.admin.service;


import com.alibaba.fastjson.JSONArray;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.ExtFilter;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.user.dao.AccountDao;
import com.xxx.user.service.UploadFileService;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.MD5Utils;
import com.xxx.admin.form.*;
import com.xxx.model.business.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class SuplierService extends CommonService {

    @Autowired
    private UploadFileService uploadFileService;
    @Autowired
    private AccountDao accountDao;

    /**
     * @Description:根据id获取供应商详情
     * @Author: hanchao
     * @Date: 2017/11/2 0002
     */
    @Cacheable(value = {"SupSuplier"})
    public SupSuplier getSupSuplier(Integer id) {
        SupSuplier supSuplier = (SupSuplier) getCurrentSession().createCriteria(SupSuplier.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
        return supSuplier;
    }

    /**
     * @Description:根据id获取商品库商品详情
     * @Author: hanchao
     * @Date: 2017/11/27 0027
     */
    @Cacheable(value = {"PlaProduct"})
    public PlaProduct getPlaProduct(Integer id) {
        PlaProduct plaProduct = (PlaProduct) getCurrentSession().createCriteria(PlaProduct.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
        return plaProduct;
    }

    /**
     * @Description:供货价货品根据商品id带出数据
     * @Author: hanchao
     * @Date: 2017/11/29 0029
     */
    @Cacheable(value = {"PlaProduct"})
    public PlaProduct getPlaProductByProductCode(String productCode) {
        PlaProduct plaProduct = (PlaProduct) getCurrentSession().createCriteria(PlaProduct.class)
                .add(Restrictions.eq("productCode", productCode))
                .uniqueResult();
        return plaProduct;
    }

    /**
     * @Description:根据id获取供应商货品详情
     * @Author: hanchao
     * @Date: 2017/11/3 0003
     */
    @Cacheable(value = {"SupProduct"})
    public SupProduct getSupProduct(Integer id) {
        SupProduct supProduct = (SupProduct) getCurrentSession().createCriteria(SupProduct.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
        return supProduct;
    }

    /**
     * @Description:根据id获取审核历史
     * @Author: hanchao
     * @Date: 2017/11/8 0008
     */
    @Cacheable(value = {"SupApproveLog"})
    public List<SupApproveLog> getSupApproveLog(Integer id) {
        List<SupApproveLog> supProduct = (List<SupApproveLog>) getCurrentSession().createCriteria(SupApproveLog.class)
                .add(Restrictions.eq("suplierId", id))
                .list();
        return supProduct;
    }

    /**
     * @Description:重置密码
     * @Author: hanchao
     * @Date: 2017/11/17 0017
     */
    @CacheEvict(value = {"SupSuplier"}, allEntries = true)
    public PubUserLogin updateSuplierPassword(Integer id, String password) throws UpsertException, ResponseEntityException {
        SupSuplier supSuplier = get2(SupSuplier.class, id);
        if (supSuplier == null)
            throw new ResponseEntityException(120, "此供应商不存在");
        PubUserLogin login = supSuplier.getPubUserLogin();
        login.setUserPassword(password);
        return upsert2(login);
    }

    /**
     * @Description:供应商商品删除
     * @Author: hanchao
     * @Date: 2017/11/3 0003
     */
    @CacheEvict(value = {"SupProduct"}, allEntries = true)
    public void updateSuplierGoodsId(Integer id) throws UpsertException {
        SupProduct sup = getSupProduct(id);
        sup.setLogicDeleted(true);
        upsert2(sup);
    }

    /**
     * @Description:供货价批量导入时的去重处理
     * @Author: hanchao
     * @Date: 2017/11/30 0030
     */
    /*@Cacheable(value = {"PlaProduct"})
    public PlaProduct selectPlaproductFormPno(String pno) throws ResponseEntityException{
        Criterion cri = Restrictions.eq("pno", pno);
        return (PlaProduct) getCurrentSession().createCriteria(PlaProduct.class)
                .add(cri)
                .uniqueResult();
    }*/


    /**
     * @Description:供货价批量导入
     * @Author: hanchao
     * @Date: 2017/11/29 0029
     */
    @CacheEvict(value = {"PlaProduct"}, allEntries = true)
    public void saveBath(List<String[]> list) throws UpsertException,ResponseEntityException {
        for (String[] strings : list) {
           if(strings[0] != null){
               PlaProduct plaProduct = selectAddPlaproductPno(strings[0],strings[2]);
               if (plaProduct != null)
                   throw new ResponseEntityException(120, "批量导入中有重复货号,请重新导入");

               PlaProduct pla = (PlaProduct) getCurrentSession().createCriteria(PlaProduct.class)
                       .add(Restrictions.eq("productCode", strings[0]))
                       .uniqueResult();
               if(pla == null){
                   continue;
               }else{
                   SupSuplier sup = (SupSuplier) getCurrentSession().createCriteria(SupSuplier.class)
                           .add(Restrictions.eq("code", strings[1]))
                           .uniqueResult();
                   if(sup == null){
                       continue;
                   }else{
                       pla.setSuplierId(sup.getId());
                       pla.setPno(strings[2]);//供应商产品编号
            /*pla.setSuplierPrice(Double.parseDouble(strings[3]));  //这种方法会导致价格出现如下数字：16.399999999999999  77.900000000000006*/
                       pla.setSuplierPrice(Double.parseDouble(strings[3]));

                       BigDecimal bds = new BigDecimal(strings[4]);
                       bds = bds.setScale(1,BigDecimal.ROUND_HALF_UP);
                       String suplierDay = bds.toString();
                       pla.setSuplierDay(Double.parseDouble(suplierDay.substring(0,suplierDay.lastIndexOf("."))));
                       pla.setSupplierRemarks(strings[5]);
                    /*if("0".equals(suplierDay.substring(suplierDay.length()-1))){ //如果最后一位是0  如 26.0  33.0 就把 .0 删除
                        String NewsuplierDay = suplierDay.substring(0, suplierDay.length()-2);
                        pla.setSuplierDay(Double.parseDouble(NewsuplierDay));
                    }else{
                        pla.setSuplierPrice(Double.parseDouble(suplierDay));
                    }*/
                       upsert2(pla);
                   }

               }
           }
        }
    }


    /**
     * @Description:供应商用户批量删除
     * @Author: hanchao
     * @Date: 2017/11/16 0016
     */
    @CacheEvict(value = {"SupSuplier"}, allEntries = true)
    public void updateMoreSuplier(List<Integer> ids) throws UpsertException {
        String hql = "update SupSuplier set LogicDeleted = 1 where id in :ids";
        getCurrentSession().createQuery(hql).setParameterList("ids", ids).executeUpdate();
    }

    /**
     * @Description:供应商商品删除
     * @Author: hanchao
     * @Date: 2017/11/3 0003
     */
    @CacheEvict(value = {"SupSuplier"}, allEntries = true)
    public void updateSupliertId(Integer id) throws UpsertException {
        SupSuplier sup = getSupSuplier(id);
        sup.setLogicDeleted(true);
        upsert2(sup);
    }

    /**
     * @Description:供货价商品删除
     * @Author: hanchao
     * @Date: 2017/11/3 0003
     */
    @CacheEvict(value = {"PlaProduct"}, allEntries = true)
    public void updateSuplierGoodsListId(Integer id) throws UpsertException {
        PlaProduct pla = getPlaProduct(id);
        pla.setLogicDeleted(true);
        upsert2(pla);
    }

    /**
     * @Description:供货价货品列表新增
     * @Author: hanchao
     * @Date: 2017/11/27 0027
     */
    @CacheEvict(value = {"PlaProduct"}, allEntries = true)
    public PlaProduct insertSuplierGoodsList(SuplierManageGoodsListForm form) throws UpsertException,ResponseEntityException {
        PlaProduct pla = (PlaProduct) getCurrentSession().createCriteria(PlaProduct.class)
                .add(Restrictions.eq("productCode", form.productCode))
                .uniqueResult();
        pla.setSupplierRemarks(form.Remark);//商品备注
        pla.setPno(form.pno);
        pla.setSuplierPrice(form.suplierPrice);
        pla.setSuplierDay(form.suplierDay);
        pla.setSuplierId(form.SupplierId);
        pla = upsert2(pla);
        return pla;
    }

    /**
     * @Description:供货价货品列表编辑保存的货号去重处理
     * @Author: hanchao
     * @Date: 2017/11/29 0029
     */
    @Cacheable(value = {"PlaProduct"})
    public PlaProduct selectPlaproductPno(Integer id,String pno) throws ResponseEntityException{
        Criterion cri = Restrictions.ne("id", id);
        cri = Restrictions.and(cri, Restrictions.eq("pno", pno));
        return (PlaProduct) getCurrentSession().createCriteria(PlaProduct.class)
                .add(cri)
                .uniqueResult();
    }
    /**
     * @Description:供货价货品列表新增时的货号去重处理
     * @Author: hanchao
     * @Date: 2017/11/29 0029
     */
    @Cacheable(value = {"PlaProduct"})
    public PlaProduct selectAddPlaproductPno(String productCode,String pno) throws ResponseEntityException{
        Criterion cri = Restrictions.ne("productCode", productCode);
        cri = Restrictions.and(cri, Restrictions.eq("pno", pno));
        return (PlaProduct) getCurrentSession().createCriteria(PlaProduct.class)
                .add(cri)
                .uniqueResult();
    }


    /**
     * @Description:供货价货品列表编辑保存
     * @Author: hanchao
     * @Date: 2017/11/27 0027
     */
    @CacheEvict(value = {"PlaProduct"}, allEntries = true)
    public PlaProduct updateSuplierGoodsList(SuplierManageGoodsListForm form) throws UpsertException,ResponseEntityException {
        PlaProduct pla = get2(PlaProduct.class, form.id);
        if (pla == null)
            throw new ResponseEntityException(120, "商品不存在");
        pla.setSupplierRemarks(form.Remark);//商品备注
        pla.setSuplierPrice(form.suplierPrice);
        pla.setSuplierDay(form.suplierDay);
        pla.setSuplierId(form.SupplierId);
        pla.setPno(form.pno);
        pla = upsert2(pla);
        return pla;
    }

    /**
     * @Description:供货价货品列表编辑数据显示
     * @Author: hanchao
     * @Date: 2017/11/27 0027
     */
    @Cacheable(value = {"PlaProduct"})
    public PageList<PlaProduct> findSuplierGoodsList(PageQuery pageQuery, String name, String productCode, String suplierName) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(name))
            cri = Restrictions.and(cri, Restrictions.like("name", name, MatchMode.ANYWHERE));
        if (StringUtils.isNotBlank(productCode))
            cri = Restrictions.and(cri, Restrictions.like("productCode",productCode, MatchMode.ANYWHERE));
        if (StringUtils.isNotBlank(suplierName)) {
            ExtFilter filter = new ExtFilter("supSuplier.name", "string", suplierName, ExtFilter.ExtFilterComparison.like, null);
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(filter);
            pageQuery.filter = jsonArray.toJSONString();
        }
        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "plaProductCategory";
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<PlaProduct> list = hibernateReadonlyRepository.getList(PlaProduct.class, pageQuery);
        for (PlaProduct plaProduct : list) {
            for (PlaProductPrice plaProductPrice : plaProduct.getPlaProductPriceList()){}

        }
        return list;
    }

    /**
     * @Description:货品管理详情编辑数据新增
     * @Author: hanchao
     * @Date: 2017/11/3 0003
     */
    @CacheEvict(value = {"SupProduct"}, allEntries = true)
    public SupProduct insertSuplierGoods(SuplierManageGoodsForm form) throws UpsertException, ResponseEntityException, java.text.ParseException {
        SupProduct sup = new SupProduct();
//        sup.setName(form.name);//商品名称
//        sup.setCategoryId(form.categoryId);
//        sup.setPno(form.pno);
//        sup.setStatus(1);
//        sup.setSuplierId(form.productSupplierId);
//        sup.setSort(form.sort);//货品排序
//        sup.setRemarks(form.suplierRemark);//商品备注
//        sup.setHref(form.productPictureList.size() == 0 ? "" : form.productPictureList.get(0).href);
//        sup.setBrand(form.brand);
//        sup.setSuplierDay(form.suplierDay);
//        sup.setUpdateDate(DateTimeUtils.parseDate(form.updateDate,"yyyy-MM-dd HH:mm:ss"));
//        sup.setMaterial(form.material);
//        sup = upsert2(sup);
//        boolean isMainpic = true; //标记主图
//        for (ProductPictureListForm productPicture : form.productPictureList) {
//            SupProductPicture supProductPicture = new SupProductPicture();
//            supProductPicture.setHref(productPicture.href);
//            supProductPicture.setPid(sup.getId());
//            supProductPicture.setSort(1.0);
//            supProductPicture.setMainpic(false);
//            if (isMainpic) {
//                supProductPicture.setMainpic(true);
//            }
//            isMainpic = false;
//            upsert2(supProductPicture);
//        }
//
//        //供应商商品
//        for (SupplierGoodsListForm supplierGoodsListForm : form.productSupplierList){
//            SupProductPrice supProductPrice = new SupProductPrice();
//            supProductPrice.setPid(sup.getId());
//            supProductPrice.setColor(supplierGoodsListForm.color);
//            supProductPrice.setSize(supplierGoodsListForm.size);
//            supProductPrice.setRemarks(supplierGoodsListForm.categoryRemark);
//            sup.setStock(supplierGoodsListForm.stock);
//            sup.setPrice(supplierGoodsListForm.offlinePrice);
//            upsert2(supProductPrice);
//        }
        return sup;
    }


    /**
     * @Description:货品管理详情编辑数据保存
     * @Author: hanchao
     * @Date: 2017/11/3 0003
     */
    @CacheEvict(value = {"SupSuplier"}, allEntries = true)
    public SupProduct updateSuplierGoods(SuplierManageGoodsForm form) throws UpsertException,ResponseEntityException {
        SupProduct sup = get2(SupProduct.class, form.id);
        if (sup == null)
            throw new ResponseEntityException(120, "供应商商品不存在");
//        sup.setName(form.name);//商品名称
//        sup.setCategoryId(form.categoryId);
//        sup.setPno(form.pno);
//        sup.setSort(form.sort);
//        sup.setRemarks(form.suplierRemark);//商品备注
//        sup.setHref(form.productPictureList.size() == 0 ? "" : form.productPictureList.get(0).href);
//        sup.setBrand(form.brand);
//        sup.setSuplierDay(form.suplierDay);
//        sup.setSuplierId(form.productSupplierId);
//        sup.setMaterial(form.material);
//        sup = upsert2(sup);
//        //清除原先的图片
//        String hql = "delete SupProductPicture where pid =:pid";
//        getCurrentSession().createQuery(hql).setInteger("pid", sup.getId()).executeUpdate();
//        boolean isMainpic = true; //标记主图
//        for (ProductPictureListForm productPicture : form.productPictureList) {
//            SupProductPicture supProductPicture = new SupProductPicture();
//            supProductPicture.setHref(productPicture.href);
//            supProductPicture.setPid(sup.getId());
//            supProductPicture.setSort(1.0);
//            supProductPicture.setMainpic(false);
//            if (isMainpic) {
//                supProductPicture.setMainpic(true);
//            }
//            isMainpic = false;
//            upsert2(supProductPicture);
//        }
//
//        //供应商商品
//        //删除之前的记录
//        hql = "delete SupProductPrice where pid =:pid";
//        getCurrentSession().createQuery(hql).setInteger("pid", sup.getId()).executeUpdate();
//        for (SupplierGoodsListForm supplierGoodsListForm : form.productSupplierList){
//            SupProductPrice supProductPrice = new SupProductPrice();
//            supProductPrice.setPid(sup.getId());
//            supProductPrice.setRemarks(supplierGoodsListForm.categoryRemark);
//            supProductPrice.setColor(supplierGoodsListForm.color);
//            supProductPrice.setSize(supplierGoodsListForm.size);
//            sup.setStock(supplierGoodsListForm.stock);
//            sup.setPrice(supplierGoodsListForm.offlinePrice);
//            upsert2(supProductPrice);
//        }
        return sup;
    }


    /**
     * @Description:货品管理列表显示
     * @Author: hanchao
     * @Date: 2017/11/3 0003
     * 11.13 steven 修改bug: 分类，要取得本分类及子分类资料。
     */
    @Cacheable(value = {"SupProduct"})
    public PageList<SupProduct> findgoodsManageList(PageQuery pageQuery, String name, Integer categoryId,String suplierName) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(name))
            cri = Restrictions.and(cri, Restrictions.like("name", name, MatchMode.ANYWHERE));
        if (categoryId != null)
            cri = Restrictions.and(cri, Restrictions.in("categoryId", getAllCategory(categoryId)));
        if (StringUtils.isNotBlank(suplierName)) {
            ExtFilter filter = new ExtFilter("supSuplier.name", "string", suplierName, ExtFilter.ExtFilterComparison.like, null);
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(filter);
            pageQuery.filter = jsonArray.toJSONString();
        }
//        if(StringUtils.isNotBlank(suplierName))
//            cri = Restrictions.and(cri, Restrictions.like("suplierName", suplierName,MatchMode.ANYWHERE));
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<SupProduct> list = hibernateReadonlyRepository.getList(SupProduct.class, pageQuery);
        for (SupProduct supProduct : list) {
            for (SupProductPrice supProductPrice : supProduct.getSupProductPriceList()){}
        }
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
     * @Description:供应商审核通过/不通过
     * @Author: hanchao
     * @Date: 2017/11/3 0003
     */
    @CacheEvict(value = {"SupSuplier"}, allEntries = true)
    public SupSuplier updateApproveStatus(SuplierManageEditListForm form) throws UpsertException,ResponseEntityException {
        SupSuplier sup = get2(SupSuplier.class, form.id);
        if (sup == null)
            throw new ResponseEntityException(120, "供应商不存在");
            sup.setCode(form.code);
        SupApproveLog supApproveLog = new SupApproveLog();
        if (form.approveStatus == 1){
            supApproveLog.setSuplierId(sup.getId());
            supApproveLog.setDescription(form.explains);//说明
            supApproveLog.setOperate(form.operate);
            supApproveLog.setApproveDate(sup.getUpdateDate());
        }else if(form.approveStatus == 2){
            supApproveLog.setSuplierId(sup.getId());
            supApproveLog.setDescription(form.explains);//说明
            supApproveLog.setOperate(form.operate);
            supApproveLog.setApproveDate(sup.getUpdateDate());
        }
        sup.setApproveStatus(form.approveStatus);
        upsert2(supApproveLog);
        return upsert2(sup);
    }



     /**
     * @Description:供应商后台编辑判断手机号是否重复
     * @Author: hanchao
     * @Date: 2017/11/15 0015
     */
     @Cacheable(value = {"SupSuplier"})
     public SupSuplier selectMobile(Integer id,String mobile) throws ResponseEntityException{
         Criterion cri = Restrictions.ne("id", id);
         cri = Restrictions.and(cri, Restrictions.eq("contactPhone", mobile));
         SupSuplier supSuplier =  (SupSuplier) getCurrentSession().createCriteria(SupSuplier.class)
                 .add(cri)
                 .uniqueResult();
         return supSuplier;
     }
     /**
     * @Description:供应商后台编辑判断供应商编号是否重复
     * @Author: hanchao
     * @Date: 2017/11/15 0015
     */
     @Cacheable(value = {"SupSuplier"})
     public SupSuplier selectCode(Integer id,String code) throws ResponseEntityException{
         Criterion cri = Restrictions.ne("id", id);
         cri = Restrictions.and(cri, Restrictions.eq("code", code));
         return (SupSuplier) getCurrentSession().createCriteria(SupSuplier.class)
                 .add(cri)
                 .uniqueResult();
     }

    /**
     * @Description:供应商后台新增判断手机号是否重复
     * @Author: hanchao
     * @Date: 2017/11/15 0015
     */
    @Cacheable(value = {"PubUserBase"})
    public SupSuplier selectMobile(String mobile){
        Criterion cri = Restrictions.eq("contactPhone", mobile);
        return  (SupSuplier) getCurrentSession().createCriteria(SupSuplier.class)
                .add(cri)
                .uniqueResult();
    }

    /**
     * @Description:供应商货品后台新增判断货号是否重复
     * @Author: hanchao
     * @Date: 2017/11/15 0015
     */
    @Cacheable(value = {"SupProduct"})
    public SupProduct selectPno(String pno){
        Criterion cri = Restrictions.eq("pno", pno);
        return  (SupProduct) getCurrentSession().createCriteria(SupProduct.class)
                .add(cri)
                .uniqueResult();
    }

    /**
     * @Description:供应商货品后台编辑判断货号是否重复
     * @Author: hanchao
     * @Date: 2017/11/15 0015
     */
    @Cacheable(value = {"SupProduct"})
    public SupProduct selectPno(Integer id,String pno) throws ResponseEntityException{
        Criterion cri = Restrictions.ne("id", id);
        cri = Restrictions.and(cri, Restrictions.eq("pno", pno));
        return (SupProduct) getCurrentSession().createCriteria(SupProduct.class)
                .add(cri)
                .uniqueResult();
    }

    /**
     * @Description:供应商后台新增判断供应商编号是否重复
     * @Author: hanchao
     * @Date: 2017/11/15 0015
     */
    @Cacheable(value = {"SupSuplier"})
    public SupSuplier selectCode(String code){
        Criterion cri = Restrictions.eq("code", code);
        return  (SupSuplier) getCurrentSession().createCriteria(SupSuplier.class)
                .add(cri)
                .uniqueResult();
    }

    /**
     * @Description:供应商管理后台新增
     * @Author: hanchao
     * @Date: 2017/11/2 0002
     */
    @CacheEvict(value = {"SupSuplier"}, allEntries = true)
    public SupSuplier insertSuplier(SuplierManageEditListForm form) throws UpsertException,ResponseEntityException {
        SupSuplier sup = new SupSuplier();
        sup.setName(form.name);//供应商名称
        sup.setApproveStatus(form.approveStatus);//默认是待审核0
        sup.setCode(form.code);//供应商编号
        sup.setAddress(form.address);
        sup.setContactPhone(form.mobile);//电话,与用户基本表数据重复!!
        sup.setContact(form.contact);//联系人姓名
        if (StringUtils.isNotBlank(form.qualifications)) {
            sup.setQualifications(uploadFileService.saveOssUploadFileByBase64(form.qualifications).toString());//资质信息
        }
        sup.setDescription(form.description);//公司介绍
        sup.setModelSet(form.modelSet);//模架
        sup.setEditer(form.editer);//版型师
        sup.setSewer(form.sewer);//裁剪
        sup.setSmith(form.smith);//车工
        sup.setOpenYears(form.openYears);
        sup.setScope(form.scope);//主营业务
        sup = upsert2(sup);

        //初始化供应商信息
        accountDao.supplierProductInit(sup.getId());

        PubUserLogin pubUserLogin = new PubUserLogin();
        pubUserLogin.setUserPassword(MD5Utils.md5Hex("888888"));//默认密码
        pubUserLogin.setLoginType(3);//默认供应商入口为3
        pubUserLogin.setRelationId(sup.getId());//供应商id
        pubUserLogin.setUserCode(form.mobile);//登录用户名默认为手机号
        pubUserLogin.setUseable(true);//默认该用户状态可用
        pubUserLogin = upsert2(pubUserLogin);
        sup.setUserId(pubUserLogin.getId());

        PubUserBase pubUserBase = new PubUserBase();
        pubUserBase.setUserId(pubUserLogin.getId());
        if (StringUtils.isNotBlank(form.icon)) {
            pubUserBase.setIcon(uploadFileService.saveOssUploadFileByBase64(form.icon).toString());
        }
        pubUserBase.setMobile(form.mobile);//电话
        pubUserBase.setName(form.contact);//联系人姓名
        pubUserBase.setPersonID(form.personID);//身份证
        pubUserBase.setSex(form.sex);
        upsert2(pubUserBase);

        SupApproveLog supApproveLog = new SupApproveLog();
        supApproveLog.setApproveDate(sup.getCreatedDate());//审核时间
        supApproveLog.setSuplierId(sup.getId());
        supApproveLog.setDescription(sup.getExplains());//说明
        supApproveLog.setOperate("提交审核");
        upsert2(supApproveLog);
        return sup;
    }

    /**
     * @Description:供应商管理后台用户保存
     * @Author: hanchao
     * @Date: 2017/11/2 0002
     */
    @CacheEvict(value = {"SupSuplier"}, allEntries = true)
    public SupSuplier updateSuplier(SuplierManageEditListForm form) throws UpsertException,ResponseEntityException {
        SupSuplier sup = get2(SupSuplier.class, form.id);
        if (sup == null)
            throw new ResponseEntityException(120, "供应商不存在");
        sup.setName(form.name);//供应商名称
        sup.setApproveStatus(form.approveStatus);
        sup.setCode(form.code);//供应商编号
        sup.setAddress(form.address);
        sup.setContactPhone(form.mobile);//电话,与用户基本表数据重复!!
        sup.setContact(form.contact);//联系人姓名
        if(StringUtils.isBlank(form.qualifications)){
            sup.setQualifications("");
        }else{
            sup.setQualifications(uploadFileService.saveOssUploadFileByBase64(form.qualifications).toString());//资质信息
        }
        sup.setDescription(form.description);//公司介绍
        sup.setModelSet(form.modelSet);//模架
        sup.setEditer(form.editer);//版型师
        sup.setSewer(form.sewer);//裁剪
        sup.setSmith(form.smith);//车工
        sup.setOpenYears(form.openYears);
        sup.setScope(form.scope);//主营业务
        sup = upsert2(sup);
        //清除之前的用户信息记录
        String hql = "delete PubUserBase where userId =:userId";
        getCurrentSession().createQuery(hql).setInteger("userId", sup.getUserId()).executeUpdate();
        PubUserBase pubUserBase = new PubUserBase();
        if(StringUtils.isBlank(form.icon)){
            pubUserBase.setIcon("");
        }else{
            pubUserBase.setIcon(uploadFileService.saveOssUploadFileByBase64(form.icon).toString());
        }
        pubUserBase.setMobile(form.mobile);//电话
        pubUserBase.setUserId(sup.getUserId());
        pubUserBase.setName(form.contact);//联系人姓名
        pubUserBase.setPersonID(form.personID);//身份证
        pubUserBase.setSex(form.sex);
        upsert2(pubUserBase);

        SupApproveLog supApproveLog = new SupApproveLog();
        supApproveLog.setApproveDate(sup.getCreatedDate());//审核时间
        supApproveLog.setSuplierId(sup.getId());
        supApproveLog.setDescription(sup.getExplains());//说明
        supApproveLog.setOperate("提交审核");
        upsert2(supApproveLog);

        return sup;
    }



    /**
     * @Description:供应商管理列表显示
     * @Author: hanchao
     * @Date: 2017/11/2 0002
     */
    @Cacheable(value = {"SupSuplier"})
    public PageList<SupSuplier> findSupSuplierList(PageQuery pageQuery, String name, Integer approveStatus) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(name))
            cri = Restrictions.and(cri, Restrictions.like("name", name, MatchMode.ANYWHERE));
        if (approveStatus != null)
            cri = Restrictions.and(cri, Restrictions.eq("approveStatus", approveStatus));
        pageQuery.hibernateCriterion = cri;
//        pageQuery.hibernateFetchFields = "pubUserLogin";
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<SupSuplier> list = hibernateReadonlyRepository.getList(SupSuplier.class, pageQuery);
        return list;
    }
}
