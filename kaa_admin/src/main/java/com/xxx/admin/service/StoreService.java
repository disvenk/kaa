package com.xxx.admin.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.StoSalesCategoryForm;
import com.xxx.admin.form.StoStorePictureForm;
import com.xxx.admin.form.StoreEditForm;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.ExtFilter;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
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
import java.util.Date;
import java.util.List;

@Service
public class StoreService extends CommonService {

    @Autowired
    private UploadFileService uploadFileService;
    /**
     * @Description: 查询所有门店信息
     * @Author: Steven.Xiao
     * @Date: 2017/10/26
     */
    @Cacheable(value = {"StoStoreInfo"})
    public PageList<StoStoreInfo> findStoreList(PageQuery pageQuery, String userCode, String mobile) {
//        Criterion cri = Restrictions.eq("logicDeleted", false);
        JSONObject op = JSONObject.parseObject("{\"operator\":\"or\"}");
        JSONArray jsonArray = new JSONArray();
        if (StringUtils.isNotBlank(userCode)) {
            ExtFilter filter = new ExtFilter("pubUserLogin.userCode", "string", userCode, ExtFilter.ExtFilterComparison.like, null);
            jsonArray.add(filter);
            jsonArray.add(op);
        }
        if (StringUtils.isNotBlank(mobile)) {
            ExtFilter filter = new ExtFilter("pubUserLogin.pubUserBase.mobile", "string", mobile, ExtFilter.ExtFilterComparison.like, null);
            jsonArray.add(filter);
        }
        pageQuery.filter = jsonArray.toJSONString();
//        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "pubUserLogin,pubUserLogin.pubUserBase";
        pageQuery.order = "desc";
        pageQuery.sort = "createdDate";
        PageList<StoStoreInfo> list = hibernateReadonlyRepository.getList(StoStoreInfo.class, pageQuery);
        return list;
    }

    /**
     * @Description: 判断该账号是否购买过合一盒子服务
     * @Author: Steven.Xiao
     * @Date: 2018/1/4
     */
    public String haveBuyHeyiService(Integer storeId)
    {
        BoxInfo boxInfo=get2(BoxInfo.class,"storeId",storeId);
        if(boxInfo!=null)
        {
            return "已购";
        }
        else
        {
            return "无";
        }
    }

    /**
     * @Description: 取得门店详情
     * @Author: Steven.Xiao
     * @Date: 2017/10/27
     */
    @Cacheable(value = {"StoStoreInfo"})
    public StoStoreInfo getStoreDetail(Integer id) {
        Criterion cri = Restrictions.eq("id", id);
        StoStoreInfo storeInfo = (StoStoreInfo) getCurrentSession().createCriteria(StoStoreInfo.class)
                .add(cri)
                .setFetchMode("stoStorePictureList", FetchMode.JOIN)
                .uniqueResult();
        return storeInfo;
    }

