package com.xxx.suplier.form;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SaveProductPriceForm implements Serializable{
    public Integer id;
    public List<ProductPriceForm> productPriceList = new ArrayList<>();

}
