package com.xxx.suplier.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.ListUtils;
import com.xxx.core.query.MybatisPageQuery;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.*;
import com.xxx.suplier.dao.SuplierSalesOrderDao;
import com.xxx.suplier.form.*;
import com.xxx.user.Commo;
import com.xxx.utils.Arith;
import com.xxx.utils.DateTimeUtils;
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
import java.util.*;

@Service
public class SuplierSalesOrderService extends CommonService {

    @Autowired
    private SuplierOrderService suplierOrderService;
    @Autowired
    private SuplierSalesOrderDao suplierSalesOrderDao;

    @Autowired
    private ProductBaseService productBaseService;
    @Autowired
    private ProductProcedureService productProcedureService;


    /**
     * @Description: 订单列表
     * @Author: Chen.zm
     * @Date: 2018/1/9 0009
     */
    public PageList<JSONObject> findList(MybatisPageQuery pageQuery) {
        return suplierSalesOrderDao.findList(pageQuery);
    }

    /**
     * @Description: 获取订单状态各数量
     * @Author: Chen.zm
     * @Date: 2018/1/9 0009
     */
    public JSONArray findOrderCount(Integer suplierId) {
        List<JSONObject> list = suplierSalesOrderDao.findOrderCount(suplierId);
        //将工单数量转为map形式
        Map<Integer, Integer> map = new HashMap<>();
        for (JSONObject json : list) {
            map.put(json.getInteger("producedStatus"), json.getInteger("orderCount"));
        }
        //生产状态 2：待生产  3：生产中   4：已发货  7：已完成
        JSONArray data = new JSONArray();
        for (int i=2; i<=7; i++) {
            JSONObject json = new JSONObject();
            json.put("type", i);
            json.put("name", Commo.parseSuplierOrderProducedStatusOffline(i));
            json.put("count", map.get(i) == null ? "0" : map.get(i).toString());
            data.add(json);
            if (i == 4) i = 6;//屏蔽5,6状态
        }
        return data;
    }

    /**
     * @Description: 订单详情
     * @Author: Chen.zm
     * @Date: 2018/1/9 0009
     */
    public SupSalesOrder getSupSalesOrderDetail(Integer id, Integer suplierId) {
        Criterion cri = Restrictions.eq("id", id);
        cri = Restrictions.and(cri, Restrictions.eq("suplierId", suplierId));
        return (SupSalesOrder) getCurrentSession().createCriteria(SupSalesOrder.class)
                .add(cri)
                .setFetchMode("supSalesOrderDetail", FetchMode.JOIN)
                .setFetchMode("supSalesOrderDelivery", FetchMode.JOIN)
                .uniqueResult();
    }

