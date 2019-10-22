package com.xxx.admin.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.dao.SuplierOrderDao;
import com.xxx.admin.form.ChannelForm;
import com.xxx.admin.form.IdForm;
import com.xxx.admin.form.SupOrderListOfflineForm;
import com.xxx.admin.form.SupOrderListOnlineForm;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.ExtFilter;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.*;
import com.xxx.user.Commo;
import com.xxx.user.dao.PropertiesDao;
import com.xxx.user.security.CurrentUser;
import com.xxx.utils.BarcodeUtil;
import com.xxx.utils.Base64ImageUtils;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SupOrderService extends CommonService {

    @Autowired
    private PropertiesDao propertiesDao;
    @Autowired
    private SuplierOrderDao suplierOrderDao;

    /**
     * @Description: 平台工单列表
     * @Author: Chen.zm
     * @Date: 2017/12/4 0004
     */
    public PageList<SupOrder> supOrderListOnline(PageQuery pageQuery, SupOrderListOnlineForm form) {
        Criterion cri = Restrictions.eq("orderType", 1);
        if (form.producedStatus != null)
            cri = Restrictions.and(cri, Restrictions.eq("producedStatus", form.producedStatus));
        if (StringUtils.isNotBlank(form.supOrderNo))
            cri = Restrictions.and(cri, Restrictions.like("orderNo", form.supOrderNo, MatchMode.ANYWHERE));
        JSONArray jsonArray = new JSONArray();
        if (StringUtils.isNotBlank(form.supplierOrderNo)) {
            ExtFilter filter = new ExtFilter("stoSuplierOrder.orderNo", "string", form.supplierOrderNo, ExtFilter.ExtFilterComparison.like, null);
            jsonArray.add(filter);
        }
        if (StringUtils.isNotBlank(form.productCode)) {
            ExtFilter filter = new ExtFilter("supOrderDetail.plaProduct.productCode", "string", form.productCode, ExtFilter.ExtFilterComparison.like, null);
            jsonArray.add(filter);
        }
        if (StringUtils.isNotBlank(form.startTime)) {
            ExtFilter filter = new ExtFilter("createdDate", "date", form.startTime, ExtFilter.ExtFilterComparison.ge, null);
            jsonArray.add(filter);
        }
        if (StringUtils.isNotBlank(form.endTime)) {
            ExtFilter filter = new ExtFilter("createdDate", "date", form.endTime, ExtFilter.ExtFilterComparison.le, null);
            jsonArray.add(filter);
        }
        if (StringUtils.isNotBlank(form.deliveryDateStart)) {
            ExtFilter filter = new ExtFilter("supOrderDelivery.deliveryDate", "date", form.deliveryDateStart, ExtFilter.ExtFilterComparison.ge, null);
            jsonArray.add(filter);
        }
        if (StringUtils.isNotBlank(form.deliveryDateEnd)) {
            ExtFilter filter = new ExtFilter("supOrderDelivery.deliveryDate", "date", form.deliveryDateEnd, ExtFilter.ExtFilterComparison.le, null);
            jsonArray.add(filter);
        }
        pageQuery.filter = jsonArray.toJSONString();
        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "stoSuplierOrder,supOrderDetail,supOrderDetail.plaProduct,supOrderDelivery,supSuplier,supOrderDelivery.plaProductBase";
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<SupOrder> list = hibernateReadonlyRepository.getList(SupOrder.class, pageQuery);
        return list;
    }

    /**
     * @Description: 供应商工单列表
     * @Author: Chen.zm
     * @Date: 2017/12/4 0004
     */
    public PageList<SupOrder> supOrderListOffline(PageQuery pageQuery, SupOrderListOfflineForm form) {
        Criterion cri = Restrictions.eq("orderType", 2);
        if (form.producedStatus != null)
            cri = Restrictions.and(cri, Restrictions.eq("producedStatus", form.producedStatus));
        if (StringUtils.isNotBlank(form.supOrderNo))
            cri = Restrictions.and(cri, Restrictions.like("orderNo", form.supOrderNo, MatchMode.ANYWHERE));
        JSONArray jsonArray = new JSONArray();
        if (StringUtils.isNotBlank(form.pno)) {
            ExtFilter filter = new ExtFilter("supOrderDetail.plaProduct.pno", "string", form.pno, ExtFilter.ExtFilterComparison.like, null);
            jsonArray.add(filter);
        }
        if (StringUtils.isNotBlank(form.customerId)) {
            ExtFilter filter = new ExtFilter("supOrderDelivery.id", "string", form.customerId, ExtFilter.ExtFilterComparison.eq, null);
            jsonArray.add(filter);
        }
        if (StringUtils.isNotBlank(form.startTime)) {
            ExtFilter filter = new ExtFilter("createdDate", "date", form.startTime, ExtFilter.ExtFilterComparison.ge, null);
            jsonArray.add(filter);
        }
        if (StringUtils.isNotBlank(form.endTime)) {
            ExtFilter filter = new ExtFilter("createdDate", "date", form.endTime, ExtFilter.ExtFilterComparison.le, null);
            jsonArray.add(filter);
        }
        if (StringUtils.isNotBlank(form.deliveryDateStart)) {
            ExtFilter filter = new ExtFilter("supOrderDelivery.deliveryDate", "date", form.deliveryDateStart, ExtFilter.ExtFilterComparison.ge, null);
            jsonArray.add(filter);
        }
        if (StringUtils.isNotBlank(form.deliveryDateEnd)) {
            ExtFilter filter = new ExtFilter("supOrderDelivery.deliveryDate", "date", form.deliveryDateEnd, ExtFilter.ExtFilterComparison.le, null);
            jsonArray.add(filter);
        }
        pageQuery.filter = jsonArray.toJSONString();
        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "supOrderDetail,supOrderDelivery,supSuplier,supOrderDelivery.plaProductBase";
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<SupOrder> list = hibernateReadonlyRepository.getList(SupOrder.class, pageQuery);
        return list;
    }

    /**
     * @Description: 获取工单详情
     * @Author: Chen.zm
     * @Date: 2017/12/5 0005
     */
    @Cacheable(value = {"SupOrder,SupOrderDetail,SupOrderDelivery,StoSuplierOrder"})
    public SupOrder getSupOrder(Integer id) {
        Criterion cri = Restrictions.eq("id", id);
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
     * @Description: 创建工单记录
     * @Author: Chen.zm
     * @Date: 2017/12/5 0005
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
     * @Description: 工单记录
     * @Author: Chen.zm
     * @Date: 2017/12/5 0005
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
    public void updateSupOrderRemarks(String remarks, List<IdForm> supOrderIds) throws UpsertException {
        List<Integer> ids = new ArrayList<>();
        for (IdForm id : supOrderIds){
            ids.add(id.id);
            SupOrderRemarkLog log = new SupOrderRemarkLog();
            log.setOrderId(id.id);
            log.setName("合一智造");
            log.setRemarks(remarks);
            upsert2(log);
        }
        String hql = "update SupOrder set remarks = :remarks where id in :ids";
        getCurrentSession().createQuery(hql).setString("remarks", remarks).setParameterList("ids", ids).executeUpdate();
    }

    /**
     * @Description: 分配供应商前校验数据
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    public SupOrder produceSupplier(Integer supOrderId) throws UpsertException,ResponseEntityException {
        if (supOrderId == null)
            throw new ResponseEntityException(200, "工单id不能为空");
        SupOrder supOrder = getSupOrder(supOrderId);
        if (supOrder == null || supOrder.getSupOrderDetail() == null || supOrder.getSupOrderDetail().getPlaProduct() == null)
            throw new ResponseEntityException(210, "工单数据异常");
        if (supOrder.getSupOrderDetail().getPlaProduct().getSupSuplier() == null)
            throw new ResponseEntityException(220, "该工单商品暂不存在供应商");
        return supOrder;
    }

    /**
     * @Description: 分配供应商
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    public void saveSupOrderSupplier(Integer supOrderId, Date deliveryDate, String description) throws UpsertException,ResponseEntityException {
        SupOrder supOrder = produceSupplier(supOrderId);
        SupOrderDetail supOrderDetail = supOrder.getSupOrderDetail();
        SupOrderDelivery supOrderDelivery = supOrder.getSupOrderDelivery();
        //持久化供应商信息
        if (StringUtils.isBlank(supOrderDetail.getPlaProduct().getSupSuplier().getCode()))
            throw new ResponseEntityException(210, "供应商编号不存在");
        if (supOrderDetail.getPlaProduct().getSuplierDay() == null || supOrderDetail.getPlaProduct().getSuplierDay() == 0)
            throw new ResponseEntityException(220, "商品供货周期为空");
        supOrder.setOrderNo(propertiesDao.generateSupOrderNumber(supOrderDetail.getPlaProduct().getSupSuplier().getCode()));
        supOrder.setSuplierId(supOrderDetail.getPlaProduct().getSuplierId());
        supOrder.setDescription(description);
        supOrder.setProducedStatus(2);
        upsert2(supOrder);
        //持久化采购价
        supOrderDetail.setOutputPrice(supOrderDetail.getPlaProduct().getSuplierPrice());
        supOrderDetail.setPno(supOrderDetail.getPlaProduct().getPno());
        upsert2(supOrderDetail);
        //持久化交货日期
        supOrderDelivery.setDeliveryDate(deliveryDate);
        supOrderDelivery.setCustomer("合一智造");
        if (deliveryDate == null)
            supOrderDelivery.setDeliveryDate(DateTimeUtils.getDateByAddDay(new Date(), supOrderDetail.getPlaProduct().getSuplierDay().intValue() - 1));
        upsert2(supOrderDelivery);
    }

    /**
     * @Description: 批量分配供应商
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    public void saveSupOrderSupplierList(List<IdForm> supOrderIds) throws UpsertException, ResponseEntityException {
        for (IdForm id : supOrderIds){
            saveSupOrderSupplier(id.id, null, null);
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


    /**
     * @Description: 工单取消
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @CacheEvict(value = {"SupOrder,SupOrderDeliveryLog"}, allEntries = true)
    public void saveSupOrderCancel(String instruction, String name, List<IdForm> supOrderIds) throws UpsertException, ResponseEntityException {
        for (IdForm id : supOrderIds){
            SupOrder supOrder = get2(SupOrder.class, "id", id.id);
            if (supOrder == null)
                throw new ResponseEntityException(210, "工单不存在");
            if (supOrder.getProducedStatus() != 2 && supOrder.getProducedStatus() != 3)
                throw new ResponseEntityException(220, "工单不可取消");
            supOrder.setProducedStatus(9);
            upsert2(supOrder);

            //创建工单记录
            saveSupOrderDeliveryLog(id.id, "取消工单", instruction, name);
        }
    }


    /**
     * @Description: 平台工单各状态及数量
     * @Author: Chen.zm
     * @Date: 2017/12/7 0007
     */
    @Cacheable(value = {"SupOrder"})
    public JSONArray producedStatusCountOnline() {
        List<JSONObject> list = suplierOrderDao.producedStatusCount( 1);
        //将工单数量转为map形式
        Map<Integer, Integer> map = new HashMap<>();
        for (JSONObject json : list) {
            map.put(json.getInteger("producedStatus"), json.getInteger("count"));
        }
        JSONArray data = new JSONArray();
        for (int i=1; i<=10; i++) {
            JSONObject json = new JSONObject();
            json.put("type", i);
            json.put("name", Commo.parseSuplierOrderProducedStatus(i));
            json.put("count", map.get(i) == null ? 0 : map.get(i));
            data.add(json);
        }
        return data;
    }


    /**
     * @Description: 供应商工单各状态及数量
     * @Author: Chen.zm
     * @Date: 2017/12/7 0007
     */
    @Cacheable(value = {"SupOrder"})
    public JSONArray producedStatusCountOffline() {
        List<JSONObject> list = suplierOrderDao.producedStatusCount( 2);
        //将工单数量转为map形式
        Map<Integer, Integer> map = new HashMap<>();
        for (JSONObject json : list) {
            map.put(json.getInteger("producedStatus"), json.getInteger("count"));
        }
        //生产状态 2：待生产  3：生产中   4：已发货  7：已完成
        JSONArray data = new JSONArray();
        for (int i=2; i<=7; i++) {
            JSONObject json = new JSONObject();
            json.put("type", i);
            json.put("name", Commo.parseSuplierOrderProducedStatusOffline(i));
            json.put("count", map.get(i) == null ? 0 : map.get(i));
            data.add(json);
            if (i == 4) i = 6;//屏蔽5,6状态
        }
        return data;
    }
    /**
     * @Description:供应商工单客户名称获取
     * @Author: hanchao
     * @Date: 2017/12/11 0011
     */
    @Cacheable(value = {"SupOrderDelivery"})
    public  List<SupOrderDelivery> producedCustomerOffline() {
        return (List<SupOrderDelivery>) getCurrentSession().createCriteria(SupOrderDelivery.class)
                .list();
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
     * @Description: 获取平台word导出数据源  —— 平台
     * @Author: Chen.zm
     * @Date: 2017/12/8 0008
     */
    public Map<String, Object> exportWordOnline(SupOrder supOrder) throws Exception{
        Map<String, Object> map = new HashMap<>();
        map.put("supOrderNo", supOrder.getOrderNo());
        map.put("customer", supOrder.getSupOrderDelivery().getCustomer());
        map.put("productCode", supOrder.getSupOrderDetail().getPlaProduct().getProductCode());
        map.put("pno", supOrder.getSupOrderDetail().getPno());
        map.put("supOrderDate", DateTimeUtils.parseStr(supOrder.getCreatedDate(), "yyyy-MM-dd"));
        map.put("deliveryDate", DateTimeUtils.parseStr(supOrder.getSupOrderDelivery().getDeliveryDate(), "yyyy-MM-dd"));
        map.put("color", supOrder.getSupOrderDetail().getColor());
        map.put("size", supOrder.getSupOrderDetail().getSize());
        map.put("throatheight",supOrder.getSupOrderDetail().getThroatheight());
        map.put("shoulder",supOrder.getSupOrderDetail().getShoulder());
        map.put("bust",supOrder.getSupOrderDetail().getBust());
        map.put("waist",supOrder.getSupOrderDetail().getWaist());
        map.put("hipline",supOrder.getSupOrderDetail().getHipline());
        map.put("height",supOrder.getSupOrderDetail().getHeight());
        map.put("weight",supOrder.getSupOrderDetail().getWeight());
        map.put("material", supOrder.getSupOrderDetail().getPlaProduct().getMaterial());
        map.put("technics", supOrder.getSupOrderDetail().getPlaProduct().getTechnics());
        map.put("description", supOrder.getDescription());
        map.put("barCode", BarcodeUtil.generateBase64(supOrder.getOrderNo()));
        for (String key : map.keySet()) {
            if (map.get(key) == null) map.put(key, "");
        }
        map.put("img1", "");
        map.put("img2", "");
        map.put("img3", "");
        map.put("img4", "");
        map.put("img5", "");

        int i = 1;
        for (PlaProductPicture picture: supOrder.getSupOrderDetail().getPlaProduct().getPlaProductPictureList()) {
            //word图片占位符
            StringBuffer str = new StringBuffer();
            str.append("<w:binData w:name=\"wordml://02000007" + i + ".jpg\" xml:space=\"preserve\">");
            str.append(Base64ImageUtils.getImageBase64FromUrl(OSSClientUtil.getObjectUrl(picture.getHref())));
            str.append("</w:binData><v:shape id=\"_x0000_i1030\" type=\"#_x0000_t75\" style=\"width:191.6pt;height:166.55pt\">" +
                    "<v:imagedata src=\"wordml://02000007" + i + ".jpg\" o:title=\"5\"/></v:shape>");
            map.put("img" + i, str);
            i++;
        }
        return map;
    }

    /**
     * @Description: 批量获取平台word数据源 —— 平台
     * @Author: Chen.zm
     * @Date: 2017/12/18 0018
     */
    public List<Map> exportWordListOnline(List<Integer> supOrderIds) throws Exception{
        Criterion cri = Restrictions.in("id", supOrderIds);
        List<SupOrder> supOrderList = getCurrentSession().createCriteria(SupOrder.class)
                .add(cri)
                .setFetchMode("supOrderDetail", FetchMode.JOIN)
                .list();
        List<Map> list = new ArrayList<>();
        for (SupOrder supOrder : supOrderList) {
            Map<String, Object> map = exportWordOnline(supOrder);
            map.put("wordName", supOrder.getOrderNo());
            list.add(map);
        }
        return list;
    }


    /**
     * @Description: 获取word导出数据源 —— 供应商
     * @Author: Chen.zm
     * @Date: 2017/12/8 0008
     */
    public Map<String, Object> exportWordOffline(SupOrder supOrder) throws Exception{
        Map<String, Object> map = new HashMap<>();
        map.put("supOrderNo", supOrder.getOrderNo());
        map.put("customer", supOrder.getSupOrderDelivery().getCustomer());
        map.put("productCode", "");
        map.put("pno", supOrder.getSupOrderDetail().getPno());
        map.put("supOrderDate", DateTimeUtils.parseStr(supOrder.getCreatedDate(), "yyyy-MM-dd"));
        map.put("deliveryDate", DateTimeUtils.parseStr(supOrder.getSupOrderDelivery().getDeliveryDate(), "yyyy-MM-dd"));
        map.put("color", supOrder.getSupOrderDetail().getColor());
        map.put("size", supOrder.getSupOrderDetail().getSize());
        map.put("throatheight",supOrder.getSupOrderDetail().getThroatheight());
        map.put("shoulder",supOrder.getSupOrderDetail().getShoulder());
        map.put("bust",supOrder.getSupOrderDetail().getBust());
        map.put("waist",supOrder.getSupOrderDetail().getWaist());
        map.put("hipline",supOrder.getSupOrderDetail().getHipline());
        map.put("height",supOrder.getSupOrderDetail().getHeight());
        map.put("weight",supOrder.getSupOrderDetail().getWeight());
        map.put("material", supOrder.getSupOrderDetail().getMaterial());
        map.put("technics", supOrder.getSupOrderDetail().getTechnics());
        map.put("description", supOrder.getDescription());
        map.put("barCode", BarcodeUtil.generateBase64(supOrder.getOrderNo()));
        for (String key : map.keySet()) {
            if (map.get(key) == null) map.put(key, "");
        }
        map.put("img1", "");
        map.put("img2", "");
        map.put("img3", "");
        map.put("img4", "");
        map.put("img5", "");

        int i = 1;
        for (SupOrderDetailPicture picture: supOrder.getSupOrderDetail().getSupOrderDetailPictureList()) {
            //word图片占位符
            StringBuffer str = new StringBuffer();
            str.append("<w:binData w:name=\"wordml://02000007" + i + ".jpg\" xml:space=\"preserve\">");
            str.append(Base64ImageUtils.getImageBase64FromUrl(OSSClientUtil.getObjectUrl(picture.getHref())));
            str.append("</w:binData><v:shape id=\"_x0000_i1030\" type=\"#_x0000_t75\" style=\"width:191.6pt;height:166.55pt\">" +
                    "<v:imagedata src=\"wordml://02000007" + i + ".jpg\" o:title=\"5\"/></v:shape>");
            map.put("img" + i, str);
            i++;
        }
        return map;
    }

    /**
     * @Description: 批量获取word数据源 —— 供应商
     * @Author: Chen.zm
     * @Date: 2017/12/18 0018
     */
    public List<Map> exportWordListOffline(List<Integer> supOrderIds) throws Exception{
        Criterion cri = Restrictions.in("id", supOrderIds);
        List<SupOrder> supOrderList = getCurrentSession().createCriteria(SupOrder.class)
                .add(cri)
                .setFetchMode("supOrderDetail", FetchMode.JOIN)
                .list();
        List<Map> list = new ArrayList<>();
        for (SupOrder supOrder : supOrderList) {
            Map<String, Object> map = exportWordOffline(supOrder);
            map.put("wordName", supOrder.getOrderNo());
            list.add(map);
        }
        return list;
    }

}
