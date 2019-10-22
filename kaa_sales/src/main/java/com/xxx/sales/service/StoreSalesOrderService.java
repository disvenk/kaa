package com.xxx.sales.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.ExtFilter;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.sales.form.*;
import com.xxx.utils.Arith;
import com.xxx.model.business.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class StoreSalesOrderService extends CommonService {

    /**
     * @Description: 会员中心:门店查询销售订单
     * @Author: Steven.Xiao
     * @Date: 2017/10/26
     */
    @Cacheable(value = {"StoSalesOrder"})
    public PageList<StoSalesOrder> findStoreSalesOrderList(PageQuery pageQuery, Integer storeId, String receiver, String mobile, String orderNo, String suplierOrderNo) {
        Criterion cri = Restrictions.eq("storeId", storeId);
        if (StringUtils.isNotBlank(orderNo))
            cri = Restrictions.and(cri, Restrictions.like("orderNo", orderNo,MatchMode.ANYWHERE));
        if (StringUtils.isNotBlank(suplierOrderNo))
            cri = Restrictions.and(cri, Restrictions.like("ordernoSuplier", suplierOrderNo,MatchMode.ANYWHERE));
        JSONArray jsonArray = new JSONArray();
        JSONObject op = JSONObject.parseObject("{\"operator\":\"or\"}");
        if(StringUtils.isNotBlank(mobile)) {
            ExtFilter filter = new ExtFilter("stoSalesOrderDelivery.mobile", "string", mobile, ExtFilter.ExtFilterComparison.like, null);
            jsonArray.add(filter);
            jsonArray.add(op);
        }
        if(StringUtils.isNotBlank(receiver)) {
            ExtFilter filter = new ExtFilter("stoSalesOrderDelivery.receiver", "string", receiver, ExtFilter.ExtFilterComparison.like, null);
            jsonArray.add(filter);
        }
        pageQuery.filter = jsonArray.toJSONString();
        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "stoSalesOrderDelivery,stoSuplierOrder";
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
     * @Description: 销售订单详情
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
     * @Description: 销售订单编辑保存
     * @Author: Steven.Xiao
     * @Date: 2017/10/27
     */
    @CacheEvict(value = {"StoSalesOrder"}, allEntries = true)
    public StoSalesOrder updateStoSalesOrder(StoreSalesOrderEditForm orderEditForm, Integer sotreId) throws UpsertException,ResponseEntityException{

        //订单主体字段更新
        StoSalesOrder order =get2(StoSalesOrder.class, "id", orderEditForm.id, "storeId", sotreId);
        order.setRemarks(orderEditForm.remarks);
        order.setTotal(orderEditForm.total);
        order=upsert2(order);

        //收件人信息更新
        StoSalesOrderDelivery delivery= order.getStoSalesOrderDelivery();
        delivery.setOrderId(orderEditForm.id);
        delivery.setReceiver(orderEditForm.receiver);
        delivery.setMobile(orderEditForm.mobile);
        delivery.setProvince(orderEditForm.provinceId);
        delivery.setProvinceName(orderEditForm.provinceName);
        delivery.setCity(orderEditForm.cityId);
        delivery.setCityName(orderEditForm.cityName);
        delivery.setZone(orderEditForm.zoneId);
        delivery.setZoneName(orderEditForm.zoneName);
        delivery.setAddress(orderEditForm.address);
        delivery.setDeliveryDate(orderEditForm.expectDeliveryDate);
        delivery=upsert2(delivery);

        //订单明细更新
        for (StoreSalesDetailSubmitForm detail : orderEditForm.orderDetail) {
            StoSalesOrderDetail orderDetail = get2(StoSalesOrderDetail.class, "orderId", orderEditForm.id, "pid", detail.pid);
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
            orderDetail.setPrice(detail.price);
            orderDetail.setSubtotal(Arith.mul(detail.price, detail.num));
            upsert2(orderDetail);
        }
        return  upsert2(order);
    }

    /**
     * @Description: 取消销售订单
     * @Author: Steven.Xiao
     * @Date: 2017/10/27
     */
    @CacheEvict(value = {"StoSalesOrder"}, allEntries = true)
    public StoSalesOrder removeStoSalesOrder(Integer id, Integer storeId) throws UpsertException,ResponseEntityException {
        StoSalesOrder order = get2(StoSalesOrder.class, "id",id, "storeId", storeId);
        if (order == null)
            throw new ResponseEntityException(120, "订单不存在");
        order.setLogicDeleted(true);
        return upsert2(order);
    }

    /**
     * @Description: 直接发货：门店销售订单的快递公司和快递单号保存
     * @Author: Steven.Xiao
     * @Date: 2017/10/27
     */
    @CacheEvict(value = {"StoSalesOrder,StoSalesOrderDelivery"}, allEntries = true)
    public StoSalesOrderDelivery saveStoSalesOrderDelivery(StoreSalesOrderDeliverNoSaveForm form) throws UpsertException,ResponseEntityException{
        StoSalesOrderDelivery stoSalesOrderDelivery = get2(StoSalesOrderDelivery.class, "orderId", form.orderId);
        if (stoSalesOrderDelivery == null)
            throw new ResponseEntityException(121, "收件地址对象不存在");
        stoSalesOrderDelivery.setDeliveryNo(form.deliveryNo);
        stoSalesOrderDelivery.setDeliveryCompany(form.deliverCompanyId);
        stoSalesOrderDelivery.setDeliveryCompanyName(form.deliveryCompanyName);
        //更新发货状态；
        StoSalesOrder order=get2(StoSalesOrder.class,form.orderId);
        order.setStatus(1);

        return upsert2(stoSalesOrderDelivery);
    }

    /**
     * @Description: 平台商品详情
     * @Author: Chen.zm
     * @Date: 2017/10/31 0031
     */
    @Cacheable(value = {"PlaProduct,PlaProductCategory,PlaProductPrice,PlaProductPicture"})
    public PlaProduct plaProductDetail(Integer id) {
        Criterion cri = Restrictions.eq("id", id);
        PlaProduct plaProduct = (PlaProduct) getCurrentSession().createCriteria(PlaProduct.class)
                .add(cri)
                .setFetchMode("plaProductCategory", FetchMode.JOIN)
                .setFetchMode("plaProductPictureList", FetchMode.JOIN)
                .uniqueResult();
        for (PlaProductPrice price : plaProduct.getPlaProductPriceList()) {
        }
        return plaProduct;
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

}
