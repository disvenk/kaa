package com.xxx.pay.processor.proc;

import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.service.CommonService;
import com.xxx.core.spring.SpringContext;
import com.xxx.model.business.*;
import com.xxx.pay.processor.OrderProcess;
import com.xxx.utils.Arith;
import com.xxx.utils.DateTimeUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;

import java.util.Date;
import java.util.List;

/**
 * @Description: 盒子订单支付确认
 * @Author: Chen.zm
 * @Date: 2017/12/22 0022
 */
public class BoxPayProcess implements OrderProcess {
    private int id;
    private String payNo;
    private String channel;
    private CommonService commonServic;

    public BoxPayProcess(int targetOrderId, String payNo, String channel) {
        this.id = targetOrderId;
        this.payNo = payNo;
        this.channel = channel;
        this.commonServic = (CommonService) SpringContext.getApplicationContext().getBean("commonService");
    }

    @Override
    public void process() throws UpsertException {
        BoxPayOrder boxPayOrder = commonServic.get2(BoxPayOrder.class, id);
        Payment payment = commonServic.get2(Payment.class, "payNo", payNo);

        boxPayOrder.setActualPay(payment.getPrice());
        boxPayOrder.setStatus(1); //订单状态改成待发货
        commonServic.upsert2(boxPayOrder);

        try {
            //扣除限购数量
            if (boxPayOrder.getBoxTypeId() == 1) {
                SysDict dict = commonServic.get2(SysDict.class, "keyName", "BOX_ONE_COUNT");
                dict.setKeyValue(((dict.getKeyValue() == null ? 0 : Integer.parseInt(dict.getKeyValue())) - 1) + "");
                commonServic.upsert2(dict);
            }

            //创建盒子用户信息
            BoxInfo boxInfo = commonServic.get2(BoxInfo.class, "storeId", boxPayOrder.getStoreId());
            if (boxInfo == null) {
                boxInfo = new BoxInfo();
                boxInfo.setStoreId(boxPayOrder.getStoreId());
            }
            if (boxPayOrder.getDeposit() != null && boxPayOrder.getDeposit() >= 0) {
                if (boxInfo.getDeposit() == null) boxInfo.setDeposit(0.0);
                boxInfo.setDeposit(Arith.add(boxInfo.getDeposit(), boxPayOrder.getDeposit()));
            }
            if (boxInfo.getCount() == null) boxInfo.setCount(0);
            boxInfo.setCount(boxInfo.getCount() + boxPayOrder.getCount());

            if (boxInfo.getTermTime() == null || boxInfo.getTermTime().getTime() < new Date().getTime()) {
                boxInfo.setTermTime(new Date());
            }
            boxInfo.setTermTime(DateTimeUtils.getDateByAddDay(boxInfo.getTermTime(), boxPayOrder.getDay()));
            commonServic.upsert2(boxInfo);

            //创建盒子服务记录
            BoxOperateLog boxOperateLog = new BoxOperateLog();
            boxOperateLog.setStoreId(boxPayOrder.getStoreId());
            boxOperateLog.setName("开通合一盒子");
            boxOperateLog.setPrice(boxPayOrder.getActualPay());
            boxOperateLog.setDeposit(boxInfo.getDeposit());
            boxOperateLog.setCount(boxInfo.getCount());
            boxOperateLog.setTermTime(boxInfo.getTermTime());
            commonServic.upsert2(boxOperateLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
