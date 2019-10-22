package com.xxx.sales.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.ExtFilter;
import com.xxx.core.query.MybatisPageQuery;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.sales.dao.StoreSuplierOrderDao;
import com.xxx.sales.form.OrderDetailForm;
import com.xxx.sales.form.SubmitOrderForm;
import com.xxx.user.utils.GenerateNumberUtil;
import com.xxx.utils.Arith;
import com.xxx.utils.DateTimeUtils;
import com.xxx.model.business.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Service
public class StoreSuplierOrderService extends CommonService {

    @Autowired
    private StoreSuplierOrderDao storeSuplierOrderDao;

    /**
     * @Description: 门店订单数量
     * @Author: Chen.zm
     * @Date: 2017/11/24 0024
     */
    @Cacheable(value = {"StoSuplierOrder"})
    public List<JSONObject> orderCount(Integer storeId) throws Exception {
        return storeSuplierOrderDao.orderCount(storeId);
    }

    /**
     * @Description: 门店采购订单列表
     * @Author: Chen.zm
     * @Date: 2017/10/31 0031
     */
    @Cacheable(value = {"StoSuplierOrder,StoSuplierOrderDetail"})
    public PageList<JSONObject> findStoreSuplierOrderList(MybatisPageQuery pageQuery) throws Exception {
        return storeSuplierOrderDao.findStoreSuplierOrderList(pageQuery);
    }

    /**
     * @Description:根据采购订单id获取订单快递信息
     * @Author: hanchao
     * @Date: 2017/12/19 0019
     */
    @Cacheable(value = {"StoSuplierOrder,StoSuplierOrderDelivery"})
    public StoSuplierOrderDelivery buyOrderDeliveryMessage( Integer id) {
        Criterion cri = Restrictions.eq("orderId", id);
        return(StoSuplierOrderDelivery) getCurrentSession().createCriteria(StoSuplierOrderDelivery.class)
                .add(cri)
                .uniqueResult();
    }


    /**
     * @Description: 门店采购订单详情
     * @Author: Chen.zm
     * @Date: 2017/10/31 0031
     */
    @Cacheable(value = {"StoSuplierOrder,StoSuplierOrderDetail,StoSuplierOrderDelivery"})
    public StoSuplierOrder findStoSuplierOrder(Integer storeId, Integer id) {
        Criterion cri = Restrictions.eq("id", id);
        if (storeId != null)
            cri = Restrictions.and(cri, Restrictions.eq("storeId", storeId));
        return (StoSuplierOrder) getCurrentSession().createCriteria(StoSuplierOrder.class)
                .add(cri)
                .setFetchMode("stoSuplierOrderDetailList", FetchMode.JOIN)
                .setFetchMode("stoSuplierOrderDetailList.stoProduct", FetchMode.JOIN)
                .setFetchMode("stoSuplierOrderDetailList.stoProduct.plaProduct", FetchMode.JOIN)
                .setFetchMode("stoSuplierOrderDelivery", FetchMode.JOIN)
                .setFetchMode("stoSalesOrder", FetchMode.JOIN)
                .uniqueResult();
    }

