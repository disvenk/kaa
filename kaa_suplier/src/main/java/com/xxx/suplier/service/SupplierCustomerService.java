package com.xxx.suplier.service;


import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.SupCustomer;
import com.xxx.model.business.SupCustomerAddress;
import com.xxx.suplier.form.AddCustomerAndAddress;
import com.xxx.suplier.form.SupplierCustomerAddressEditForm;
import com.xxx.suplier.form.SupplierCustomerEditForm;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.service.UploadFileService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Service
public class SupplierCustomerService extends CommonService {
    @Autowired
    private UploadFileService uploadFileService;

    /**
     * @Description:本地工单管理里面的客户模糊查询
     * @Author: hanchao
     * @Date: 2017/12/20 0020
     */
    @CacheEvict(value = {"SupCustomer"}, allEntries = true)
    public List<SupCustomer> selectSupplierCustomer(Integer supplierId, String customer) throws UpsertException, ResponseEntityException, ParseException {
        Criterion cri = Restrictions.eq("suplierId", supplierId);
        cri = Restrictions.and(cri, Restrictions.eq("logicDeleted",false));
        if (StringUtils.isNotBlank(customer))
            cri = Restrictions.and(cri, Restrictions.like("customer", customer, MatchMode.ANYWHERE));
        List<SupCustomer> supCustomers = getCurrentSession().createCriteria(SupCustomer.class)
                .add(cri)
                .list();
        return supCustomers;
    }


    /**
     * @Description:客户管理地址新增
     * @Author: hanchao
     * @Date: 2017/12/20 0020
     */
    @CacheEvict(value = {"SupCustomerAddress"}, allEntries = true)
    public void addSupplierCustomerAddress(SupplierCustomerAddressEditForm form) throws UpsertException, ResponseEntityException {
        SupCustomerAddress supCustomerAddress = new SupCustomerAddress();
        supCustomerAddress.setReceiver(form.receiver);
        supCustomerAddress.setCustomerId(form.id);
        supCustomerAddress.setMobile(form.mobile);
        supCustomerAddress.setProvince(form.province);
        supCustomerAddress.setProvinceName(form.provinceName);
        supCustomerAddress.setCity(form.city);
        supCustomerAddress.setCityName(form.cityName);
        supCustomerAddress.setZone(form.zone);
        supCustomerAddress.setZoneName(form.zoneName);
        supCustomerAddress.setAddress(form.address);
        upsert2(supCustomerAddress);
    }

    /**
     * @Description:客户管理地址保存
     * @Author: hanchao
     * @Date: 2017/12/20 0020
     */
    @CacheEvict(value = {"SupCustomerAddress"}, allEntries = true)
    public void saveSupplierCustomerAddress(SupplierCustomerAddressEditForm form) throws UpsertException, ResponseEntityException {
        SupCustomerAddress supCustomerAddress = get2(SupCustomerAddress.class, "id", form.id);
        if (supCustomerAddress == null)
            throw new ResponseEntityException(210, "客户地址信息不存在");
        supCustomerAddress.setReceiver(form.receiver);
        supCustomerAddress.setMobile(form.mobile);
        supCustomerAddress.setProvince(form.province);
        supCustomerAddress.setProvinceName(form.provinceName);
        supCustomerAddress.setCity(form.city);
        supCustomerAddress.setCityName(form.cityName);
        supCustomerAddress.setZone(form.zone);
        supCustomerAddress.setZoneName(form.zoneName);
        supCustomerAddress.setAddress(form.address);
        upsert2(supCustomerAddress);
    }

    /**
     * @Description:工厂端地址列表删除
     * @Author: hanchao
     * @Date: 2017/12/20 0020
     */
    @CacheEvict(value = {"SupCustomerAddress"}, allEntries = true)
    public void removeSupplierCustomerAddress(Integer id) throws UpsertException, ResponseEntityException {
        SupCustomerAddress supCustomerAddress = get2(SupCustomerAddress.class, id);
        if (supCustomerAddress == null)
            throw new ResponseEntityException(210, "地址信息不存在");
        supCustomerAddress.setLogicDeleted(true);
        upsert2(supCustomerAddress);
    }

    /**
     * @Description:客户管理地址列表
     * @Author: hanchao
     * @Date: 2017/12/20 0020
     */
    @CacheEvict(value = {"SupCustomerAddress"}, allEntries = true)
    public List<SupCustomerAddress> getSupplierCustomerAddressList(Integer id) throws UpsertException, ResponseEntityException, ParseException {
        Criterion cri = Restrictions.eq("customerId", id);
        cri = Restrictions.and(cri, Restrictions.eq("logicDeleted", false));
        List<SupCustomerAddress> supCustomerAddresses = getCurrentSession().createCriteria(SupCustomerAddress.class)
                .add(cri)
                .list();
        return supCustomerAddresses;
    }

