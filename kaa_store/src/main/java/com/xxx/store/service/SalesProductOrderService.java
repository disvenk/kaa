package com.xxx.store.service;


import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.service.CommonService;
import com.xxx.store.form.OrderDetailForm;
import com.xxx.store.form.SubmitOrderForm;
import com.xxx.user.utils.GenerateNumberUtil;
import com.xxx.utils.Arith;
import com.xxx.utils.DateTimeUtils;
import com.xxx.model.business.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Service
public class SalesProductOrderService extends CommonService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * @Description: 获取门店商品
     * @Author: Chen.zm
     * @Date: 2017/10/30 0030
     */
    @Cacheable(value = {"StoProduct,PlaProductCategory"})
    public StoProduct getStoreProduct(Integer productId) {
        Criterion cri = Restrictions.eq("id", productId);
        return (StoProduct) getCurrentSession().createCriteria(StoProduct.class)
                .add(cri)
                .setFetchMode("plaProductCategory", FetchMode.JOIN)
                .setFetchMode("plaProduct", FetchMode.JOIN)
                .uniqueResult();
    }

    /**
     * @Description: 获取指定的购物车商品
     * @Author: Chen.zm
     * @Date: 2017/10/30 0030
     */
    @Cacheable(value = {"StoShopcart"})
    public List<StoShopcart> findShopcartList(Integer storeId, List<Integer> ids) {
        Criterion cri = Restrictions.eq("storeId", storeId);
        cri = Restrictions.and(cri, Restrictions.in("id", ids));
        return getCurrentSession().createCriteria(StoShopcart.class)
                .add(cri)
                .setFetchMode("stoProduct", FetchMode.JOIN)
                .setFetchMode("stoProduct.plaProductCategory", FetchMode.JOIN)
                .setFetchMode("stoProduct.plaProduct", FetchMode.JOIN)
                .setFetchMode("stoProductSpec", FetchMode.JOIN)
                .addOrder(Order.desc("id"))
                .list();
    }

    /**
     * @Description: 创建销售订单
     * @Author: Chen.zm
     * @Date: 2017/10/30 0030
     */
    @CacheEvict(value = {"StoSalesOrder,StoSalesOrderDelivery,StoSalesOrderDetail,StoShopcart"}, allEntries = true)
    public StoSalesOrder saveStoSalesOrder(SubmitOrderForm form, Integer storeId) throws UpsertException, ResponseEntityException, ParseException {
        StoSalesOrder stoSalesOrder = new StoSalesOrder();
        stoSalesOrder.setOrderNo(GenerateNumberUtil.generateStoreSalesNumber());
        stoSalesOrder.setOrderDate(new Date());
        stoSalesOrder.setStoreId(storeId);
        StoStoreInfo store = get2(StoStoreInfo.class, storeId);
        if (store == null)
            throw new ResponseEntityException(150, "门店信息异常");
        stoSalesOrder.setChannelType(2);
        stoSalesOrder.setChannelName(store.getStoreName());
        stoSalesOrder.setRemarks(form.remarks);
        stoSalesOrder = upsert2(stoSalesOrder);

        //创建收件信息
        StoSalesOrderDelivery delivery = new StoSalesOrderDelivery();
        delivery.setOrderId(stoSalesOrder.getId());
        delivery.setReceiver(form.receiver);
        delivery.setMobile(form.mobile);
        delivery.setProvince(form.province);
        delivery.setProvinceName(form.provinceName);
        delivery.setCity(form.city);
        delivery.setCityName(form.cityName);
        delivery.setZone(form.zone);
        delivery.setZoneName(form.zoneName);
        delivery.setAddress(form.address);
        if (StringUtils.isNotBlank(form.expectsendTime)) {
            delivery.setExpectsendDate(DateTimeUtils.parseDate(form.expectsendTime, "yyyy-MM-dd"));
        }
        delivery = upsert2(delivery);

        //创建订单明细
        Double total = 0.0;
        for (OrderDetailForm detail : form.orderDetail) {
            StoSalesOrderDetail orderDetail = new StoSalesOrderDetail();
            orderDetail.setOrderId(stoSalesOrder.getId());
            orderDetail.setPid(detail.pid);
            StoProduct product = getStoreProduct(detail.pid);
            if (product == null)
                throw new ResponseEntityException(120, "商品不存在");
            orderDetail.setHref(product.getHref());
            orderDetail.setProductName(product.getName());
            orderDetail.setCategoryName(product.getPlaProductCategory() == null ? "" : product.getPlaProductCategory().getName());
            orderDetail.setColor(detail.color);
            orderDetail.setSize(detail.size);
            orderDetail.setShoulder(detail.shoulder);
            orderDetail.setBust(detail.bust);
            orderDetail.setWaist(detail.waist);
            orderDetail.setHipline(detail.hipline);
            orderDetail.setHeight(detail.height);
            orderDetail.setWeight(detail.weight);
            orderDetail.setThroatheight(detail.throatheight);
            orderDetail.setOther(detail.other);
            orderDetail.setQty(detail.num);
            StoProductPrice productPrice = shoppingCartService.getStoProductPrice(detail.pid, detail.color, detail.size);
            if (productPrice == null)
                throw new ResponseEntityException(140, "商品信息不存在");
            orderDetail.setPrice(productPrice.getOfflinePrice());
            orderDetail.setSubtotal(Arith.mul(orderDetail.getPrice(), orderDetail.getQty()));
            orderDetail = upsert2(orderDetail);
            total = Arith.add(total, orderDetail.getSubtotal());

            //清除对应的购物车商品  逻辑删除   若要数据删除，可以批量删除
            if (detail.shopcartId != null) {
                StoShopcart stoShopcart = get2(StoShopcart.class, detail.shopcartId);
                stoShopcart.setLogicDeleted(true);
                upsert2(stoShopcart);
            }
        }
        stoSalesOrder.setTotal(total);
        return upsert2(stoSalesOrder);
    }


}
