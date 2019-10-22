package com.xxx.suplier.form;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SuplierWorkerEditForm implements Serializable{
    public Integer id;
    public String code;
    public String name;
    public String phone;
    public String remark;
    public String updateDate;
    public Integer suplierId;
    public Integer workerType;

    public List<SuplierWorkerStationTypeForm> workerStationTypeList = new ArrayList<>();

}
