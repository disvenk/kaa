package com.xxx.sales.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description:商品编辑保存的实体类
 * @Author: hanchao
 * @Date: 2017/10/26 0026
 */
public class StoreProductForm {
    public String brand;
    public Double sort;
    public Integer sales;//销量
    public Integer id;
    public String  name;
    public Integer pid;//平台商品id
    public String  href;
    public String  vedioUrl;//视频
    public Integer views;//浏览量
    public String  pno;//货号不用插入到商品库
    public Double price;//建议零售价
    public Double  suplierDay;//单件供货周期
    public Date    updateDate;
    public String  material;//材料说明
    public Integer colligate;//综合排序
    public String  remarks;
    public String  supplierRemarks;//规格备注
    public String  description;//商品图文详情
    public Integer categoryId;
    public Integer platProductId;
    public Integer status;//是否上架

    public List<StoreProductSupplierListForm> storeProductSupplierList=new ArrayList<>(); //

    public List<StoreProductPictureListForm> storeProductPictureList=new ArrayList<>();//商品与图片

    public List<StoreProductLabelListForm> storeProductLabelList=new ArrayList<>();//


}
