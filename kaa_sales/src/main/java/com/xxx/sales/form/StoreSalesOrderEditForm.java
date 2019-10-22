package com.xxx.sales.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StoreSalesOrderEditForm {
    public Integer id;
    public String receiver;
    public String mobile;
    public String provinceId;
    public String provinceName;
    public String cityId;
    public String cityName;
    public String zoneId;
    public String zoneName;
    public String address;
    public Date expectDeliveryDate;
    public String remarks;
    public double total;

    public List<StoreSalesDetailSubmitForm> orderDetail=new ArrayList<>();
}
