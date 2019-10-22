package com.xxx.admin.service;


import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.dao.SuplierOrderDao;
import com.xxx.admin.form.SuplierAllotmentForm;
import com.xxx.admin.form.UpdateSuplierOrderForm;
import com.xxx.admin.form.UpdateSuplierOrderPidForm;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.MybatisPageQuery;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.user.utils.GenerateNumberUtil;
import com.xxx.utils.Arith;
import com.xxx.model.business.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuplierOrderService extends CommonService {

    @Autowired
    private SuplierOrderDao suplierOrderDao;

    /**
     * @Description: 门店采购订单列表
     * @Author: Chen.zm
     * @Date: 2017/10/31 0031
     */
    @Cacheable(value = {"StoSuplierOrder,StoSuplierOrderDetail"})
    public PageList<JSONObject> findSuplierOrderList(MybatisPageQuery pageQuery) throws Exception {
        //TODO 处理生产状态的 查询与筛选
        return suplierOrderDao.findSuplierOrderList(pageQuery);
    }

    /**
     * @Description: 门店采购订单详情
     * @Author: Chen.zm
     * @Date: 2017/10/31 0031
     */
    @Cacheable(value = {"StoSuplierOrder,StoSuplierOrderDetail,StoSuplierOrderDelivery"})
    public StoSuplierOrder findStoSuplierOrder(Integer id) {
        Criterion cri = Restrictions.eq("id", id);
        return (StoSuplierOrder) getCurrentSession().createCriteria(StoSuplierOrder.class)
                .add(cri)
                .setFetchMode("stoSuplierOrderDetailList", FetchMode.JOIN)
                .setFetchMode("stoSuplierOrderDelivery", FetchMode.JOIN)
                .setFetchMode("stoSalesOrder", FetchMode.JOIN)
                .setFetchMode("stoProduct", FetchMode.JOIN)
                .setFetchMode("stoProduct.plaProduct", FetchMode.JOIN)
                .setFetchMode("supOrder", FetchMode.JOIN)
                .setFetchMode("supOrder.supSuplier", FetchMode.JOIN)
                .uniqueResult();
    }




    /**
     * @Description: 获取门店采购订单对应的供应商采购订单  【* 一个采购订单明细  对应一个供应商订单
     * @param orderId: 采购订单id
     * @param pid: 商品id
     * @param color: 商品颜色
     * @param size: 商品尺寸
     * @Author: Chen.zm
     * @Date: 2017/11/1 0001
     */
    @Cacheable(value = {"SupOrder,SupOrderDetail"})
    public SupOrderDetail findSupOrderDetailByOrderIdAndPid(Integer orderId, Integer pid, String color, String size) {
        Criterion cri = Restrictions.eq("pid", pid);
        cri = Restrictions.and(cri, Restrictions.eq("color", color));
        cri = Restrictions.and(cri, Restrictions.eq("size", size));
        cri = Restrictions.and(cri, Restrictions.eq("s.orderId", orderId));
        return (SupOrderDetail) getCurrentSession().createCriteria(SupOrderDetail.class)
                .createAlias("supOrder", "s", JoinType.LEFT_OUTER_JOIN)
                .add(cri)
                .setFetchMode("supOrder", FetchMode.JOIN)
                .setFetchMode("supOrder.stoSuplierOrder", FetchMode.JOIN)
                .setFetchMode("supOrder.supSuplier", FetchMode.JOIN)
                .uniqueResult();
    }


    /**
     * @Description: 修改采购订单信息
     * @Author: Chen.zm
     * @Date: 2017/11/1 0001
     */
    @CacheEvict(value = {"StoSuplierOrder,StoSuplierOrderDelivery,SupOrder"}, allEntries = true)
    public StoSuplierOrder updateSuplierOrder(UpdateSuplierOrderForm form) throws UpsertException, ResponseEntityException{
        StoSuplierOrder suplierOrder = findStoSuplierOrder(form.id);
        if (suplierOrder == null)
                throw new ResponseEntityException(120, "订单不存在");
        suplierOrder.setStatus(form.status);
        suplierOrder.setRemarks(form.remarks);
        StoSuplierOrderDelivery delivery = suplierOrder.getStoSuplierOrderDelivery();
        if (delivery != null) {
            delivery.setReceiver(form.receiver);
            delivery.setMobile(form.mobile);
            delivery.setProvince(form.province);
            delivery.setProvinceName(form.provinceName);
            delivery.setCity(form.city);
            delivery.setCityName(form.cityName);
            delivery.setZone(form.zone);
            delivery.setZoneName(form.zoneName);
            delivery.setAddress(form.address);
            delivery.setDeliveryCompany(form.deliveryCompany);
            delivery.setDeliveryCompanyName(form.deliveryCompanyName);
            delivery.setDeliveryNo(form.deliveryNo);
            upsert2(delivery);
        }
        for (UpdateSuplierOrderPidForm pidForm : form.pids) {
            SupOrderDetail supOrderDetail = findSupOrderDetailByOrderIdAndPid(form.id, pidForm.pid, pidForm.color, pidForm.size);
            if (supOrderDetail == null)
                throw new ResponseEntityException(130, "供应商订单明细不存在");
            SupOrder supOrder = supOrderDetail.getSupOrder();
            if (supOrder == null)
                throw new ResponseEntityException(140, "供应商订单不存在");
            supOrder.setProducedStatus(pidForm.producedStatus);
            upsert2(supOrder);
        }
        return upsert2(suplierOrder);
    }

    /**
     * @Description: 采购订单发货
     * @Author: Chen.zm
     * @Date: 2017/11/1 0001
     */
    @CacheEvict(value = {"StoSuplierOrderDelivery"}, allEntries = true)
    public void updateSuplierOrderShipping(Integer id, Integer deliveryCompany, String deliveryCompanyName, String deliveryNo) throws UpsertException, ResponseEntityException {
        StoSuplierOrder suplierOrder = findStoSuplierOrder(id);
        if (suplierOrder == null)
            throw new ResponseEntityException(120, "订单不存在");
        if (suplierOrder.getStatus() != 1)
            throw new ResponseEntityException(130, "订单只能在待发货时发货");
        StoSuplierOrderDelivery delivery = suplierOrder.getStoSuplierOrderDelivery();
        if (delivery == null)
            throw new ResponseEntityException(140, "订单收货明细不存在");
        delivery.setDeliveryCompany(deliveryCompany);
        delivery.setDeliveryCompanyName(deliveryCompanyName);
        delivery.setDeliveryNo(deliveryNo);
        upsert2(delivery);
        suplierOrder.setStatus(4);
        upsert2(suplierOrder);
    }


    /**
     * @Description: 获取供应商列表
     * @Author: Chen.zm
     * @Date: 2017/11/1 0001
     */
    @Cacheable(value = {"SupSuplier"})
    public List<SupSuplier> findSuplierList(String name, String size, String scope) throws Exception {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(name))
            cri = Restrictions.and(cri, Restrictions.like("name", name, MatchMode.ANYWHERE));
        if (StringUtils.isNotBlank(size))
            cri = Restrictions.and(cri, Restrictions.like("size", size, MatchMode.ANYWHERE));
        if (StringUtils.isNotBlank(scope))
            cri = Restrictions.and(cri, Restrictions.like("scope", scope, MatchMode.ANYWHERE));
        return getCurrentSession().createCriteria(SupSuplier.class)
                .add(cri)
                .list();
    }


    /**
     * @Description: 分配供应商订单
     * @Author: Chen.zm
     * @Date: 2017/10/31 0031
     */
    @CacheEvict(value = {"StoSuplierOrder,StoSuplierOrderDetail,SupOrder,SupOrderDetail"}, allEntries = true)
    public SupOrder saveSupOrder(SuplierAllotmentForm form) throws UpsertException, ResponseEntityException{
        StoSuplierOrder suplierOrder = findStoSuplierOrder(form.orderId);
        if (suplierOrder == null)
            throw new ResponseEntityException(150, "订单不存在");
        StoSuplierOrderDetail suplierOrderDetail = get2(StoSuplierOrderDetail.class, form.detailId);
        if (suplierOrderDetail == null)
            throw new ResponseEntityException(160, "商品信息不存在");
        SupSuplier supSuplier = get2(SupSuplier.class, form.suplierId);
        if (supSuplier == null)
            throw new ResponseEntityException(170, "供应商不存在");

        //判断是否已指派供应商
        SupOrderDetail detail = findSupOrderDetailByOrderIdAndPid(form.orderId, suplierOrderDetail.getPid(), suplierOrderDetail.getColor(), suplierOrderDetail.getSize());
        if (detail != null) {
            //更换供应商
            SupOrder supOrder = detail.getSupOrder();
            if (supOrder.getProducedStatus() != null && supOrder.getProducedStatus() != 0 && supOrder.getProducedStatus() != 1)
                throw new ResponseEntityException(180, "供应商未接单时才能更改供应商");
            supOrder.setSuplierId(form.suplierId);
            detail.setOutputPrice(form.outputPrice);
            upsert2(detail);
            return upsert2(supOrder);
        }
        SupOrder supOrder = new SupOrder();
        supOrder.setOrderNo(GenerateNumberUtil.generateSuplierNumber(suplierOrder.getOrderNo()));
        supOrder.setOrderId(suplierOrder.getId());
        supOrder.setTotal(suplierOrder.getTotal());
        supOrder.setActualPay(suplierOrder.getActualPay());
        supOrder.setSuplierId(form.suplierId);
        supOrder = upsert2(supOrder);
        //创建商品明细
        SupOrderDetail orderDetail = new SupOrderDetail();
        orderDetail.setOrderId(supOrder.getId());
        orderDetail.setPid(suplierOrderDetail.getPid());
        orderDetail.setHref(suplierOrderDetail.getHref());
        orderDetail.setProductName(suplierOrderDetail.getProductName());
        orderDetail.setCategoryName(suplierOrderDetail.getCategoryName());
        orderDetail.setColor(suplierOrderDetail.getColor());
        orderDetail.setSize(suplierOrderDetail.getSize());
        orderDetail.setShoulder(suplierOrderDetail.getShoulder());
        orderDetail.setBust(suplierOrderDetail.getBust());
        orderDetail.setWaist(suplierOrderDetail.getWaist());
        orderDetail.setHipline(suplierOrderDetail.getHipline());
        orderDetail.setHeight(suplierOrderDetail.getHeight());
        orderDetail.setWeight(suplierOrderDetail.getWeight());
        orderDetail.setThroatheight(suplierOrderDetail.getThroatheight());
        orderDetail.setOther(suplierOrderDetail.getOther());
        orderDetail.setQty(suplierOrderDetail.getQty());
        orderDetail.setPrice(suplierOrderDetail.getPrice());
        orderDetail.setSubtotal(Arith.mul(form.outputPrice, suplierOrderDetail.getQty()));
        orderDetail.setOutputPrice(form.outputPrice);
        upsert2(orderDetail);
        return supOrder;
    }


    /**
     * @Description: 修改质检状态
     * @Author: Chen.zm
     * @Date: 2017/11/4 0004
     */
    @CacheEvict(value = {"SupOrder"}, allEntries = true)
    public SupOrder updateProducedStatus(Integer orderId, Integer pid, Integer producedStatus, String instruction, String personnel, String color, String size) throws UpsertException,ResponseEntityException {
        SupOrderDetail detail = findSupOrderDetailByOrderIdAndPid(orderId, pid, color, size);
        if (detail == null)
            throw new ResponseEntityException(140, "订单不存在");
        SupOrder supOrder = detail.getSupOrder();
        if (supOrder == null)
            throw new ResponseEntityException(140, "订单不存在");
        if (supOrder.getProducedStatus() < 3)
            throw new ResponseEntityException(150, "只有在生产完成时才能质检");
        switch (producedStatus){
            case 4:
                supOrder.setProducedStatus(4);
                saveSupOrderDeliveryLog(supOrder.getId(), "质检通过", instruction, personnel);
                break;
            case 5:
                supOrder.setProducedStatus(5);
                saveSupOrderDeliveryLog(supOrder.getId(), "质检未通过", instruction, personnel);
                break;
        }
        return upsert2(supOrder);
    }

    /**
     * @Description: 创建交付记录
     * @Author: Chen.zm
     * @Date: 2017/11/4 0004
     */
    @CacheEvict(value = {"SupOrderDeliveryLog"}, allEntries = true)
    public SupOrderDeliveryLog saveSupOrderDeliveryLog(Integer orderId, String action, String instruction, String personnel) throws UpsertException {
        SupOrderDeliveryLog log = new SupOrderDeliveryLog();
        log.setOrderId(orderId);
        log.setAction(action);
        log.setInstruction(instruction);
        log.setPersonnel(personnel);
        return upsert2(log);
    }

    /**
     * @Description: 获取订单交付记录列表
     * @Author: Chen.zm
     * @Date: 2017/11/4 0004
     */
    @Cacheable(value = {"SupOrderDeliveryLog"})
    public List<SupOrderDeliveryLog> findSupOrderDeliveryLogList(Integer id) {
        Criterion cri = Restrictions.eq("orderId", id);
        return getCurrentSession().createCriteria(SupOrderDeliveryLog.class)
                .add(cri)
                .addOrder(Order.asc("id"))
                .list();
    }

    /**
     * @Description:工单状态
     * @Author: hanchao
     * @Date: 2017/12/13 0013
     */
    @Cacheable(value = {"StoSuplierOrder,SupOrder"})
    public List<SupOrder> findSupOrderStatusList(String suplierOrderNo) throws ResponseEntityException {
        Criterion cri = Restrictions.eq("orderNo", suplierOrderNo);
        StoSuplierOrder suplierOrder = (StoSuplierOrder) getCurrentSession().createCriteria(StoSuplierOrder.class) .add(cri).uniqueResult();
        if(suplierOrder == null){
            throw new ResponseEntityException(210, "该订单不存在");
        }
        cri = Restrictions.eq("orderId", suplierOrder.getId());
        return getCurrentSession().createCriteria(SupOrder.class)
                .add(cri)
                .addOrder(Order.asc("id"))
                .list();
    }
}
