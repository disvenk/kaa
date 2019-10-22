package com.xxx.store.service;


import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.service.CommonService;
import com.xxx.utils.Arith;
import com.xxx.model.business.StoProductPrice;
import com.xxx.model.business.StoShopcart;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartService extends CommonService {

    /**
     * @Description: 门店购物车添加商品
     * @Author: Chen.zm
     * @Date: 2017/10/27 0027
     */
    @CacheEvict(value = {"StoShopcart"}, allEntries = true)
    public StoShopcart saveShopcartProduct(Integer storeId, Integer pid, Integer num, String color, String size) throws UpsertException,ResponseEntityException {
        StoShopcart shopcart = new StoShopcart();
        shopcart.setStoreId(storeId);
        shopcart.setColor(color);
        shopcart.setSize(size);
        shopcart.setPid(pid);
        shopcart.setQty((double) num);
        StoProductPrice productPrice = getStoProductPrice(pid, color, size);
        if (productPrice == null)
            throw new ResponseEntityException(140, "商品信息不存在");
        shopcart.setPrice(productPrice.getOfflinePrice());
        shopcart.setSubtotal(Arith.mul(shopcart.getPrice(), shopcart.getQty()));
        return upsert2(shopcart);
    }

    /**
     * @Description: 根据商品id、颜色、尺寸 获取商品信息
     * @Author: Chen.zm
     * @Date: 2017/11/10 0010
     */
    @Cacheable(value = {"StoProductPrice"})
    public StoProductPrice getStoProductPrice(Integer pid, String color, String size) {
        Criterion cri = Restrictions.eq("pid", pid);
        cri = Restrictions.and(cri, Restrictions.eq("color", color));
        cri = Restrictions.and(cri, Restrictions.eq("size", size));
        return (StoProductPrice) getCurrentSession().createCriteria(StoProductPrice.class)
                .add(cri)
                .uniqueResult();
    }

    /**
     * @Description: 获取购物车商品列表
     * @Author: Chen.zm
     * @Date: 2017/10/27 0027
     */
    @Cacheable(value = {"StoShopcart"})
    public List<StoShopcart> findShopcartList(Integer storeId) {
        Criterion cri = Restrictions.eq("storeId", storeId);
        cri = Restrictions.and(cri, Restrictions.eq("logicDeleted", false));
        return getCurrentSession().createCriteria(StoShopcart.class)
                .add(cri)
                .setFetchMode("stoProduct", FetchMode.JOIN)
                .setFetchMode("stoProductSpec", FetchMode.JOIN)
                .addOrder(Order.desc("id"))
                .list();
    }


    /**
     * @Description: 删除购物车内商品
     * @Author: Chen.zm
     * @Date: 2017/10/27 0027
     */
    @CacheEvict(value = {"StoShopcart"}, allEntries = true)
    public void removeShopcart(Integer storeId, Integer id) {
        if (id != null) {
            String hql = "delete StoShopcart where storeId =:storeId and id=:id";
            getCurrentSession().createQuery(hql).setInteger("storeId", storeId).setInteger("id", id).executeUpdate();
            return ;
        }
        String hql = "delete StoShopcart where storeId =:storeId";
        getCurrentSession().createQuery(hql).setInteger("storeId", storeId).executeUpdate();
    }

    /**
     * @Description: 修改购物车内的商品数量
     * @Author: Chen.zm
     * @Date: 2017/10/27 0027
     */
    @CacheEvict(value = {"StoShopcart"}, allEntries = true)
    public StoShopcart updateShopcartNum(Integer storeId, Integer id, Integer num) throws UpsertException,ResponseEntityException {
        StoShopcart shopcart = get2(StoShopcart.class, "id", id, "storeId", storeId);
        if (shopcart == null)
            throw new ResponseEntityException(130, "购物车商品不存在");
        shopcart.setQty((double) num);
        shopcart.setSubtotal(Arith.mul(shopcart.getPrice(), shopcart.getQty()));
        return upsert2(shopcart);
    }


}
