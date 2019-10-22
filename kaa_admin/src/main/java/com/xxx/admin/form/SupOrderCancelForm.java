package com.xxx.admin.form;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SupOrderCancelForm implements Serializable{
    public String instruction;
    public List<IdForm> ids = new ArrayList<>();
}