    /**
     * @Description: 保存订单
     * @Author: Chen.zm
     * @Date: 2018/1/9 0009
     */
    public void saveOrder(SuplierSalesOrderForm form, Integer suplierId) throws ResponseEntityException, UpsertException, ParseException{
        SupSalesOrder salesOrder;
        if (form.id != null) {
            salesOrder = get2(SupSalesOrder.class, "id", form.id, "suplierId", suplierId);
            if (salesOrder == null)
                throw new ResponseEntityException(210, "订单不存在");
        } else {
            salesOrder = new SupSalesOrder();
            salesOrder.setSuplierId(suplierId);
            if (StringUtils.isBlank(form.orderNo))
                throw new ResponseEntityException(220, "订单号不能为空");
            //校验订单号是否存在
            SupSalesOrder so = get2(SupSalesOrder.class, "orderNo", form.orderNo, "suplierId", suplierId);
            if (so != null)
                throw new ResponseEntityException(230, "订单号已存在");
            salesOrder.setOrderNo(form.orderNo);
            salesOrder.setProducedStatus(2);
        }
        salesOrder.setInsideOrderNo(form.insideOrderNo);
        salesOrder.setRemarks(form.remarks);
        salesOrder.setDescription(form.description);
        salesOrder.setTotal(Arith.mul(form.qty, form.price));
        salesOrder = upsert2(salesOrder);

        //校验客户信息
        SupCustomer customer = get2(SupCustomer.class, "id", form.customerId, "suplierId", suplierId);
        if (customer == null)
            throw new ResponseEntityException(240, "客户不存在");
        //校验客户地址信息
        SupCustomerAddress address = get2(SupCustomerAddress.class, "id", form.customerAddressId, "customerId", form.customerId);
        if (address == null)
            throw new ResponseEntityException(260, "客户地址不存在");


        //保存订单收件信息
        SupSalesOrderDelivery delivery = get2(SupSalesOrderDelivery.class, "orderId", salesOrder.getId());
        if (delivery == null) {
            delivery = new SupSalesOrderDelivery();
            delivery.setOrderId(salesOrder.getId());
        }
        delivery.setCustomerId(form.customerId);
//        delivery.setCustomer(customer.getCustomer());
        delivery.setCustomer(customer.getCustomer());
        delivery.setCustomerPhone(form.customerPhone);
        delivery.setContact(form.customer);
        delivery.setCustomerAddressId(form.customerAddressId);

        delivery.setReceiver(address.getReceiver());
        delivery.setMobile(address.getMobile());
        delivery.setProvince(address.getProvince());
        delivery.setProvinceName(address.getProvinceName());
        delivery.setCity(address.getCity());
        delivery.setCityName(address.getCityName());
        delivery.setZone(address.getZone());
        delivery.setZoneName(address.getZoneName());
        delivery.setAddress(address.getAddress());
        delivery.setDeliveryDate(DateTimeUtils.parseDate(form.deliveryTime, "yyyy-MM-dd"));
        upsert2(delivery);

        //校验供应商商品信息
        SupProduct product = get2(SupProduct.class, "id", form.pid, "suplierId", suplierId);
        if (product == null)
            throw new ResponseEntityException(270, "商品不存在");

        //保存订单详情
        SupSalesOrderDetail detail = get2(SupSalesOrderDetail.class, "orderId", salesOrder.getId());
        if (detail == null) {
            detail = new SupSalesOrderDetail();
            detail.setOrderId(salesOrder.getId());
        }
        detail.setPid(form.pid);
        detail.setPno(product.getPno());
        detail.setMaterial(form.material);
        detail.setTechnics(form.technics);
        if (form.productPictureList.size() != 0) {
            detail.setHref(form.productPictureList.get(0).href);
        }
        detail.setProductName(product.getName());
        detail.setCategoryName(form.categoryName);
        detail.setColor(form.color);
        detail.setSize(form.size);
        detail.setShoulder(form.shoulder);
        detail.setBust(form.bust);
        detail.setWaist(form.waist);
        detail.setHipline(form.hipline);
        detail.setHeight(form.height);
        detail.setWeight(form.weight);
        detail.setThroatheight(form.throatheight);
        detail.setQty(form.qty);
        detail.setPrice(form.price);
        detail.setSubtotal(Arith.mul(form.qty, form.price));
        detail = upsert2(detail);

        //保存图片集合
        String hql = "delete SupSalesOrderDetailPicture where detailId =:detailId";
        getCurrentSession().createQuery(hql).setInteger("detailId", detail.getId()).executeUpdate();
        for (ProductPictureForm f : form.productPictureList) {
            SupSalesOrderDetailPicture picture = new SupSalesOrderDetailPicture();
            picture.setDetailId(detail.getId());
            picture.setHref(f.href);
            upsert2(picture);
        }

        //保存原材料集合
        hql = "delete SupSalesOrderDetailMaterial where detailId =:detailId";
        getCurrentSession().createQuery(hql).setInteger("detailId", detail.getId()).executeUpdate();
        for (ProductMaterialForm f : form.materialList) {
            //校验原材料是否存在
            if (f.materialId == null) {
                CategorySaveForm fo = new CategorySaveForm();
                fo.name = f.materialName;
                fo.type = 4;
                f.materialId = productBaseService.addProductBase(fo).getId();
            }

            SupSalesOrderDetailMaterial material = new SupSalesOrderDetailMaterial();
            material.setDetailId(detail.getId());
            material.setMaterialId(f.materialId);
            material.setMaterialName(f.materialName);
            material.setPrice(f.price);
            material.setCount(f.count);
            material.setUnit(f.unit);
            upsert2(material);
        }

        //保存工序集合
        hql = "delete SupSalesOrderDetailProcedure where detailId =:detailId";
        getCurrentSession().createQuery(hql).setInteger("detailId", detail.getId()).executeUpdate();
        for (ProductProcedureForm f : form.procedureList) {
            //校验工序是否存在
            if (f.procedureId == null) {
                ProcedureSaveForm fo = new ProcedureSaveForm();
                fo.name = f.procedureName;
                fo.price = f.price == null ? null : f.price.toString();
                f.procedureId = productProcedureService.addProductProcedure(fo).getId();
            }

            SupSalesOrderDetailProcedure procedure = new SupSalesOrderDetailProcedure();
            procedure.setDetailId(detail.getId());
            procedure.setProcedureId(f.procedureId);
            procedure.setProcedureName(f.procedureName);
            procedure.setPrice(f.price);
            upsert2(procedure);
        }

    }

