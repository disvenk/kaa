package com.xxx.admin.form;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UpdateSuplierOrderForm implements Serializable{

    public Integer id;
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

    public Integer status;
    public Integer deliveryCompany;
    public String deliveryCompanyName;
    public String deliveryNo;

    public List<UpdateSuplierOrderPidForm> pids = new ArrayList<>();



}
