package com.xxx.suplier.form;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SuplierProductForm implements Serializable{
    public Integer id;
    public String pno;
    public String name;
    public Integer categoryId;
    public String material;
    public String technics;
    public String description;
    public Double shoulder;
    public Double bust;
    public Double waist;
    public Double hipline;
    public Double height;
    public Double weight;
    public Double throatheight;
    public Double price;
    public String remarks;

//    public List<IdForm> pictureList = new ArrayList<>();
    public List<ProductPictureForm> productPictureList = new ArrayList<>();
    public List<ProductPriceForm> productPriceList = new ArrayList<>();
    public List<ProductProcedureForm> procedureList = new ArrayList<>();
    public List<ProductMaterialForm> materialList = new ArrayList<>();

}
