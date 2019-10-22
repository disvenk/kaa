package com.xxx.user.service;

import com.xxx.core.service.CommonService;
import com.xxx.model.business.Payment;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 支付表操作
 */
@Service("userPayment")
public class PaymentService extends CommonService {


    /**
     * @Description: 获取订单支付成功记录
     * @Author: Chen.zm
     * @Date: 2017/10/31 0031
     */
    @Cacheable(value = {"Payment"})
    public Payment findPayment(Integer orderId, Integer orderType) throws Exception {
        Criterion cri = Restrictions.eq("orderId", orderId);
        cri = Restrictions.and(cri, Restrictions.eq("orderType", orderType));
        cri = Restrictions.and(cri, Restrictions.eq("status", 20));
        return (Payment) getCurrentSession().createCriteria(Payment.class)
                .add(cri)
                .uniqueResult();
    }


}
