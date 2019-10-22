package com.xxx.sales.form;

import java.util.ArrayList;
import java.util.List;

public class SalesProductListForm {
    public List<CategoryIdForm> categoryIdList = new ArrayList<>();
    public List<LabelIdForm> labelIdList = new ArrayList<>();
    public String nameOrCode; //名称或者商品编码
    public Double startPrice;
    public Double endPrice;
    public Integer sortType; // 1:综合  2：销量  3：价格  4：最新
    public Integer pageNum;
}