    /**
     * @Description: 删除订单
     * @Author: Chen.zm
     * @Date: 2018/1/9 0009
     */
    public void removeOrder(Integer id, Integer suplierId) throws ResponseEntityException, UpsertException{
        SupSalesOrder so = get2(SupSalesOrder.class, "id", id, "suplierId", suplierId);
        if (so == null)
            throw new ResponseEntityException(210, "订单号不存在");
        so.setLogicDeleted(true);
        upsert2(so);

        //删除订单下的工单
        String hql = "update SupOrder set logicDeleted = true where supSalesOrderId = :orderId";
        getCurrentSession().createQuery(hql).setInteger("orderId", so.getId()).executeUpdate();
    }

    /**
     * @Description: 批量修改备注
     * @Author: Chen.zm
     * @Date: 2018/1/9 0009
     */
    public void updateOrderRemarks(String remarks, String name, List<IdForm> orderIds) throws UpsertException {
        List<Integer> ids = new ArrayList<>();
        for (IdForm id : orderIds){
            ids.add(id.id);
            //创建备注记录
            saveOrderRemarkLog(id.id, name, remarks);
        }
        String hql = "update SupSalesOrder set remarks = :remarks where id in :ids";
        getCurrentSession().createQuery(hql).setString("remarks", remarks).setParameterList("ids", ids).executeUpdate();
    }

    /**
     * @Description: 创建备注记录
     * @Author: Chen.zm
     * @Date: 2018/1/9 0009
     */
    public void saveOrderRemarkLog(Integer orderId, String name, String remarks) throws UpsertException{
        SupSalesOrderRemarkLog log = new SupSalesOrderRemarkLog();
        log.setOrderId(orderId);
        log.setName(name);
        log.setRemarks(remarks);
        upsert2(log);
    }