    /**
     * @Description:工厂端客户信息删除
     * @Author: hanchao
     * @Date: 2017/12/20 0020
     */
    @CacheEvict(value = {"SupCustomer"}, allEntries = true)
    public void removeSupplierCustomer(Integer supplierId, SupplierCustomerEditForm form) throws UpsertException, ResponseEntityException {
        SupCustomer supCustomer = get2(SupCustomer.class, "id", form.id, "suplierId", supplierId);
        if (supCustomer == null)
            throw new ResponseEntityException(210, "客户信息不存在");
        supCustomer.setLogicDeleted(true);
        upsert2(supCustomer);
    }

    /**
     * @Description:新增工厂端客户管理信息
     * @Author: hanchao
     * @Date: 2017/12/20 0020
     */
    @CacheEvict(value = {"SupCustomer"}, allEntries = true)
    public void addSupplierCustomer(SupplierCustomerEditForm form) throws UpsertException, ResponseEntityException {
        SupCustomer supCustomer = new SupCustomer();
        supCustomer.setCustomer(form.customer);
        supCustomer.setCustomerInit(form.customerInit);
        supCustomer.setCustomerPhone(form.customerPhone);
        supCustomer.setIcon(form.icon);
        supCustomer.setRemarks(form.remark);
        supCustomer.setUpdateDate(new Date());
        supCustomer.setSuplierId(CurrentUser.get().getSuplierId());
        upsert2(supCustomer);
    }

    /**
     * @Description:保存工厂端客户管理信息
     * @Author: hanchao
     * @Date: 2017/12/20 0020
     */
    @CacheEvict(value = {"SupCustomer"}, allEntries = true)
    public void saveSupplierCustomerEdit(Integer supplierId, SupplierCustomerEditForm form) throws UpsertException, ResponseEntityException, ParseException {
        SupCustomer supCustomer = get2(SupCustomer.class, "id", form.id, "suplierId", supplierId);
        if (supCustomer == null)
            throw new ResponseEntityException(210, "客户信息不存在");
        supCustomer.setCustomer(form.customer);
        supCustomer.setCustomerInit(form.customerInit);
        supCustomer.setCustomerPhone(form.customerPhone);
        supCustomer.setIcon(form.icon);
        supCustomer.setRemarks(form.remark);
        upsert2(supCustomer);
    }

    /**
     * @Description:工厂端客户维护列表
     * @Author: hanchao
     * @Date: 2017/12/20 0020
     */
    @Cacheable(value = {"SupCustomer"})
    public PageList<SupCustomer> findSupplierCustomerList(PageQuery pageQuery, String customer, String customerInit, String customerPhone, Integer suplierId) throws ParseException {
        Criterion cri = Restrictions.eq("suplierId", suplierId);
        if (StringUtils.isNotBlank(customer))
            cri = Restrictions.and(cri, Restrictions.like("customer", customer, MatchMode.ANYWHERE));
        if (StringUtils.isNotBlank(customerInit))
            cri = Restrictions.and(cri, Restrictions.like("customerInit", customerInit, MatchMode.ANYWHERE));
        if (StringUtils.isNotBlank(customerPhone))
            cri = Restrictions.and(cri, Restrictions.like("customerPhone", customerPhone, MatchMode.ANYWHERE));
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<SupCustomer> list = hibernateReadonlyRepository.getList(SupCustomer.class, pageQuery);
        return list;
    }

    /**
     * @Description: 新增一个客户及新增相应的客户地址
     * @Author: Steven.Xiao
     * @Date: 2018/1/11
     */
    @CacheEvict(value = {"SupCustomer"}, allEntries = true)
    public SupCustomer addCustomerAndAddress(Integer supplierId, AddCustomerAndAddress form) throws UpsertException, ResponseEntityException, ParseException {
        SupCustomer supCustomer = new SupCustomer();
        supCustomer.setSuplierId(supplierId);
        supCustomer.setCustomer(form.customerName);
        supCustomer.setCustomerInit(form.contactName);
        supCustomer.setCustomerPhone(form.mobile);
        supCustomer = upsert2(supCustomer);

        //保存新增地址
        SupCustomerAddress address = new SupCustomerAddress();
        address.setCustomerId(supCustomer.getId());
        address.setReceiver(form.receiver);
        address.setMobile(form.receiverTel);
        address.setProvince(form.provinceId);
        address.setProvinceName(form.province);
        address.setCity(form.cityId);
        address.setCityName(form.city);
        address.setZone(form.zoneId);
        address.setZoneName(form.zone);
        address.setAddress(form.address);
        upsert2(address);

        return supCustomer;
    }

    /**
     * @Description: 判断客户名称是否唯一
     * @Author: Steven.Xiao
     * @Date: 2018/1/11
     */
    @CacheEvict(value = {"SupCustomer"}, allEntries = true)
    public boolean notExistCustomer(Integer supplierId, String customerName) {
        SupCustomer supCustomer = get2(SupCustomer.class, "customer", customerName, "suplierId", supplierId, "logicDeleted", false);
        if (supCustomer == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @Description: 用一句话描述作用
     * @Author: Steven.Xiao
     * @Date: 2018/1/13
     */
    @CacheEvict(value = {"SupCustomer"}, allEntries = true)
    public SupCustomer getCustomer(Integer customerId) {
        SupCustomer supCustomer = get2(SupCustomer.class, "id", customerId, "logicDeleted", false);
        return supCustomer;
    }

}
