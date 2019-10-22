package com.xxx.pay.processor;


import com.xxx.pay.processor.proc.*;

/**
 * 订单处理工厂
 */
public class OrderProcessFactory {
    public OrderProcess create(int targetOrderId, int orderType, String payNo, String channel) {
        switch (orderType) {  // 1-门店采购订单  2-盒子购买订单  3-盒子使用商品购买
            case 1:
                return new StoreSuplierProcess(targetOrderId, payNo, channel);
            case 2:
                return new BoxPayProcess(targetOrderId, payNo, channel);
            case 3:
                return new BoxUseLogProductProcess(targetOrderId, payNo, channel);

        }
        return null;
    }
}