    /**
     * @Description: 生成工单 ——批量
     * @Author: Chen.zm
     * @Date: 2018/1/9 0009
     */
    public void saveSupOrder(Integer orderId, Integer suplierId, Integer type, String userName) throws UpsertException, ResponseEntityException{
        SupSalesOrder salesOrder = getSupSalesOrderDetail(orderId, suplierId);
        if (salesOrder == null)
            throw new ResponseEntityException(210, "订单不存在");
        if (salesOrder.getProducedStatus() != 2)
            throw new ResponseEntityException(220, "订单状态非待生产");
        if (salesOrder.getSupSalesOrderDetail() == null)
            throw new ResponseEntityException(230, "订单商品信息为空");
        if (salesOrder.getSupSalesOrderDelivery() == null)
            throw new ResponseEntityException(240, "订单地址信息为空");
        if (salesOrder.getSupSalesOrderDetail().getQty() == null)
            throw new ResponseEntityException(250, "订单商品数量为空");
        //修改订单状态
        salesOrder.setProducedStatus(3);
        upsert2(salesOrder);
        int qty = salesOrder.getSupSalesOrderDetail().getQty();
        int count = 1; //每个工单对应的商品数量
        if (type == 1) {
            //生成单笔
            count = qty;
        }
        for (int i=1; i <= qty/count; i++) {
            SupOrder supOrder = new SupOrder();
            SupOrderDetail supOrderDetail = new SupOrderDetail();
            SupOrderDelivery supOrderDelivery = new SupOrderDelivery();
            supOrder.setProducedStatus(3);
            supOrder.setOrderType(2);
            supOrder.setTotal(salesOrder.getTotal());
            supOrder.setSuplierId(suplierId);
            supOrder.setSupSalesOrderId(salesOrder.getId());
            supOrder.setUrgent(1);
            //工单号
            supOrder.setOrderNo(salesOrder.getOrderNo() + "-"+ i);
            supOrder.setDescription(salesOrder.getDescription());
            supOrder = upsert2(supOrder);

            supOrderDelivery.setOrderId(supOrder.getId());
            supOrderDelivery.setCustomer(salesOrder.getSupSalesOrderDelivery().getCustomer());
            supOrderDelivery.setCustomerPhone(salesOrder.getSupSalesOrderDelivery().getCustomerPhone());
            supOrderDelivery.setCustomerId(salesOrder.getSupSalesOrderDelivery().getCustomerId());
            supOrderDelivery.setCustomerAddressId(salesOrder.getSupSalesOrderDelivery().getCustomerAddressId());
            supOrderDelivery.setReceiver(salesOrder.getSupSalesOrderDelivery().getReceiver());
            supOrderDelivery.setMobile(salesOrder.getSupSalesOrderDelivery().getMobile());
            supOrderDelivery.setProvince(salesOrder.getSupSalesOrderDelivery().getProvince());
            supOrderDelivery.setProvinceName(salesOrder.getSupSalesOrderDelivery().getProvinceName());
            supOrderDelivery.setCity(salesOrder.getSupSalesOrderDelivery().getCity());
            supOrderDelivery.setCityName(salesOrder.getSupSalesOrderDelivery().getCityName());
            supOrderDelivery.setZone(salesOrder.getSupSalesOrderDelivery().getZone());
            supOrderDelivery.setZoneName(salesOrder.getSupSalesOrderDelivery().getZoneName());
            supOrderDelivery.setAddress(salesOrder.getSupSalesOrderDelivery().getAddress());
            supOrderDelivery.setDeliveryDate(salesOrder.getSupSalesOrderDelivery().getDeliveryDate());
            upsert2(supOrderDelivery);

            supOrderDetail.setOrderId(supOrder.getId());
            supOrderDetail.setPno(salesOrder.getSupSalesOrderDetail().getPno());
            supOrderDetail.setHref(salesOrder.getSupSalesOrderDetail().getHref());
            supOrderDetail.setCategoryName(salesOrder.getSupSalesOrderDetail().getCategoryName());
            supOrderDetail.setColor(salesOrder.getSupSalesOrderDetail().getColor());
            supOrderDetail.setSize(salesOrder.getSupSalesOrderDetail().getSize());
            supOrderDetail.setThroatheight(salesOrder.getSupSalesOrderDetail().getThroatheight());
            supOrderDetail.setShoulder(salesOrder.getSupSalesOrderDetail().getShoulder());
            supOrderDetail.setBust(salesOrder.getSupSalesOrderDetail().getBust());
            supOrderDetail.setWaist(salesOrder.getSupSalesOrderDetail().getWaist());
            supOrderDetail.setHipline(salesOrder.getSupSalesOrderDetail().getHipline());
            supOrderDetail.setHeight(salesOrder.getSupSalesOrderDetail().getHeight());
            supOrderDetail.setWeight(salesOrder.getSupSalesOrderDetail().getWeight());
            supOrderDetail.setQty(count);
            supOrderDetail.setPrice(salesOrder.getSupSalesOrderDetail().getPrice());
            supOrderDetail.setOutputPrice(salesOrder.getSupSalesOrderDetail().getPrice());
            supOrderDetail.setMaterial(salesOrder.getSupSalesOrderDetail().getMaterial());
            supOrderDetail.setTechnics(salesOrder.getSupSalesOrderDetail().getTechnics());
            supOrderDetail = upsert2(supOrderDetail);

            //创建图片集
            if (salesOrder.getSupSalesOrderDetail().getSupSalesOrderDetailPictureList() != null) {
                for (SupSalesOrderDetailPicture img : salesOrder.getSupSalesOrderDetail().getSupSalesOrderDetailPictureList()) {
                    SupOrderDetailPicture picture = new SupOrderDetailPicture();
                    picture.setDetailId(supOrderDetail.getId());
                    picture.setHref(img.getHref());
                    picture.setSort(img.getSort());
                    upsert2(picture);
                }
            }

            //创建工单记录
            suplierOrderService.saveSupOrderDeliveryLog(supOrder.getId(), "生成工单","无", userName);
        }
        return;
    }

