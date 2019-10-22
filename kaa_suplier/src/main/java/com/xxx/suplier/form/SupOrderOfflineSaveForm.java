package com.xxx.suplier.form;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SupOrderOfflineSaveForm implements Serializable{
    public Integer supOrderId;
    public String insideOrderNo;
    public String customer;
    public String customerPhone;
    public String remarks;
    public String receiver;
    public String mobile;
    public String address;
    public String province;
    public String provinceName;
    public String city;
    public String cityName;
    public String zone;
    public String zoneName;
    public String deliveryDate;
    public String pno;
    public String categoryName;
    public String color;
    public String size;
    public Double throatheight;
    public Double shoulder;
    public Double bust;
    public Double waist;
    public Double hipline;
    public Double height;
    public Double weight;
    public Integer qty;
    public Double outputPrice;
    public String material;
    public String technics;
    public String description;
    public Integer customerId;
    public Integer customerAddressId;

    public List<HrefForm> imgs = new ArrayList<>();

}
