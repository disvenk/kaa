package com.xxx.sales.service;


import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.sales.form.HrefForm;
import com.xxx.sales.form.StoreJoinForm;
import com.xxx.user.service.UploadFileService;
import com.xxx.model.business.StoStoreInfo;
import com.xxx.model.business.StoStorePicture;
import com.xxx.model.business.StoSuplierOrder;
import com.xxx.model.business.StoSuplierOrderDetail;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UserCenterService extends CommonService {

    @Autowired
    private UploadFileService uploadFileService;

    /**
     * @Description: 门店入驻
     * @Author: Chen.zm
     * @Date: 2017/11/20 0020
     */
    @CacheEvict(value = {"StoStoreInfo"}, allEntries = true)
    public StoStoreInfo saveStoreInfo(StoreJoinForm form, Integer storeId) throws ResponseEntityException, UpsertException{
        StoStoreInfo storeInfo = get2(StoStoreInfo.class, storeId);
        if (storeInfo == null)
            throw new ResponseEntityException(140, "门店不存在");
        storeInfo.setStoreName(form.name);
        storeInfo.setProvince(form.province);
        storeInfo.setProvinceName(form.provinceName);
        storeInfo.setCity(form.city);
        storeInfo.setCityName(form.cityName);
        storeInfo.setZone(form.zone);
        storeInfo.setZoneName(form.zoneName);
        storeInfo.setAddress(form.address);
        storeInfo.setStoreStatus(0);//待审核
        storeInfo.setStorepicture(form.hrefList.size() == 0 ? "" : uploadFileService.saveOssUploadFileByBase64(form.hrefList.get(0).href).toString());
        storeInfo.setCredentials(uploadFileService.saveOssUploadFileByBase64(form.credentials).toString());
        storeInfo.setContact(form.contact);
        storeInfo.setContactPhone(form.contactPhone);
        storeInfo.setScope(form.scope);
        upsert2(storeInfo);
        String hql1 = "delete StoStorePicture where storeId =:storeId";
        getCurrentSession().createQuery(hql1).setInteger("storeId", storeId).executeUpdate();
        boolean ismainHref = true;//标记主图
        for (HrefForm href : form.hrefList) {
            StoStorePicture stoProductPicture = new StoStorePicture();
            stoProductPicture.setStoreId(storeId);
            stoProductPicture.setHref(uploadFileService.saveOssUploadFileByBase64(href.href).toString());
            stoProductPicture.setSort(0.0);
            stoProductPicture.setMainpic(false);
            if (ismainHref) {
                stoProductPicture.setMainpic(true);
                ismainHref = false;
            }
            upsert2(stoProductPicture);
        }
        return storeInfo;
    }

    /**
     * @Description: 获取门店信息
     * @Author: Chen.zm
     * @Date: 2017/11/16 0016
     */
    @Cacheable(value = {"StoStoreInfo"})
    public StoStoreInfo storeInfo(Integer id) {
        Criterion cri = Restrictions.eq("id", id);
        StoStoreInfo storeInfo = (StoStoreInfo) getCurrentSession().createCriteria(StoStoreInfo.class)
                .add(cri)
                .setFetchMode("stoSalesCategoryList", FetchMode.JOIN)
                .setFetchMode("stoSalesCategoryList.plaProductCategory", FetchMode.JOIN)
                .uniqueResult();
        for (StoStorePicture picture : storeInfo.getStoStorePictureList()) {
        }
        return storeInfo;
    }

    /**
     * @Description: 会员中心：根据用户Id,取得用户正在进行中的采购订单
     * @Author: Steven.Xiao
     * @Date: 2017/11/16
     */
    @Cacheable(value = {"StoSuplierOrder"})
    public PageList<StoSuplierOrder> findSupplierOrderList(PageQuery pageQuery, Integer storeId) {
        Criterion cri = Restrictions.eq("storeId", storeId);
        //进行中的状态：
        cri = Restrictions.and(cri, Restrictions.ne("status", 2));
        cri = Restrictions.and(cri, Restrictions.ne("status", 3));
        pageQuery.hibernateCriterion = cri;
//        pageQuery.hibernateFetchFields = "stoSuplierOrder";
        pageQuery.limit = 10;
        pageQuery.order = "desc";
        pageQuery.sort = "createdDate";
        PageList<StoSuplierOrder> list = hibernateReadonlyRepository.getList(StoSuplierOrder.class, pageQuery);
        for (StoSuplierOrder order : list) {
            for (StoSuplierOrderDetail detail : order.getStoSuplierOrderDetailList()) {
            }
        }
        return list;
    }
}
