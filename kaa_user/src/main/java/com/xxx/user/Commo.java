package com.xxx.user;

public class Commo {

    /**
     * 获取默认图片路径
     * @return
     */
    public static String findDefaultImg() {
        return "";
    }

    /**
     * @Description: 获取门店采购订单状态对应的名称
     * @Author: Chen.zm
     * @Date: 2017/10/27 0027
     */
    public static String parseStoreSuplierOrderStatus(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "待支付";
            case 1: return "待发货";
            case 2: return "已完成";
            case 3: return "已取消";
            case 4: return "待收货";
            default: return "其他";
        }
    }

    /**
     * @Description: 获取平台工单生产状态
     * @Author: Chen.zm
     * @Date: 2017/11/1 0001
     */
    public static String parseSuplierOrderProducedStatus(Integer producedStatus) {
        if (producedStatus == null) return "无";
        //生产状态  1:待分配  2：待接单   3：生产中   4：已发货  5：待质检  6：质检不合格  7：质检通过  8：已入库  9：已取消  10：确认取消
        switch (producedStatus) {
            case 0: return "无";
            case 1: return "待分配";
            case 2: return "待接单";
            case 3: return "生产中";
            case 4: return "已发货";
            case 5: return "待质检";
            case 6: return "质检不合格";
            case 7: return "质检通过";
            case 8: return "已入库";
            case 9: return "已取消";
            case 10: return "确认取消";
            default: return "其他";
        }
    }

    /**
     * @Description: 获取供应商工单生产状态
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    public static String parseSuplierOrderProducedStatusOffline(Integer producedStatus) {
        if (producedStatus == null) return "无";
        //生产状态  2：待接单(待生产)   3：生产中   4：已发货  7：质检通过(已完成)
        switch (producedStatus) {
            case 2: return "待生产";
            case 3: return "生产中";
            case 4: return "已发货";
            case 7: return "已结束";
            default: return "其他";
        }
    }


    /**
     * @Description: 获取支付类型对应的名称
     * @Author: Chen.zm
     * @Date: 2017/10/31 0031
     */
    public static String parsePaymentChannelName(String payType) {
        if (payType == null) return "";
        //支付渠道, ALIPAY:支付宝; WECHAT:微信; UPMP:银联 CASH:现金
        switch (payType) {
            case "ALIPAY": return "支付宝";
            case "WECHAT": return "微信";
            case "UPMP": return "微信";
            case "CASH": return "微信";
            default: return "其他";
        }
    }


    /**
     * @Description: 解析 线下线上渠道
     * @Author: Chen.zm
     * @Date: 2017/11/21 0021
     */
    public static String parseChannelType(String channelType) {
        if (channelType == null) return "";
        //渠道类型   1：线上渠道  2：线下渠道
        switch (channelType) {
            case "1": return "线上渠道";
            case "2": return "线下渠道";
            default: return "其他";
        }
    }


    /**
     * @Description: 解析设计师身份
     * @Author: Chen.zm
     * @Date: 2017/11/22 0022
     */
    public static String parseDesignerType(Integer type) {
        if (type == null) return "其它";
        //类型  1：在校学生   2：自由设计师   3：设计工作室   4：其它
        switch (type) {
            case 1:
                return "在校学生";
            case 2:
                return "自由设计师";
            case 3:
                return "设计工作室";
            case 4:
                return "其它";
        }
        return "其它";
    }


    /**
     * @Description: 定义：性别
     * @Author: Steven.Xiao
     * @Date: 2017/11/4
     */
    public static String parseSex(Integer type) {
        if (type == null) return "";
        //1：男  2：女
        switch (type) {
            case 1:
                return "男";
            case 2:
                return "女";
        }
        return "";
    }

    /**
     * @Description: 用户身份
     * @Author: Steven.Xiao
     * @Date: 2017/11/4
     */
    public static String parseUserType(Integer type) {
        if (type == null) return "";
        //1：网销店 2：零售门店 3：结婚领域流量平台 4：婚庆服务企业
        switch (type) {
            case 1:
                return "网销店";
            case 2:
                return "零售门店";
            case 3:
                return "结婚领域流量平台";
            case 4:
                return "婚庆服务企业";
        }
        return "";
    }

    /**
     * @Description: 门店状态
     * @Author: Steven.Xiao
     * @Date: 2017/11/4
     */
    public static String parseStoreStatus(Integer type) {
        if (type == null) return "未入驻";
        //0.待审核 1.审核通过 2.审核不通过
        switch (type) {
            case 0:
                return "待审核";
            case 1:
                return "审核通过";
            case 2:
                return "审核不通过";
        }
        return "";
    }

    /**
     * @Description: 销售订单状态
     * @Author: Steven.Xiao
     * @Date: 2017/11/7
     */
    public static String parseSalesOrderStatus(Integer type) {
        if (type == null) return "";
        //0：待发货  1：已发货
        switch (type) {
            case 0:
                return "待发货";
            case 1:
                return "已发货";
        }
        return "";
    }

    /**
     * @Description: 销售订单状态
     * @Author: Steven.Xiao
     * @Date: 2017/11/7
     */
    public static String parseSuplierOrderStatus(Integer type) {
        if (type == null) return "";
        //0：未采购  1：采购中 2:已完成
        switch (type) {
            case 0:
                return "未采购";
            case 1:
                return "采购中";
            case 2:
                return "已完成";
        }
        return "";
    }

    /**
     * @Description:结款状态
     * @Author: hanchao
     * @Date: 2017/12/18 0018
     */
    public static String getPayStatus(Integer type){
        if (type == null) return "";
        //0：全部  1：已结算 2:未结算
        switch (type) {
            case 0:
                return "全部";
            case 1:
                return "已结款";
            case 2:
                return "未结款";
        }
        return "";
    }


    /**
     * @Description: 解析盒子使用记录状态
     * @Author: Chen.zm
     * @Date: 2017/12/22 0022
     */
    public static String parseBoxUseLogStatus(Integer status) {
        if (status == null) return "";
        //状态  0：未发出    1：已发出   2：已退回   3：已完成
        switch (status) {
            case 0:
                return "未发出";
            case 1:
                return "已发出";
            case 2:
                return "已退回";
            case 3:
                return "已完成";
        }
        return "";
    }


    /**
     * @Description: 解析盒子使用记录对应商品的状态
     * @Author: Chen.zm
     * @Date: 2017/12/22 0022
     */
    public static String parseBoxUseLogProductStatus(Integer status) {
        if (status == null) return "";
        //状态  0：未支付    1：已支付
        switch (status) {
            case 0:
                return "未支付";
            case 1:
                return "已支付";
        }
        return "";
    }

    /**
     * @Description:盒子商品上下架状态
     * @Author: hanchao
     * @Date: 2017/12/23 0023
     */
    public static String parseBoxProductStatus(Integer status) {
        if (status == null) return "";
        //状态  0:已下架   1:已上架
        switch (status) {
            case 0:
                return "已下架";
            case 1:
                return "已上架";
        }
        return "";
    }


}
