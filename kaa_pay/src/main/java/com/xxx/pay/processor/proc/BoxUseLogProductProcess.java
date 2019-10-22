package com.xxx.pay.processor.proc;

import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.service.CommonService;
import com.xxx.core.spring.SpringContext;
import com.xxx.model.business.*;
import com.xxx.pay.processor.OrderProcess;
import com.xxx.utils.Arith;
import com.xxx.utils.DateTimeUtils;

import java.util.Date;

/**
 * @Description: 盒子订单支付确认
 * @Author: Chen.zm
 * @Date: 2017/12/22 0022
 */
public class BoxUseLogProductProcess implements OrderProcess {
    private int id;
    private String payNo;
    private String channel;
    private CommonService commonServic;

    public BoxUseLogProductProcess(int targetOrderId, String payNo, String channel) {
        this.id = targetOrderId;
        this.payNo = payNo;
        this.channel = channel;
        this.commonServic = (CommonService) SpringContext.getApplicationContext().getBean("commonService");
    }

    @Override
    public void process() throws UpsertException {
        BoxUseLogProduct boxUseLogProduct = commonServic.get2(BoxUseLogProduct.class, id);
        Payment payment = commonServic.get2(Payment.class, "payNo", payNo);

//        boxUseLogProduct.setActualPay(payment.getPrice());
        boxUseLogProduct.setStatus(1);
        commonServic.upsert2(boxUseLogProduct);

    }

}