    /**
     * @Description: 编辑门店信息后，保存
     * @Author: Steven.Xiao
     * @Date: 2017/11/1
     */
    @CacheEvict(value = {"StoStoreInfo"}, allEntries = true)
    public StoStoreInfo updateStoStoreInfo(StoreEditForm form) throws UpsertException, ResponseEntityException {

        StoStoreInfo store = get2(StoStoreInfo.class, form.storeId);
        //更新会员信息
        PubUserBase userBase = store.getPubUserLogin().getPubUserBase();

        PubUserLogin login = store.getPubUserLogin();
        //图像
        if(StringUtils.isBlank(form.icon)){
            userBase.setIcon("");
        }else{
            userBase.setIcon(uploadFileService.saveOssUploadFileByBase64(form.icon).toString());
        }
        //用户名
        login.setUserCode(form.userCode);
        //手机号
        userBase.setMobile(form.mobile);
        //真实姓名
        userBase.setName(form.userName);
        //性别
        userBase.setSex(form.sex);
        //用户身份
        login.setUserType(form.userType);
        //备注
        userBase.setRemarks(form.remarks);

        //更新会员基本信息
        upsert2(userBase);

        //更新用户名信息
        upsert2(login);

        // 更新门店信息

        //门店名称
        store.setStoreName(form.storeName);
        //省
        store.setProvince(form.provinceId);
        //省名称
        store.setProvinceName(form.provinceName);
        //市
        store.setCity(form.cityId);
        //市名称
        store.setCityName(form.cityName);
        //区
        store.setZone(form.zoneId);
        //区名称
        store.setZoneName(form.zoneName);
        //详细地址
        store.setAddress(form.address);
        //审核状态
        store.setStoreStatus(form.approveStatus);
        //销售商品
        store.setScope(form.scope);

        //销售的商品
        //清除销售的商品
        String hql = "delete StoSalesCategory where storeId =:storeId";
        getCurrentSession().createQuery(hql).setInteger("storeId", form.storeId).executeUpdate();
        for (StoSalesCategoryForm category : form.stoSalesCategoryList) {
            StoSalesCategory salesCategory = new StoSalesCategory();
            salesCategory.setCategoryId(category.categoryId);
            salesCategory.setStoreId(form.storeId);
            upsert2(salesCategory);
        }

        //门店的图片
        //清除原先的图片
        String hql1 = "delete StoStorePicture where storeId =:storeId";
        getCurrentSession().createQuery(hql1).setInteger("storeId", form.storeId).executeUpdate();
        double i = 1;
        boolean ismainHref = true;//标记主图
        for (StoStorePictureForm picture : form.stoStorePictureList) {
            StoStorePicture stoProductPicture = new StoStorePicture();
            if(StringUtils.isBlank(picture.href)){
                stoProductPicture.setHref("");
            }else{
                stoProductPicture.setHref(uploadFileService.saveOssUploadFileByBase64(picture.href).toString());
            }
            stoProductPicture.setSort(i);
            stoProductPicture.setMainpic(false);
            if (ismainHref) {
                stoProductPicture.setMainpic(true);
            }
            ismainHref = false;
            stoProductPicture.setStoreId(form.storeId);
            i = i + 1;
            upsert2(stoProductPicture);
        }

        //门店首图：默认取第一张图片
        if(StringUtils.isBlank(form.stoStorePictureList.size() == 0 ? "" : form.stoStorePictureList.get(0).href)){
            store.setStorepicture("");
        }else {
            store.setStorepicture(uploadFileService.saveOssUploadFileByBase64(form.stoStorePictureList.size() == 0 ? "" : form.stoStorePictureList.get(0).href).toString());
        }
        //资质信息
        if(StringUtils.isBlank(form.credentials)){
            store.setCredentials("");
        }else{
            store.setCredentials(uploadFileService.saveOssUploadFileByBase64(form.credentials).toString());
        }

        //联系人
        store.setContact(form.contact);
        //联系人电话
        store.setContactPhone(form.contactTelephone);
        return upsert2(store);

    }

    /**
     * @Description: 删除门店
     * @Author: Steven.Xiao
     * @Date: 2017/10/31
     */
    @CacheEvict(value = {"StoStoreInfo"}, allEntries = true)
    public StoStoreInfo removeStoStoreInfo(Integer id) throws UpsertException, ResponseEntityException {
        StoStoreInfo store = getStoreDetail(id);
        if (store == null)
            throw new ResponseEntityException(120, "门店不存在");
        store.setLogicDeleted(true);
        return upsert2(store);
    }

    /**
     * @Description: 重置密码
     * @Author: Steven.Xiao
     * @Date: 2017/10/31
     */
    @CacheEvict(value = {"StoStoreInfo"}, allEntries = true)
    public PubUserLogin updateStorePassword(Integer id, String password) throws UpsertException, ResponseEntityException {
        StoStoreInfo store = get2(StoStoreInfo.class, id);
        if (store == null)
            throw new ResponseEntityException(120, "此门店不存在");
        PubUserLogin login = store.getPubUserLogin();
        login.setUserPassword(password);
        return upsert2(login);
    }

    /**
     * @Description: 审核门店
     * @Author: Steven.Xiao
     * @Date: 2017/10/31
     */
    @CacheEvict(value = {"StoStoreInfo"}, allEntries = true)
    public StoStoreInfo updateStoreStatus(Integer id, Integer status) throws UpsertException, ResponseEntityException {
        StoStoreInfo store = get2(StoStoreInfo.class, id);
        if (store == null)
            throw new ResponseEntityException(120, "此门店不存在");
        if (status.equals(store.getStoreStatus()))
            return store;
        store.setStoreStatus(status);
        return upsert2(store);
    }

