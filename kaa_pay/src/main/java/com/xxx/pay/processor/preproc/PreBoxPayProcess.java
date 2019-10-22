package com.xxx.pay.processor.preproc;

import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.service.CommonService;
import com.xxx.core.spring.SpringContext;
import com.xxx.model.business.BoxPayOrder;
import com.xxx.pay.processor.OrderProcess;
import org.hibernate.criterion.Restrictions;

/**
 * @Description: 盒子购买前校验
 * @Author: Chen.zm
 * @Date: 2017/12/22 0022
 */
public class PreBoxPayProcess implements OrderProcess {
    private int id;
    private double price;
    private CommonService commonServic;

    public PreBoxPayProcess(int targetOrderId, double price) {
        this.id = targetOrderId;
        this.price = price;
        this.commonServic = (CommonService) SpringContext.getApplicationContext().getBean("commonService");
    }

    @Override
    public void process() throws Exception {
        BoxPayOrder boxPayOrder = (BoxPayOrder) commonServic.getCurrentSession().createCriteria(BoxPayOrder.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();

        if (boxPayOrder == null)
            throw new ResponseEntityException(110, "订单不存在");
        if (!boxPayOrder.getStatus().equals(0))
            throw new ResponseEntityException(120, "订单状态不是待支付");
        if (boxPayOrder.getTotal() == null || !boxPayOrder.getTotal().equals(price))
            throw new ResponseEntityException(130, "支付金额不正确");

    }
}
