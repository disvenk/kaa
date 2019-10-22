package com.xxx.suplier.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.ExtFilter;
import com.xxx.core.query.ListUtils;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.*;
import com.xxx.suplier.form.*;
import com.xxx.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Service
public class ProduceRecordService extends CommonService {


    /**
     * @Description:批量修改供应商产品编号
     * @Author: hanchao
     * @Date: 2017/12/14 0014
     */
    @CacheEvict(value = {"SupOrder,SupOrderDetail"}, allEntries = true)
    public void updateSupOrderPno(Integer suplierId, List<IdForm> supOrderIds,String pno) throws UpsertException, ResponseEntityException {
        for (IdForm id : supOrderIds){
            SupOrder supOrder = get2(SupOrder.class, "id", id.id, "suplierId", suplierId);
            if (supOrder == null)
                throw new ResponseEntityException(210, "工单不存在");
            supOrder.getSupOrderDetail().setPno(pno);
            upsert2(supOrder);
        }
    }

    /**
     * @Description:保存工厂工人生产记录
     * @Author: hanchao
     * @Date: 2017/12/4 0004
     */
    @CacheEvict(value = {"SupOrder,SupOrderProductionLog"}, allEntries = true)
    public void saveproduceRecord(Integer supplierId, ProduceRecordEditForm form) throws UpsertException, ResponseEntityException, ParseException {
        SupOrder supOrder = get2(SupOrder.class, "id", form.id, "suplierId", supplierId);
        if (supOrder == null)
            throw new ResponseEntityException(120, "生产记录不存在");
        if (form.produceRecordStationTypeList.size() != 0) {
            String hql = "delete SupOrderProductionLog where orderId =:orderId";
            getCurrentSession().createQuery(hql).setInteger("orderId", supOrder.getId()).executeUpdate();
            for (ProduceRecordStationTypeForm produceRecordStationTypeForm : form.produceRecordStationTypeList) {
                SupOrderProductionLog supOrderProductionLog = new SupOrderProductionLog();
                if(produceRecordStationTypeForm.workerId != null){
                    supOrderProductionLog.setDetailProcedureId(produceRecordStationTypeForm.detailProcedureId);
                    supOrderProductionLog.setOrderId(supOrder.getId());
                    supOrderProductionLog.setWorkerId(produceRecordStationTypeForm.workerId);
                    supOrderProductionLog.setProductionDate(DateTimeUtils.parseDate(produceRecordStationTypeForm.updateDate,"yyyy-MM-dd HH:mm:ss"));
                    supOrderProductionLog = upsert2(supOrderProductionLog);
                    supOrder.setProductionLogId(supOrderProductionLog.getId());
                    upsert2(supOrder);
                }
                }
            }
    }
    /**
     * @Description:获取所有工人名称
     * @Author: hanchao
     * @Date: 2017/12/6 0006
     */
    @Cacheable(value = {"SupWorker"})
    public List<SupWorker> getAllWorkerName(Integer supplierId) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        cri = Restrictions.eq("suplierId", supplierId);
        List<SupWorker> supWorker = (List<SupWorker>) getCurrentSession().createCriteria(SupWorker.class)
                .add(cri)
                .list();
        return supWorker;
    }
    /**
     * @Description:获取所有工人Id
     * @Author: hanchao
     * @Date: 2017/12/6 0006
     */
    @Cacheable(value = {"SupWorkerStation"})
    public SupWorkerStation getWorkId(String name) {
        Criterion cri = Restrictions.eq("name", name);
        SupWorkerStation supWorker = (SupWorkerStation) getCurrentSession().createCriteria(SupWorkerStation.class)
                .add(cri)
                .uniqueResult();
        return supWorker;
    }

    /**
     * @Description:工厂工人生产记录编辑详情
     * @Author: hanchao
     * @Date: 2017/12/4 0004
     */
    @Cacheable(value = {"SupOrder,SupOrderProductionLog"})
    public SupOrder findProduceRecordDateil(Integer id) {
        Criterion cri = Restrictions.eq("id", id);
        SupOrder supOrder = (SupOrder) getCurrentSession().createCriteria(SupOrder.class)
                .add(cri)
                .setFetchMode("supOrderProductionLogList", FetchMode.JOIN)
                .uniqueResult();
        return supOrder;
    }

    /**
     * @Description:获取工厂工人维护列表
     * @Author: hanchao
     * @Date: 2017/12/4 0004
     */
    @Cacheable(value = {"SupOrder,SupOrderProductionLog,SupOrderDetail"})
    public PageList<SupOrder> findProduceRecordList(PageQuery pageQuery, String orderNo, String supplierProductCode, String procedureType, Integer producedStatus,String productCode,Integer timeStatus, Integer suplierId) throws ParseException {
        Criterion cri = Restrictions.eq("suplierId", suplierId);
        if(StringUtils.isNotBlank(orderNo))
            cri = Restrictions.and(cri, Restrictions.like("orderNo", orderNo, MatchMode.ANYWHERE));
        JSONArray jsonArray = new JSONArray();
        if (StringUtils.isNotBlank(supplierProductCode)) {
            ExtFilter filter = new ExtFilter("supOrderDetail.pno", "string", supplierProductCode, ExtFilter.ExtFilterComparison.like, null);
            jsonArray.add(filter);
        }
        if (StringUtils.isNotBlank(productCode)) {
            ExtFilter filter = new ExtFilter("supOrderDetail.plaProduct.productCode", "string", productCode, ExtFilter.ExtFilterComparison.like, null);
            jsonArray.add(filter);
        }
        if(timeStatus != null){
            Date date = new Date((new Date().getTime() - 3600 * 24 * 1000));
            ExtFilter filter = new ExtFilter("supOrderProductionLog.productionDate", "date", DateTimeUtils.parseStr(date), ExtFilter.ExtFilterComparison.le, null);
            jsonArray.add(filter);
        }
        if (StringUtils.isNotBlank(procedureType)) {
            ExtFilter filter = new ExtFilter("supOrderProductionLog.supSalesOrderDetailProcedure.procedureId", "string", procedureType, ExtFilter.ExtFilterComparison.eq, null);
            jsonArray.add(filter);
        }
        if(producedStatus != null)
            cri = Restrictions.and(cri, Restrictions.eq("producedStatus", producedStatus));
        pageQuery.filter = jsonArray.toJSONString();
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<SupOrder> list = hibernateReadonlyRepository.getList(SupOrder.class, pageQuery);
        for (SupOrder supOrder : list) {
            for (SupOrderProductionLog supOrderProductionLog : supOrder.getSupOrderProductionLogList());
        }
        return ListUtils.removeDuplicateWithOrder(list);
    }








}
