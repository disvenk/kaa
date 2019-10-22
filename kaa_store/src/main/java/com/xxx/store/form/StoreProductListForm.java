package com.xxx.store.form;


import java.io.Serializable;

public class StoreProductListForm implements Serializable{
    public Integer categoryId;
    public Integer sortType; //1:综合  2：销量  3：价格  4：最新
    public Double startPrice;
    public Double endPrice;
    public Integer pageNum;

}
