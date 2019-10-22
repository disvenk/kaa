package com.xxx.sales.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateSuplierOrderForm {

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
    public Date expectsendDate;

    public List<StoreSuplierOrderDetailForm> orderDetail = new ArrayList<>();
}
