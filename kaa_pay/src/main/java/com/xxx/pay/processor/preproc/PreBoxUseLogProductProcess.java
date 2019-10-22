package com.xxx.pay.processor.preproc;

import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.service.CommonService;
import com.xxx.core.spring.SpringContext;
import com.xxx.model.business.BoxPayOrder;
import com.xxx.model.business.BoxUseLogProduct;
import com.xxx.pay.processor.OrderProcess;
import org.hibernate.criterion.Restrictions;

/**
 * @Description: 盒子使用商品购买前校验
 * @Author: Chen.zm
 * @Date: 2017/12/22 0022
 */
public class PreBoxUseLogProductProcess implements OrderProcess {
    private int id;
    private double price;
    private CommonService commonServic;

    public PreBoxUseLogProductProcess(int targetOrderId, double price) {
        this.id = targetOrderId;
        this.price = price;
        this.commonServic = (CommonService) SpringContext.getApplicationContext().getBean("commonService");
    }

    @Override
    public void process() throws Exception {
        BoxUseLogProduct boxUseLogProduct = (BoxUseLogProduct) commonServic.getCurrentSession().createCriteria(BoxUseLogProduct.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();

        if (boxUseLogProduct == null)
            throw new ResponseEntityException(110, "订单不存在");
        if (!boxUseLogProduct.getStatus().equals(0))
            throw new ResponseEntityException(120, "订单状态不是待支付");
        if (boxUseLogProduct.getPrice() == null || !boxUseLogProduct.getPrice().equals(price))
            throw new ResponseEntityException(130, "支付金额不正确");

    }
}
