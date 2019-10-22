package com.xxx.suplier.form;

import java.util.ArrayList;
import java.util.List;

public class CategorySaveForm {
    public Integer id;
    public String name;
    public String remarks;
    public Integer type;
    public Integer pageNum;
    public List<SupProductBaseForm> list = new ArrayList<>();
}
