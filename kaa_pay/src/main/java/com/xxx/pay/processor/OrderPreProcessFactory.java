package com.xxx.pay.processor;


import com.xxx.pay.processor.preproc.*;

/**
 * 订单处理工厂
 */
public class OrderPreProcessFactory {
    public OrderProcess create(int targetOrderId, int orderType, double price) {
        switch (orderType) {  // 1-门店采购订单  2-盒子购买订单  3-盒子使用商品购买
            case 1:
                return new PreStoreSuplierProcess(targetOrderId, price);
            case 2:
                return new PreBoxPayProcess(targetOrderId, price);
            case 3:
                return new PreBoxUseLogProductProcess(targetOrderId, price);

        }
        return null;
    }
}
