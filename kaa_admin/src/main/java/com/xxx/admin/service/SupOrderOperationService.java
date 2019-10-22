package com.xxx.admin.service;


import com.alibaba.fastjson.JSONArray;
import com.xxx.admin.form.OrderNoListForm;
import com.xxx.admin.form.SupOrderOperationForm;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.ExtFilter;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.*;
import com.xxx.user.security.CurrentUser;
import com.xxx.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.text.ParseException;
import java.util.Date;

@Service
public class SupOrderOperationService extends CommonService {

    @Autowired
    private SupOrderService supOrderService;

    /**
     * @Description:入库通过操作
     * @Author: hanchao
     * @Date: 2017/12/5 0005
     */
    @CacheEvict(value = {"SupOrderStorageLog"}, allEntries = true)
    public void saveSupOrderStorage( SupOrderOperationForm form) throws UpsertException, ResponseEntityException {
        for(OrderNoListForm orderNoList : form.orderNoList){
            SupOrder supOrder = get2(SupOrder.class,"orderNo",orderNoList.orderNo);
            if(supOrder == null)
                throw new ResponseEntityException(210, "工单不存在");
            if (supOrder.getProducedStatus() != 7)
                throw new ResponseEntityException(220, "工单不可入库");
            supOrder.setProducedStatus(8);//状态改为入库成功
            upsert2(supOrder);

            //创建质检记录
            saveSupOrderStorageLog(supOrder.getId(),orderNoList.orderNo,"合一智造");

            //创建工单记录
            supOrderService.saveSupOrderDeliveryLog(supOrder.getId(), "入库", "入库通过", "合一智造");
        }
    }

    /**
     * @Description:创建入库记录
     * @Author: hanchao
     * @Date: 2017/12/12 0012
     */
    @CacheEvict(value = {"SupOrderStorageLog"}, allEntries = true)
    public void saveSupOrderStorageLog(Integer orderId, String orderNo, String name) throws UpsertException, ResponseEntityException {
        SupOrderStorageLog supOrderStorageLog = new SupOrderStorageLog();
        supOrderStorageLog.setOrderId(orderId);
        supOrderStorageLog.setOrderNo(orderNo);
        supOrderStorageLog.setName(name);
        upsert2(supOrderStorageLog);
    }

    /**
     * @Description:获取入库列表页面
     * @Author: hanchao
     * @Date: 2017/12/6 0006
     */
    @Cacheable(value = {"SupOrder,SupOrderTestingLog,SupOrderDetail"})
    public PageList<SupOrderStorageLog> findSupOrderStorageList(PageQuery pageQuery, String orderNo, String name, String startTime, String endTime) throws ParseException {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if(StringUtils.isNotBlank(orderNo))
            cri = Restrictions.and(cri, Restrictions.like("orderNo", orderNo, MatchMode.ANYWHERE));
        if(StringUtils.isNotBlank(name))
            cri = Restrictions.and(cri, Restrictions.like("name", name, MatchMode.ANYWHERE));
        if(StringUtils.isNotBlank(startTime))
            cri = Restrictions.and(cri, Restrictions.ge("updateDate", DateTimeUtils.parseDate(startTime,"yyyy-MM-dd")));
        if(StringUtils.isNotBlank(endTime))
            cri = Restrictions.and(cri, Restrictions.le("updateDate", DateTimeUtils.parseDate(endTime,"yyyy-MM-dd")));

        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<SupOrderStorageLog> list = hibernateReadonlyRepository.getList(SupOrderStorageLog.class, pageQuery);
        return list;
    }
    /**
     * @Description:质检通过操作
     * @Author: hanchao
     * @Date: 2017/12/5 0005
     */
    @CacheEvict(value = {"SupOrderTestingLog"}, allEntries = true)
    public void saveOrderTest( SupOrderOperationForm form) throws UpsertException, ResponseEntityException {
        for(OrderNoListForm orderNoList : form.orderNoList){
            SupOrder supOrder = get2(SupOrder.class,"orderNo",orderNoList.orderNo);
            if(supOrder == null)
                throw new ResponseEntityException(210, "工单不存在");
            if (supOrder.getProducedStatus() != 5)
                throw new ResponseEntityException(220, "工单不可质检");
            supOrder.setProducedStatus(7);//状态改为质检通过
            upsert2(supOrder);

            //创建质检通过记录
            saveSupOrderTestingLog(supOrder.getId(),orderNoList.orderNo,"合一智造");

            //创建工单记录
            supOrderService.saveSupOrderDeliveryLog(supOrder.getId(), "质检", "质检通过", "合一智造");
        }
    }

    /**
     * @Description:质检不通过操作
     * @Author: hanchao
     * @Date: 2017/12/5 0005
     */
    @CacheEvict(value = {"SupOrderTestingLog"}, allEntries = true)
    public void saveOrderTestFail( SupOrderOperationForm form) throws UpsertException, ResponseEntityException {
        for(OrderNoListForm orderNoList : form.orderNoList){
            SupOrder supOrder = get2(SupOrder.class,"orderNo",orderNoList.orderNo);
            if(supOrder == null)
                throw new ResponseEntityException(210, "工单不存在");
            if (supOrder.getProducedStatus() != 5)
                throw new ResponseEntityException(220, "工单不可质检");
            supOrder.setProducedStatus(6);//状态改为质检不通过
            upsert2(supOrder);

            //创建工单记录
            supOrderService.saveSupOrderDeliveryLog(supOrder.getId(), "质检", "质检不通过", "合一智造");
        }
    }

