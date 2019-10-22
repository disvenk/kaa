package com.xxx.sales.form;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BoxUseLogForm implements Serializable{

    public String province;
    public String provinceName;
    public String city;
    public String cityName;
    public String zone;
    public String zoneName;
    public String receiver;
    public String mobile;
    public String address;

    public List<IdForm> ids = new ArrayList<>();
}
