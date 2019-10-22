package com.xxx.store.form;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SubmitOrderForm implements Serializable{

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
    public String expectsendTime;

    public List<OrderDetailForm> orderDetail = new ArrayList<>();
}