    /**
     * @Description: 根据条件，查询门店商品
     * @Author: Steven.Xiao
     * @Date: 2017/10/31
     */
    @Cacheable(value = {"StoStoreInfo"})
    public PageList<StoProduct> findStoreProductList(PageQuery pageQuery, String productName, Integer categoryId, Integer storeId) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(productName))
            cri = Restrictions.and(cri, Restrictions.like("name", productName, MatchMode.ANYWHERE));
        if (categoryId != null)
            cri = Restrictions.and(cri, Restrictions.in("categoryId", getAllCategory(categoryId)));
        if (storeId != null)
            cri = Restrictions.and(cri, Restrictions.eq("storeId", storeId));
        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "stoStoreInfo";
        pageQuery.limit =10;
        pageQuery.order = "asc";
        pageQuery.sort = "sort";
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
     * @Description: 查看门店商品的详情
     * @Author: Steven.Xiao
     * @Date: 2017/10/31
     */
    @Cacheable(value = {"StoProduct"})
    public StoProduct getStoProductDetail(Integer id) {
        Criterion cri = Restrictions.eq("id", id);
        StoProduct product = (StoProduct) getCurrentSession().createCriteria(StoProduct.class)
                .add(cri)
                .setFetchMode("stoProductPictureList", FetchMode.JOIN)
                .uniqueResult();
        if (product != null) {
            for (StoProductPrice price : product.getStoProductPriceList()) {
            }
        }
        return product;
    }

    /**
     * @Description: 根据条件，查询门店的线下销售订单列表
     * @Author: Steven.Xiao
     * @Date: 2017/10/31
     */
    @Cacheable(value = {"StoSalesOrder"})
    public PageList<StoSalesOrder> findStoreSalesOderList(PageQuery pageQuery, String receiver, String mobile, String orderNo, String ordernoSuplier, Integer storeId) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(receiver))
            cri = Restrictions.and(cri, Restrictions.like("receiver", receiver, MatchMode.ANYWHERE));
        if (StringUtils.isNotBlank(mobile))
            cri = Restrictions.and(cri, Restrictions.like("mobile", mobile, MatchMode.ANYWHERE));
        if (StringUtils.isNotBlank(orderNo))
            cri = Restrictions.and(cri, Restrictions.like("orderNo", orderNo, MatchMode.ANYWHERE));
        if (StringUtils.isNotBlank(ordernoSuplier))
            cri = Restrictions.and(cri, Restrictions.like("ordernoSuplier", ordernoSuplier, MatchMode.ANYWHERE));
        if (storeId != null)
            cri = Restrictions.and(cri, Restrictions.eq("storeId", storeId));
        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "stoSalesOrderDelivery";
        pageQuery.limit = 10;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<StoSalesOrder> list = hibernateReadonlyRepository.getList(StoSalesOrder.class, pageQuery);
        for (StoSalesOrder stoSalesOrder : list) {
            for (StoSalesOrderDetail stoSalesOrderDetail : stoSalesOrder.getStoSalesOrderDetailList()) {
                stoSalesOrderDetail.getStoProduct();
            }
        }
        return list;
    }

    /**
     * @Description: 门店销售订单详情
     * @Author: Steven.Xiao
     * @Date: 2017/10/27
     */
    @Cacheable(value = {"StoSalesOrder,stoSalesOrderDetailList,stoSalesOrderDelivery"})
    public StoSalesOrder getStoSalesOrderDetail(Integer id) {
        Criterion cri = Restrictions.eq("id", id);
        return (StoSalesOrder) getCurrentSession().createCriteria(StoSalesOrder.class)
                .add(cri)
                .setFetchMode("stoSalesOrderDetailList", FetchMode.JOIN)
                .setFetchMode("stoSalesOrderDelivery", FetchMode.JOIN)
                .setFetchMode("StoSalesOrder", FetchMode.JOIN)
                .uniqueResult();
    }

    /**
     * @Description: 门店采购订单详情
     * @Author: Steven.Xiao
     * @Date: 2017/11/02
     */
    @Cacheable(value = {"StoSuplierOrder,StoSuplierOrderDetail,StoSuplierOrderDelivery"})
    public StoSuplierOrder getStoreSuplierOrderDetail(Integer id) {
        Criterion cri = Restrictions.eq("id", id);
        return (StoSuplierOrder) getCurrentSession().createCriteria(StoSuplierOrder.class)
                .add(cri)
                .setFetchMode("stoSuplierOrderDetailList", FetchMode.JOIN)
                .setFetchMode("stoSuplierOrderDetailList.stoProduct", FetchMode.JOIN)
                .setFetchMode("stoSuplierOrderDetailList.stoProduct.plaProduct", FetchMode.JOIN)
                .setFetchMode("stoSuplierOrderDelivery", FetchMode.JOIN)
                .setFetchMode("stoSalesOrder", FetchMode.JOIN)
                .setFetchMode("supOrder", FetchMode.JOIN)
                .setFetchMode("supOrder.supSuplier", FetchMode.JOIN)
                .uniqueResult();
    }

    /**
     * @Description: 获取门店采购订单对应的供应商采购订单   注：目前供应商订单只对应一个人商品明细
     * @Author: Chen.zm
     * @Date: 2017/11/1 0001
     */
    @Cacheable(value = {"SupOrder,SupOrderDetail"})
    public SupOrderDetail findSupOrderDetailByOrderIdAndPid(Integer orderId, Integer pid) {
        Criterion cri = Restrictions.eq("orderId", orderId);
        cri = Restrictions.and(cri, Restrictions.eq("pid", pid));
        return (SupOrderDetail) getCurrentSession().createCriteria(SupOrderDetail.class)
                .add(cri)
                .setFetchMode("supOrder", FetchMode.JOIN)
                .setFetchMode("supOrder.stoSuplierOrder", FetchMode.JOIN)
                .setFetchMode("supOrder.supSuplier", FetchMode.JOIN)
                .uniqueResult();
    }

    /**
     * @Description: 取得门店的的审核历史记录
     * @Author: Steven.Xiao
     * @Date: 2017/11/6
     */
    @Cacheable(value = {"StoStoreApproveLog"})
    public List<StoStoreApproveLog> findStoreApproveLog(Integer storeId) {
        Criterion cri = Restrictions.eq("storeId", storeId);
        return getCurrentSession().createCriteria(StoStoreApproveLog.class)
                .add(cri)
                .list();
    }

    /**
     * @Description: 门店更新 审核记录
     * @Author: Steven.Xiao
     * @Date: 2017/11/6
     */
    @Cacheable(value = {"StoStoreApproveLog"})
    public StoStoreApproveLog upateStoreApproveLog(Integer storeId, String description, Date approveDate, String operate) throws UpsertException, ResponseEntityException {
        StoStoreApproveLog log = new StoStoreApproveLog();
        log.setStoreId(storeId);
        log.setDescription(description);
        log.setOperate(operate);
        log.setApproveDate(approveDate);
        return upsert2(log);
    }

    /**
     * @Description: 取得所有门店列表
     * @Author: Steven.Xiao
     * @Date: 2017/11/7
     */
    @Cacheable(value = {"StoStoreInfo"})
    public List<StoStoreInfo> getStoStoreInfoList() {
        return getCurrentSession().createCriteria(StoStoreInfo.class)
                .addOrder(Order.asc("id"))
                .list();
    }

    /**
     * @Description: 根据销售订单Id,取得快递单号和快递公司名称
     * @Author: Steven.Xiao
     * @Date: 2017/11/10
     */
    @Cacheable(value = {"StoSalesOrderDelivery"})
    public StoSalesOrderDelivery getStoSalesOrderDeliveryById(Integer id) {
        Criterion cri = Restrictions.eq("orderId", id);
        StoSalesOrderDelivery stoSalesOrderDelivery   = (StoSalesOrderDelivery) getCurrentSession().createCriteria(StoSalesOrderDelivery.class)
                .add(cri)
                .uniqueResult();
        return stoSalesOrderDelivery;
    }

    /**
     * @Description: 根据ID取得快递公司的代码，传入快递100接口
     * @Author: Steven.Xiao
     * @Date: 2017/11/10
     */
    @Cacheable(value = {"PlaProductBase"})
    public PlaProductBase getPlaProductBaseById(Integer id) {
        return get2(PlaProductBase.class, id);
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