    /**
     * @Description:创建质检记录
     * @Author: hanchao
     * @Date: 2017/12/12 0012
     */
    @CacheEvict(value = {"SupOrderTestingLog"}, allEntries = true)
    public void saveSupOrderTestingLog(Integer orderId, String orderNo, String name) throws UpsertException, ResponseEntityException {
        SupOrderTestingLog supOrderTestingLog = new SupOrderTestingLog();
        supOrderTestingLog.setOrderId(orderId);
        supOrderTestingLog.setOrderNo(orderNo);
        supOrderTestingLog.setName(name);
        upsert2(supOrderTestingLog);
    }



    /**
     * @Description:获取质检操作列表
     * @Author: hanchao
     * @Date: 2017/12/4 0004
     */
    @Cacheable(value = {"SupOrder,SupOrderTestingLog,SupOrderDetail"})
    public PageList<SupOrderTestingLog> findSupOrderTestList(PageQuery pageQuery, String orderNo, String name, String startTime, String endTime) throws ParseException {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if(StringUtils.isNotBlank(orderNo))
            cri = Restrictions.and(cri, Restrictions.like("orderNo", orderNo, MatchMode.ANYWHERE));
        if(StringUtils.isNotBlank(name))
            cri = Restrictions.and(cri, Restrictions.like("name", name, MatchMode.ANYWHERE));
        if(StringUtils.isNotBlank(startTime))
            cri = Restrictions.and(cri, Restrictions.ge("updateDate",  DateTimeUtils.parseDate(startTime,"yyyy-MM-dd")));
        if(StringUtils.isNotBlank(endTime))
            cri = Restrictions.and(cri, Restrictions.le("updateDate", DateTimeUtils.parseDate(endTime,"yyyy-MM-dd")));
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<SupOrderTestingLog> list = hibernateReadonlyRepository.getList(SupOrderTestingLog.class, pageQuery);
        return list;
    }

    /**
     * @Description:获取工单收货列表
     * @Author: hanchao
     * @Date: 2017/12/4 0004
     */
    @Cacheable(value = {"SupOrder,SupOrderProductionLog,SupOrderDetail"})
    public PageList<SupOrderReceiptLog> findSupOrderReceiptLogList(PageQuery pageQuery, String orderNo, String name, String startTime, String endTime) throws ParseException {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if(StringUtils.isNotBlank(orderNo))
            cri = Restrictions.and(cri, Restrictions.eq("orderNo", orderNo));
        if(StringUtils.isNotBlank(name))
            cri = Restrictions.and(cri, Restrictions.like("name", name, MatchMode.ANYWHERE));
        if(StringUtils.isNotBlank(startTime))
            cri = Restrictions.and(cri, Restrictions.ge("updateDate", DateTimeUtils.parseDate(startTime,"yyyy-MM-dd")));
        if(StringUtils.isNotBlank(endTime))
            cri = Restrictions.and(cri, Restrictions.le("updateDate", DateTimeUtils.parseDate(endTime,"yyyy-MM-dd")));
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<SupOrderReceiptLog> list = hibernateReadonlyRepository.getList(SupOrderReceiptLog.class, pageQuery);
        return list;
    }

    /**
     * @Description:工单收货保存操作
     * @Author: hanchao
     * @Date: 2017/12/5 0005
     */
    @CacheEvict(value = {"supOrder"}, allEntries = true)
    public void saveWorkOrder(SupOrderOperationForm form) throws UpsertException, ResponseEntityException {
        for(OrderNoListForm orderNoList : form.orderNoList){
            SupOrder supOrder = get2(SupOrder.class,"orderNo",orderNoList.orderNo);
            if (supOrder == null)
                throw new ResponseEntityException(210, "工单不存在");
            if (supOrder.getProducedStatus() != 4)
                throw new ResponseEntityException(220, "工单不可收货");
            supOrder.setProducedStatus(5);//状态改为已发货
            upsert2(supOrder);

            //创建收货记录
            saveSupOrderReceiptLog(supOrder.getId(), supOrder.getOrderNo(), "合一智造");

            //创建工单记录
            supOrderService.saveSupOrderDeliveryLog(supOrder.getId(), "收货", "收货成功", "合一智造");
        }
    }

    /**
     * @Description: 创建工单收货记录
     * @Author: Chen.zm
     * @Date: 2017/12/12 0012
     */
    @CacheEvict(value = {"SupOrderReceiptLog"}, allEntries = true)
    public void saveSupOrderReceiptLog(Integer orderId, String orderNo, String name) throws UpsertException, ResponseEntityException {
        SupOrderReceiptLog supOrderReceiptLog = new SupOrderReceiptLog();
        supOrderReceiptLog.setOrderId(orderId);
        supOrderReceiptLog.setOrderNo(orderNo);
        supOrderReceiptLog.setName(name);
        upsert2(supOrderReceiptLog);
    }

    /**
     * @Description:工单号扫码条件
     * @Author: hanchao
     * @Date: 2017/12/6 0006
     */
    @Cacheable(value = {"SupOrder"})
    public SupOrder selectWorkOrder(String orderno) {
        SupOrder supOrder = (SupOrder) getCurrentSession().createCriteria(SupOrder.class)
                .add(Restrictions.eq("orderNo", orderno))
                .uniqueResult();
        return supOrder;
    }


}
