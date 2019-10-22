package com.xxx.sales.form;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SubmitOrderForm implements Serializable{

    public String orderNo;
    public Integer salesOrderId; //销售订单号
    public String province;
    public String provinceName;
    public String city;
    public String cityName;
    public String zone;
    public String zoneName;
    public String receiver;
    public String mobile;
    public String address;
    public String remarks;
    public String expectsendDate;

    public List<OrderDetailForm> orderDetail = new ArrayList<>();
}
