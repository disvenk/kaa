package com.xxx.sales.form;

import java.util.ArrayList;
import java.util.List;

public class StoreProductListForm {
    public List<StoreProductCategoryIdListForm> plaProductCategory = new ArrayList<>();
    public List<StoreProductLabelIdListForm> plaProductLabelId = new ArrayList<>();
    public Integer labelId;//标签id
    public String name;
    public Double startPrice;//最低价格
    public Double endPrice;//最高价格
    public Integer sortType; // 1:综合  2,最新上线  3：销量  4：价格  3 ,周期
    public Integer pageNum;
    public Integer categoryId;
    public String productCode;
}
