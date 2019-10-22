package com.xxx.suplier.form;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProduceRecordEditForm implements Serializable{
    public Integer id;
    public String orderNo;
    public String supplierProductCode;
    public String href;
    public String categoryName;
    public String color;
    public Integer qty;
    public Integer getProducedStatus;

    public List<ProduceRecordStationTypeForm> produceRecordStationTypeList = new ArrayList<>();

}
