package com.xxx.suplier.service;


import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.ListUtils;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.*;
import com.xxx.suplier.form.IdForm;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SuplierOrderService extends CommonService {

    /**
     * @Description: 获取供应商订单
     * @Author: Chen.zm
     * @Date: 2017/11/3 0003
     */
    @Cacheable(value = {"SupOrder,StoSuplierOrder,SupOrderDetail"})
    public PageList<SupOrder> findSupOrderByProducedStatus(PageQuery pageQuery, Integer suplierId, Integer status) {
        Criterion cri = Restrictions.eq("suplierId", suplierId);
        if (status != null)
            cri = Restrictions.and(cri, Restrictions.eq("producedStatus", status));
        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "stoSuplierOrder," +
                "stoSuplierOrder.stoSuplierOrderDelivery," +
                "supOrderDetail," +
                "supOrderDetail.stoProduct," +
                "supOrderDetail.stoProduct.plaProduct";
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        return ListUtils.removeDuplicateWithOrder(hibernateReadonlyRepository.getList(SupOrder.class, pageQuery));
    }


    /**
     * @Description: 获取工单详情
     * @Author: Chen.zm
     * @Date: 2017/11/3 0003
     */
    @Cacheable(value = {"SupOrder,StoSuplierOrder,SupOrderDetail"})
    public SupOrder findSupOrderDateil(Integer id, Integer suplierId) {
        Criterion cri = Restrictions.eq("id", id);
        cri = Restrictions.and(cri, Restrictions.eq("suplierId", suplierId));
        return (SupOrder) getCurrentSession().createCriteria(SupOrder.class)
                .add(cri)
                .setFetchMode("stoSuplierOrder", FetchMode.JOIN)
                .setFetchMode("supOrderDelivery", FetchMode.JOIN)
                .setFetchMode("supOrderDetail", FetchMode.JOIN)
                .setFetchMode("supOrderDetail.plaProduct", FetchMode.JOIN)
                .setFetchMode("supOrderDetail.plaProduct.supSuplier", FetchMode.JOIN)
                .uniqueResult();
    }

    /**
     * @Description: 修改供应商生产状态
     * @Author: Chen.zm
     * @Date: 2017/11/4 0004
     */
//    @CacheEvict(value = {"SupOrder"}, allEntries = true)
//    public SupOrder updateProducedStatus(Integer suplierId, Integer orderId, Integer producedStatus, String instruction) throws UpsertException,ResponseEntityException {
////        SupOrder supOrder = get2(SupOrder.class, "id", orderId, "suplierId", suplierId);
//        SupOrder supOrder = findSupOrderDateil(orderId);
//        if (supOrder == null)
//            throw new ResponseEntityException(140, "订单不存在");
//        if (supOrder.getProducedStatus() != producedStatus)
//            throw new ResponseEntityException(150, "订单状态异常");
//        switch (producedStatus){
//            case 1: supOrder.setProducedStatus(2); break;
//            case 2:
//                supOrder.setProducedStatus(3);
//                saveSupOrderDeliveryLog(supOrder.getId(), "生产完成", instruction);
//                break;
//            case 5:
//                supOrder.setProducedStatus(3);
//                saveSupOrderDeliveryLog(supOrder.getId(), "修改完成", instruction);
//                break;
//        }
//        return upsert2(supOrder);
//    }

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
     * @Description: 批量修改备注
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @CacheEvict(value = {"SupOrder"}, allEntries = true)
    public void updateSupOrderRemarks(String remarks, String name, List<IdForm> supOrderIds) throws UpsertException {
        List<Integer> ids = new ArrayList<>();
        for (IdForm id : supOrderIds){
            ids.add(id.id);
            //创建备注记录
            saveSupOrderRemarkLog(id.id, name, remarks);
        }
        String hql = "update SupOrder set remarks = :remarks where id in :ids";
        getCurrentSession().createQuery(hql).setString("remarks", remarks).setParameterList("ids", ids).executeUpdate();
    }

    /**
     * @Description: 创建备注记录
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @CacheEvict(value = {"SupOrderDeliveryRecord"}, allEntries = true)
    public void saveSupOrderRemarkLog(Integer orderId, String name, String remarks) throws UpsertException{
        SupOrderRemarkLog log = new SupOrderRemarkLog();
        log.setOrderId(orderId);
        log.setName(name);
        log.setRemarks(remarks);
        upsert2(log);
    }

    /**
     * @Description: 获取工单备注记录
     * @Author: Chen.zm
     * @Date: 2017/12/12 0012
     */
    @Cacheable(value = {"SupOrderRemarkLog"})
    public List<SupOrderRemarkLog> findSupOrderRemarkLogList(Integer id) {
        Criterion cri = Restrictions.eq("orderId", id);
        return getCurrentSession().createCriteria(SupOrderRemarkLog.class)
                .add(cri)
                .addOrder(Order.desc("id"))
                .list();
    }


    /**
     * @Description: 更新当前物流信息
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @CacheEvict(value = {"SupOrderDelivery"}, allEntries = true)
    public void updateSupOrderDelivery(Integer orderId, Integer deliveryCompany, String deliveryCompanyName, String deliveryNo) throws UpsertException, ResponseEntityException {
        SupOrderDelivery delivery = get2(SupOrderDelivery.class, "orderId", orderId);
        if (delivery == null)
            throw new ResponseEntityException(230, "工单信息异常");
        delivery.setDeliveryCompany(deliveryCompany);
        delivery.setDeliveryCompanyName(deliveryCompanyName);
        delivery.setDeliveryNo(deliveryNo);
        upsert2(delivery);
    }

    /**
     * @Description: 创建物流记录
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @CacheEvict(value = {"SupOrderDeliveryRecord"}, allEntries = true)
    public void saveSupOrderDeliveryRecord(Integer orderId, Integer deliveryCompany, String deliveryCompanyName, String deliveryNo) throws UpsertException, ResponseEntityException {
        SupOrderDeliveryRecord record = new SupOrderDeliveryRecord();
        record.setOrderId(orderId);
        record.setDeliveryCompany(deliveryCompany);
        record.setDeliveryCompanyName(deliveryCompanyName);
        record.setDeliveryNo(deliveryNo);
        upsert2(record);
    }

    /**
     * @Description: 获取工单物流记录
     * @Author: Chen.zm
     * @Date: 2017/12/12 0012
     */
    @Cacheable(value = {"SupOrderDeliveryRecord"})
    public List<SupOrderDeliveryRecord> findSupOrderDeliveryRecordList(Integer id) {
        Criterion cri = Restrictions.eq("orderId", id);
        return getCurrentSession().createCriteria(SupOrderDeliveryRecord.class)
                .add(cri)
                .setFetchMode("plaProductBase", FetchMode.JOIN)
                .addOrder(Order.desc("id"))
                .list();
    }

    /**
     * @Description: 工单接单
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @CacheEvict(value = {"SupOrder,SupOrderDeliveryLog"}, allEntries = true)
    public void saveSupOrderTaking(Integer suplierId, String name, List<IdForm> supOrderIds) throws UpsertException, ResponseEntityException {
        for (IdForm id : supOrderIds){
            SupOrder supOrder = get2(SupOrder.class, "id", id.id, "suplierId", suplierId);
            if (supOrder == null)
                throw new ResponseEntityException(210, "工单不存在");
            if (supOrder.getProducedStatus() != 2)
                throw new ResponseEntityException(220, "工单非待接单");
            supOrder.setProducedStatus(3);
            upsert2(supOrder);

            //创建工单记录
            saveSupOrderDeliveryLog(id.id, "接单","无", name);
        }
    }


    /**
     * @Description: 工单发货
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @CacheEvict(value = {"SupOrder,SupOrderDeliveryLog"}, allEntries = true)
    public void saveSupOrderDeliver(Integer deliveryCompany, String deliveryCompanyName, String deliveryNo,
                                    Integer suplierId, String name, List<IdForm> supOrderIds) throws UpsertException, ResponseEntityException {
        for (IdForm id : supOrderIds){
            SupOrder supOrder = get2(SupOrder.class, "id", id.id, "suplierId", suplierId);
            if (supOrder == null)
                throw new ResponseEntityException(210, "工单不存在");
            if (supOrder.getProducedStatus() != 3)
                throw new ResponseEntityException(220, "工单非生产中");
            supOrder.setProducedStatus(4);
            upsert2(supOrder);

            //创建工单记录
            saveSupOrderDeliveryLog(id.id, "发货","无", name);

            //修改当前物流信息
            updateSupOrderDelivery(id.id, deliveryCompany, deliveryCompanyName, deliveryNo);

            //创建物流记录
            saveSupOrderDeliveryRecord(id.id, deliveryCompany, deliveryCompanyName, deliveryNo);
        }

    }


    /**
     * @Description: 工单修改完成
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @CacheEvict(value = {"SupOrder,SupOrderDeliveryLog"}, allEntries = true)
    public void saveSupOrderModify(Integer deliveryCompany, String deliveryCompanyName, String deliveryNo, String instruction,
                                    Integer suplierId, String name, List<IdForm> supOrderIds) throws UpsertException, ResponseEntityException {
        for (IdForm id : supOrderIds){
            SupOrder supOrder = get2(SupOrder.class, "id", id.id, "suplierId", suplierId);
            if (supOrder == null)
                throw new ResponseEntityException(210, "工单不存在");
            if (supOrder.getProducedStatus() != 6)
                throw new ResponseEntityException(220, "工单非质检不通过");
            supOrder.setProducedStatus(4);
            upsert2(supOrder);

            //创建工单记录
            saveSupOrderDeliveryLog(id.id, "修改完成", instruction, name);

            //修改当前物流信息
            updateSupOrderDelivery(id.id, deliveryCompany, deliveryCompanyName, deliveryNo);

            //创建物流记录
            saveSupOrderDeliveryRecord(id.id, deliveryCompany, deliveryCompanyName, deliveryNo);
        }
    }


    /**
     * @Description: 工单确认取消
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @CacheEvict(value = {"SupOrder,SupOrderDeliveryLog"}, allEntries = true)
    public void saveSupOrderCancel(Integer suplierId, String name, List<IdForm> supOrderIds) throws UpsertException, ResponseEntityException {
        for (IdForm id : supOrderIds){
            SupOrder supOrder = get2(SupOrder.class, "id", id.id, "suplierId", suplierId);
            if (supOrder == null)
                throw new ResponseEntityException(210, "工单不存在");
            if (supOrder.getProducedStatus() != 9)
                throw new ResponseEntityException(220, "工单非取消状态");
            supOrder.setProducedStatus(10);
            upsert2(supOrder);

            //创建工单记录
            saveSupOrderDeliveryLog(id.id, "确认取消","无", name);
        }
    }



    /**
     * @Description: 工单重新修改  —供应商工单
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @CacheEvict(value = {"SupOrder,SupOrderDeliveryLog"}, allEntries = true)
    public void saveSupOrderModifyOffline(Integer deliveryCompany, String deliveryCompanyName, String deliveryNo, String instruction,
                                   Integer suplierId, String name, List<IdForm> supOrderIds) throws UpsertException, ResponseEntityException {
        for (IdForm id : supOrderIds){
            SupOrder supOrder = get2(SupOrder.class, "id", id.id, "suplierId", suplierId);
            if (supOrder == null || supOrder.getOrderType() != 2)
                throw new ResponseEntityException(210, "工单不存在");
            if (supOrder.getProducedStatus() != 4)
                throw new ResponseEntityException(220, "工单非已发货");
            supOrder.setProducedStatus(3);
            upsert2(supOrder);

            //创建工单记录
            saveSupOrderDeliveryLog(id.id, "重新修改", instruction, name);

        }
    }


    /**
     * @Description: 工单完成工单  —供应商工单
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @CacheEvict(value = {"SupOrder,SupOrderDeliveryLog"}, allEntries = true)
    public void saveSupOrderFinishOffline(Integer suplierId, String name, List<IdForm> supOrderIds) throws UpsertException, ResponseEntityException {
        for (IdForm id : supOrderIds){
            SupOrder supOrder = get2(SupOrder.class, "id", id.id, "suplierId", suplierId);
            if (supOrder == null  || supOrder.getOrderType() != 2)
                throw new ResponseEntityException(210, "工单不存在");
            if (supOrder.getProducedStatus() != 4)
                throw new ResponseEntityException(220, "工单非已发货");
            supOrder.setProducedStatus(7);
            upsert2(supOrder);

            //创建工单记录
            saveSupOrderDeliveryLog(id.id, "完成工单","无", name);
        }
    }


    /**
     * @Description: 修改供应商商品编号
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @CacheEvict(value = {"PlaProduct"}, allEntries = true)
    public void saveProductPnoModify(String productCode, String pno) throws UpsertException, ResponseEntityException {
        PlaProduct product = get2(PlaProduct.class, "productCode", productCode);
        if (product == null)
            throw new ResponseEntityException(210, "商品不存在");
        product.setPno(pno);
        upsert2(product);
    }

}