    /**
     * @Description: 获取订单下的工单集
     * @Author: Chen.zm
     * @Date: 2018/1/10 0010
     */
    public List<SupOrder> supOrderList(Integer orderId) {
        List<SupOrder> list = getCurrentSession().createCriteria(SupOrder.class)
                .add(Restrictions.eq("supSalesOrderId", orderId))
                .addOrder(Order.desc("id"))
                .list();
        return list;
    }


    /**
     * @Description: 发货
     * @Author: Chen.zm
     * @Date: 2018/1/10 0010
     */
    public void orderDeliver(Integer deliveryCompany, String deliveryCompanyName, String deliveryNo, Integer suplierId, String userName, Integer orderId) throws ResponseEntityException, UpsertException{
        //修改订单状态
        SupSalesOrder so = get2(SupSalesOrder.class, "id", orderId, "suplierId", suplierId);
        if (so == null)
            throw new ResponseEntityException(230, "订单不存在");
        if (so.getProducedStatus() != 3)
            throw new ResponseEntityException(240, "订单非生产中");
        so.setProducedStatus(4);
        upsert2(so);
        //保存订单物流记录
        SupSalesOrderDelivery delivery = get2(SupSalesOrderDelivery.class, "orderId", orderId);
        if (delivery == null)
            throw new ResponseEntityException(250, "订单客户信息为空");
        delivery.setDeliveryCompany(deliveryCompany);
        delivery.setDeliveryCompanyName(deliveryCompanyName);
        delivery.setDeliveryNo(deliveryNo);
        delivery.setDeliveryActualDate(new Date());
        upsert2(delivery);


        //修改工单状态
        List<SupOrder> list = supOrderList(orderId);
        for (SupOrder supOrder : list) {
            if (!suplierId.equals(supOrder.getSuplierId()))
                throw new ResponseEntityException(210, "工单不存在");
            if (supOrder.getProducedStatus() != 3)
                throw new ResponseEntityException(220, "工单非生产中");
            supOrder.setProducedStatus(4);
            upsert2(supOrder);

            //创建工单记录
            suplierOrderService.saveSupOrderDeliveryLog(supOrder.getId(), "发货","无", userName);

            //修改当前物流信息
            suplierOrderService.updateSupOrderDelivery(supOrder.getId(), deliveryCompany, deliveryCompanyName, deliveryNo);

            //创建物流记录
            suplierOrderService.saveSupOrderDeliveryRecord(supOrder.getId(), deliveryCompany, deliveryCompanyName, deliveryNo);
        }
    }


    /**
     * @Description: 完成订单
     * @Author: Chen.zm
     * @Date: 2018/1/10 0010
     */
    public void orderFinish(Integer suplierId, String userName, Integer orderId) throws ResponseEntityException, UpsertException{
        //修改订单状态
        SupSalesOrder so = get2(SupSalesOrder.class, "id", orderId, "suplierId", suplierId);
        if (so == null)
            throw new ResponseEntityException(230, "订单不存在");
        if (so.getProducedStatus() != 4)
            throw new ResponseEntityException(240, "订单非已发货");
        so.setProducedStatus(7);
        upsert2(so);

        //修改工单状态
        List<SupOrder> list = supOrderList(orderId);
        for (SupOrder supOrder : list) {
            if (supOrder.getSuplierId() != suplierId.intValue())
                throw new ResponseEntityException(210, "工单不存在");
            if (supOrder.getProducedStatus() != 4)
                throw new ResponseEntityException(220, "工单非已发货");
            supOrder.setProducedStatus(7);
            upsert2(supOrder);

            //创建工单记录
            suplierOrderService.saveSupOrderDeliveryLog(supOrder.getId(), "完成工单","无", userName);
        }
    }

}
