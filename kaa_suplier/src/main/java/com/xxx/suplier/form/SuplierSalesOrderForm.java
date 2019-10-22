package com.xxx.suplier.form;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SuplierSalesOrderForm implements Serializable{
    public Integer id;
    public String orderNo;
    public String insideOrderNo;
    public Integer customerId;
    public String customer;
    public String customerPhone;
    public Integer customerAddressId;
    public String remarks;
    public String deliveryTime;
    public String description;

    public Integer pid;
    public String categoryName;
    public String color;
    public String size;
    public Double shoulder;
    public Double bust;
    public Double waist;
    public Double hipline;
    public Double height;
    public Double weight;
    public Double throatheight;
    public Integer qty;
    public Double price;
    public String material;
    public String technics;

    public List<ProductPictureForm> productPictureList = new ArrayList<>();
    public List<ProductProcedureForm> procedureList = new ArrayList<>();
    public List<ProductMaterialForm> materialList = new ArrayList<>();

}
