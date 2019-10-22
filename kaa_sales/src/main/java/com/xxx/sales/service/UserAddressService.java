package com.xxx.sales.service;


import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.PubUserAddress;
import com.xxx.sales.form.PubUserAddressForm;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAddressService extends CommonService {

    /**
     * @Description: 新增收件地址
     * @Author: Steven.Xiao
     * @Date: 17/10/25
     */
    @CacheEvict(value = {"PubUserAddress"}, allEntries = true)
    public PubUserAddress saveUserAddress(Integer userId, String receiver, String mobile, String tel, String provinceId,String provinceName,String cityId, String cityName,String zoneId, String zoneName, String address, boolean isDefault) throws UpsertException,ResponseEntityException{
        PubUserAddress pal = new PubUserAddress();
        if (pal == null)
            throw new ResponseEntityException(120, "用户地址不存在");
        pal.setUserId(userId);
        pal.setReceiver(receiver);
        pal.setMobile(mobile);
        pal.setTel(tel);
        pal.setProvince(provinceId);
        pal.setProvinceName(provinceName);
        pal.setCity(cityId);
        pal.setCityName(cityName);
        pal.setZone(zoneId);
        pal.setZoneName(zoneName);
        pal.setAddress(address);

        //如果新增的地址，为默认地址，则需要将其它地址置为非默认
        if(isDefault) {
            String hql = "update PubUserAddress set isDefault = 0 where userId =:userId";
            getCurrentSession().createQuery(hql).setInteger("userId", userId).executeUpdate();
        }

        pal.setDefault(isDefault);
        return upsert2(pal);
    }

    /**
     * @Description: 修改地址信息后，保存
     * @Author: Steven.Xiao
     * @Date: 17/10/25
     */
    @CacheEvict(value = {"PubUserAddress"}, allEntries = true)
    public PubUserAddress updateUserAddress(PubUserAddressForm form,Integer userId) throws UpsertException,ResponseEntityException{
        PubUserAddress pal = get2(PubUserAddress.class, "id", form.id, "userId", userId);
        if (pal == null)
            throw new ResponseEntityException(120, "用户地址Id不存在");
        pal.setReceiver(form.receiver);
        pal.setMobile(form.mobile);
        pal.setTel(form.tel);
        pal.setProvince(form.province);
        pal.setProvinceName(form.provinceName);
        pal.setCity(form.city);
        pal.setCityName(form.cityName);
        pal.setZone(form.zone);
        pal.setZoneName(form.zoneName);
        pal.setAddress(form.address);

        //如果编辑的地址，为默认地址，则需要将其它地址置为非默认
        if(form.isDefault) {
            String hql = "update PubUserAddress set isDefault = 0 where userId =:userId";
            getCurrentSession().createQuery(hql).setInteger("userId", userId).executeUpdate();
        }

        pal.setDefault(form.isDefault);
        return upsert2(pal);
    }

    /**
     * @Description: 根据ID删除地址信息
     * @Author: Steven.Xiao
     * @Date: 17/10/25
     */
    @CacheEvict(value = {"PubUserAddress"}, allEntries = true)
    public void removeUserAddress(Integer id) {
        PubUserAddress address = getUserAddress(id);
        delete(address);
    }

    /**
     * @Description: 根据记录ID取得地址信息
     * @Author: Steven.Xiao
     * @Date: 17/10/25
     */
    @Cacheable(value = {"PubUserAddress"})
    public PubUserAddress getUserAddress(Integer id) {
        return get2(PubUserAddress.class, "id", id);
    }

    /**
     * @Description: 根据用户用户ＩＤ，查询用户地址信息
     * @Author: Steven.Xiao
     * @Date: 17/10/25
     */
    @Cacheable(value = {"PubUserAddress"})
    public List<PubUserAddress> findUserAddressList(Integer userId) {
        Criterion cri = Restrictions.eq("userId", userId);
        return getCurrentSession().createCriteria(PubUserAddress.class)
				.add(cri)
				.addOrder(Order.desc("createdDate"))
                .list();
    }

    /**
     * @Description: 设为默认
     * @Author: Steven.Xiao
     * @Date: 2017/11/21
     */
    @CacheEvict(value = {"PubUserAddress"}, allEntries = true)
    public void setDefaultAddress(Integer id, Integer userId) throws UpsertException,ResponseEntityException {
        PubUserAddress address = get2(PubUserAddress.class, "id", id, "userId", userId);
        if (address == null)
            throw new ResponseEntityException(120, "用户地址不存在");
        String hql = "update PubUserAddress set isDefault = 0 where userId =:userId";
        getCurrentSession().createQuery(hql).setInteger("userId", userId).executeUpdate();

        address.setDefault(true);
        upsert2(address);
    }
}
