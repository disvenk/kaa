package com.xxx.admin.form;

import java.util.ArrayList;
import java.util.List;

public class BoxProductListForm {
    public List<BoxProductCategoryIdListForm> plaProductCategory = new ArrayList<>();
    public List<BoxProductLabelIdListForm> plaProductLabelId = new ArrayList<>();
    public Integer labelId;//标签id
    public String name;
    public Double startPrice;//最低价格
    public Double endPrice;//最高价格
    public Integer pageNum;
    public Integer categoryId;
    public String productCode;
}