    /**
     * @Description: 获取门店商品列表
     * @Author: Chen.zm
     * @Date: 2017/10/31 0031
     */
    @Cacheable(value = {"PlaProduct,PlaProductCategory,StoProduct,StoProductPrice"})
    public PageList<StoProduct> findProductList(PageQuery pageQuery, String name, Integer categoryId, String pno) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(name))
            cri = Restrictions.and(cri, Restrictions.like("name", name, MatchMode.ANYWHERE));
        if (categoryId != null)
            cri = Restrictions.and(cri, Restrictions.eq("categoryId", categoryId));
        if(StringUtils.isNotBlank(pno)) {
            ExtFilter filter = new ExtFilter("plaProduct.pno", "string", pno, ExtFilter.ExtFilterComparison.like, null);
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(filter);
            pageQuery.filter = jsonArray.toJSONString();
        }
        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "plaProductCategory,plaProduct";
        pageQuery.limit = 10;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<StoProduct> list = hibernateReadonlyRepository.getList(StoProduct.class, pageQuery);
        for (StoProduct product : list) {
            for (StoProductPrice supplier : product.getStoProductPriceList()) {
            }
        }
        return list;
    }

    /**
     * @Description: 门店商品详情
     * @Author: Chen.zm
     * @Date: 2017/10/31 0031
     */
    @Cacheable(value = {"PlaProduct,PlaProductCategory,StoProduct,StoProductPicture"})
    public StoProduct productDetail(Integer id) {
        Criterion cri = Restrictions.eq("id", id);
        StoProduct product = (StoProduct) getCurrentSession().createCriteria(StoProduct.class)
                .add(cri)
                .setFetchMode("plaProductCategory", FetchMode.JOIN)
                .setFetchMode("plaProduct", FetchMode.JOIN)
                .setFetchMode("stoProductPriceList", FetchMode.JOIN)
                .uniqueResult();
        return product;
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
                .setFetchMode("stoProduct", FetchMode.JOIN)
                .setFetchMode("stoProduct.plaProductCategory", FetchMode.JOIN)
                .uniqueResult();
    }


    /**
     * @Description: 获取销售订单详情
     * @Author: Chen.zm
     * @Date: 2017/11/27 0027
     */
    @Cacheable(value = {"StoSalesOrderDetail"})
    public List<StoSalesOrderDetail> getStoSalesOrderDetailList(Integer orderId) {
        Criterion cri = Restrictions.eq("orderId", orderId);
        return  getCurrentSession().createCriteria(StoSalesOrderDetail.class)
                .add(cri)
                .list();
    }


    /**
     * @Description: 根据货号获取门店商品
     * @Author: Chen.zm
     * @Date: 2017/11/6 0006
     */
    @Cacheable(value = {"PlaProduct,StoProduct"})
    public StoProduct getProductByPno(String pno) {
        return (StoProduct) getCurrentSession().createCriteria(StoProduct.class).createAlias("plaProduct", "p", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.eq("p.pno", pno))
                .setFetchMode("stoProductPictureList", FetchMode.JOIN)
                .uniqueResult();
    }


    /**
     * @Description: 创建门店采购订单
     * @Author: Chen.zm
     * @Date: 2017/10/31 0031
     */
    @CacheEvict(value = {"StoSuplierOrder,StoSuplierOrderDelivery,StoSuplierOrderDetail"}, allEntries = true)
    public StoSuplierOrder saveStoSuplierOrder(SubmitOrderForm form, Integer storeId) throws UpsertException, ResponseEntityException, ParseException{
        StoSuplierOrder suplierOrder = new StoSuplierOrder();
        if (StringUtils.isNotBlank(form.orderNo)) {
            suplierOrder.setOrderNo(form.orderNo);
        } else {
            //生成订单号
            suplierOrder.setOrderNo(GenerateNumberUtil.generateStoreSuplierNumber());
        }
        suplierOrder.setOrderDate(new Date());
        suplierOrder.setStoreId(storeId);
        suplierOrder.setRemarks(form.remarks);
        suplierOrder.setSalesOrderId(form.salesOrderId);
        suplierOrder.setStatus(0);//订单状态为待支付
        suplierOrder = upsert2(suplierOrder);

        //创建收件信息
        StoSuplierOrderDelivery delivery = new StoSuplierOrderDelivery();
        delivery.setOrderId(suplierOrder.getId());
        delivery.setReceiver(form.receiver);
        delivery.setMobile(form.mobile);
        delivery.setProvince(form.province);
        delivery.setProvinceName(form.provinceName);
        delivery.setCity(form.city);
        delivery.setCityName(form.cityName);
        delivery.setZone(form.zone);
        delivery.setZoneName(form.zoneName);
        delivery.setAddress(form.address);
        delivery.setExpectsendDate(DateTimeUtils.parseDate(form.expectsendDate, "yyyy-MM-dd"));
        delivery = upsert2(delivery);

        //创建订单明细
        Double total = 0.0;
        for (OrderDetailForm detail : form.orderDetail) {
            StoSuplierOrderDetail orderDetail = new StoSuplierOrderDetail();
            orderDetail.setOrderId(suplierOrder.getId());
            orderDetail.setType(1);  //标示为门店商品
            orderDetail.setStorePid(detail.pid);
            StoProduct stoProduct = get2(StoProduct.class, detail.pid);
            if (stoProduct == null)
                throw new ResponseEntityException(120, "商品不存在");
            orderDetail.setPid(stoProduct.getPlatProductId());
            //获取门店商品明细
            StoProductPrice productPrice = getStoProductPrice(detail.pid, detail.color, detail.size);
            if (productPrice == null || productPrice.getStoProduct() == null)
                throw new ResponseEntityException(120, "商品不存在");
            orderDetail.setHref(productPrice.getStoProduct().getHref());
            orderDetail.setProductName(productPrice.getStoProduct().getName());
            orderDetail.setCategoryName(productPrice.getStoProduct().getPlaProductCategory() == null ? "" : productPrice.getStoProduct().getPlaProductCategory().getName());
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
            PlaProductPrice plaProductPrice = get2(PlaProductPrice.class, "pid", stoProduct.getPlatProductId(), "color", detail.color, "size", detail.size);
            if (plaProductPrice == null)
                throw new ResponseEntityException(130, "平台商品不存在");
            orderDetail.setPrice(plaProductPrice.getOfflinePrice());
            orderDetail.setSubtotal(Arith.mul(orderDetail.getPrice(), orderDetail.getQty()));
            orderDetail = upsert2(orderDetail);
            total = Arith.add(total, orderDetail.getSubtotal());
        }
        suplierOrder.setTotal(total);

        //修改销售订单采购状态
        if (suplierOrder.getSalesOrderId() != null) {
            StoSalesOrder salesOrder = get2(StoSalesOrder.class, suplierOrder.getSalesOrderId());
            salesOrder.setOrdernoSuplier(suplierOrder.getOrderNo());
            salesOrder.setOrderstatusSuplier(1);
            upsert2(salesOrder);

            suplierOrder.setChannelName(salesOrder.getChannelName());
        }
        suplierOrder.setChannelType(2); //线下渠道
        return upsert2(suplierOrder);
    }


    /**
     * @Description: 销售商品下单
     * @Author: Chen.zm
     * @Date: 2017/11/20 0020
     */
    @CacheEvict(value = {"StoSuplierOrder,StoSuplierOrderDelivery,StoSuplierOrderDetail"}, allEntries = true)
    public StoSuplierOrder saveStoSuplierOrderSales(SubmitOrderForm form, Integer storeId) throws UpsertException, ResponseEntityException, ParseException{
        StoSuplierOrder suplierOrder = new StoSuplierOrder();
        suplierOrder.setOrderNo(GenerateNumberUtil.generateStoreSuplierNumber());
        suplierOrder.setOrderDate(new Date());
        suplierOrder.setStoreId(storeId);
        suplierOrder.setRemarks(form.remarks);
        suplierOrder.setChannelType(2); //线下渠道
        StoStoreInfo store = get2(StoStoreInfo.class, storeId);
        suplierOrder.setChannelName(store == null ? "" : store.getStoreName());
        suplierOrder.setStatus(0);//订单状态为待支付
        suplierOrder = upsert2(suplierOrder);

        //创建收件信息
        StoSuplierOrderDelivery delivery = new StoSuplierOrderDelivery();
        delivery.setOrderId(suplierOrder.getId());
        delivery.setReceiver(form.receiver);
        delivery.setMobile(form.mobile);
        delivery.setProvince(form.province);
        delivery.setProvinceName(form.provinceName);
        delivery.setCity(form.city);
        delivery.setCityName(form.cityName);
        delivery.setZone(form.zone);
        delivery.setZoneName(form.zoneName);
        delivery.setAddress(form.address);
        delivery = upsert2(delivery);

        //创建订单明细
        Double total = 0.0;
        for (OrderDetailForm detail : form.orderDetail) {
            StoSuplierOrderDetail orderDetail = new StoSuplierOrderDetail();
            orderDetail.setOrderId(suplierOrder.getId());
            orderDetail.setType(2); //标示为销售商品
            orderDetail.setSalesPid(detail.pid);
            SalesProduct salesProduct = get2(SalesProduct.class, detail.pid);
            if (salesProduct == null)
                throw new ResponseEntityException(120, "商品不存在");
            orderDetail.setPid(salesProduct.getPlatProductId());
            //获取门店商品明细
            SalesProductPrice productPrice = get2(SalesProductPrice.class, "pid", detail.pid, "color", detail.color, "size", detail.size);
            if (productPrice == null || productPrice.getSalesProduct() == null)
                throw new ResponseEntityException(120, "商品不存在");
            orderDetail.setHref(productPrice.getSalesProduct().getHref());
            orderDetail.setProductName(productPrice.getSalesProduct().getName());
            orderDetail.setCategoryName(productPrice.getSalesProduct().getPlaProductCategory() == null ? "" : productPrice.getSalesProduct().getPlaProductCategory().getName());
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
            orderDetail.setPrice(productPrice.getOfflinePrice());
            orderDetail.setSubtotal(Arith.mul(orderDetail.getPrice(), orderDetail.getQty()));
            orderDetail = upsert2(orderDetail);
            total = Arith.add(total, orderDetail.getSubtotal());
        }
        suplierOrder.setTotal(total);

        return upsert2(suplierOrder);
    }


    /**
     * @Description: 取消门店采购订单
     * @Author: Chen.zm
     * @Date: 2017/11/1 0001
     */
    @CacheEvict(value = {"StoSuplierOrder"}, allEntries = true)
    public void updateSuplierOrderSuplierOrderCancel(Integer id, Integer storeId) throws UpsertException, ResponseEntityException{
        StoSuplierOrder suplierOrder = findStoSuplierOrder(storeId, id);
        if (suplierOrder == null)
            throw new ResponseEntityException(120, "订单不存在");
        if (suplierOrder.getStatus() != 0)
            throw new ResponseEntityException(130, "订单只能在未支付时取消");
        suplierOrder.setStatus(3);
        upsert2(suplierOrder);
    }

    /**
     * @Description: 删除订单
     * @Author: Chen.zm
     * @Date: 2017/11/1 0001
     */
    @CacheEvict(value = {"StoSuplierOrder"}, allEntries = true)
    public void removeSuplierOrderSuplierOrder(Integer id, Integer storeId) throws UpsertException, ResponseEntityException{
        StoSuplierOrder suplierOrder = findStoSuplierOrder(storeId, id);
        if (suplierOrder == null)
            throw new ResponseEntityException(120, "订单不存在");
        suplierOrder.setLogicDeleted(true);
        upsert2(suplierOrder);
    }

    /**
     * @Description: 订单确认收货
     * @Author: Chen.zm
     * @Date: 2017/11/1 0001
     */
    @CacheEvict(value = {"StoSuplierOrder"}, allEntries = true)
    public void updateSuplierOrderSuplierOrderReceive(Integer id, Integer storeId) throws UpsertException, ResponseEntityException{
        StoSuplierOrder suplierOrder = findStoSuplierOrder(storeId, id);
        if (suplierOrder == null)
            throw new ResponseEntityException(120, "订单不存在");
        if (suplierOrder.getStatus() != 4)
            throw new ResponseEntityException(130, "订单只能在待收货时确认收货");
        suplierOrder.setStatus(2);
        upsert2(suplierOrder);
        StoSalesOrder salesOrder = suplierOrder.getStoSalesOrder();
        if (salesOrder != null) {
            salesOrder.setOrderstatusSuplier(2);
            upsert2(salesOrder);
        }
    }

    /**
     * @Description: 取得商品的图文详情
     * @Author: Steven.Xiao
     * @Date: 2017/12/7
     */

    public String getProductDescription(Integer pid) {
        StoProductDescription stoProductDescription = get2(StoProductDescription.class, "pid", pid);
        if(stoProductDescription==null) {
            return "";
        }
        return stoProductDescription.getDescription();
    }


}
