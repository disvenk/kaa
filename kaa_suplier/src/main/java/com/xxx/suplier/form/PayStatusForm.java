package com.xxx.suplier.form;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PayStatusForm implements Serializable{
    public List<IdForm> ids = new ArrayList<>();
    public String payStatus;

}
