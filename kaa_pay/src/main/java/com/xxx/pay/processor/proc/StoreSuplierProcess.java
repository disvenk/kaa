package com.xxx.pay.processor.proc;

import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.service.CommonService;
import com.xxx.core.spring.SpringContext;
import com.xxx.model.business.*;
import com.xxx.pay.processor.OrderProcess;
import com.xxx.utils.Arith;
import com.xxx.utils.DateTimeUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * @Description: 门店采购订单支付确认
 * @Author: Chen.zm
 * @Date: 2017/11/7 0007
 */
public class StoreSuplierProcess implements OrderProcess {
    private int id;
    private String payNo;
    private String channel;
    private CommonService commonServic;

    public StoreSuplierProcess(int targetOrderId, String payNo, String channel) {
        this.id = targetOrderId;
        this.payNo = payNo;
        this.channel = channel;
        this.commonServic = (CommonService) SpringContext.getApplicationContext().getBean("commonService");
    }

    @Override
    public void process() throws UpsertException {
        StoSuplierOrder suplierOrder = commonServic.get2(StoSuplierOrder.class, id);
        Payment payment = commonServic.get2(Payment.class, "payNo", payNo);

        suplierOrder.setActualPay(payment.getPrice());
        suplierOrder.setStatus(1); //订单状态改成待发货
        commonServic.upsert2(suplierOrder);

        try {
            saveSupOrder();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: 创建工单
     * @Author: Chen.zm
     * @Date: 2017/12/5 0005
     */
    public void saveSupOrder() throws UpsertException {
        List<StoSuplierOrderDetail> suplierOrderDetails = commonServic.getCurrentSession().createCriteria(StoSuplierOrderDetail.class)
                .add(Restrictions.eq("orderId", id))
                .setFetchMode("supOrder", FetchMode.JOIN)
                .setFetchMode("supOrder.stoSuplierOrderDelivery", FetchMode.JOIN)
                .list();
        for (StoSuplierOrderDetail suplierOrderDetail : suplierOrderDetails) {
            //拆分商品数量
            int qty = suplierOrderDetail.getQty() == null ? 0 : suplierOrderDetail.getQty();
            for (int i=0; i<qty ; i++) {
                StoSuplierOrder suplierOrder = suplierOrderDetail.getStoSuplierOrder();
                StoSuplierOrderDelivery suplierOrderDelivery = suplierOrderDetail.getStoSuplierOrder().getStoSuplierOrderDelivery();

                //创建订单
                SupOrder supOrder = new SupOrder();
                supOrder.setOrderId(suplierOrder.getId());
                supOrder.setTotal(suplierOrder.getTotal());
                supOrder.setActualPay(suplierOrder.getActualPay());
                supOrder.setOrderType(1);
                supOrder.setProducedStatus(1);
                supOrder = commonServic.upsert2(supOrder);

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
                orderDetail.setQty(1);//已经拆分为一件
                orderDetail.setPrice(suplierOrderDetail.getPrice());
                orderDetail.setSubtotal(suplierOrderDetail.getPrice());
                commonServic.upsert2(orderDetail);

                //收件明细
                SupOrderDelivery delivery = new SupOrderDelivery();
                delivery.setOrderId(supOrder.getId());
                delivery.setReceiver(suplierOrderDelivery.getReceiver());
                delivery.setMobile(suplierOrderDelivery.getMobile());
                delivery.setProvince(suplierOrderDelivery.getProvince());
                delivery.setProvinceName(suplierOrderDelivery.getProvinceName());
                delivery.setCity(suplierOrderDelivery.getCity());
                delivery.setCityName(suplierOrderDelivery.getCityName());
                delivery.setZone(suplierOrderDelivery.getZone());
                delivery.setZoneName(suplierOrderDelivery.getZoneName());
                delivery.setAddress(suplierOrderDelivery.getAddress());
                delivery.setExpectsendDate(suplierOrderDelivery.getExpectsendDate());
                commonServic.upsert2(delivery);

            }

        }

    }
}
