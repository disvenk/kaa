package com.xxx.admin.form;

import java.util.ArrayList;
import java.util.List;

    /**
    * @Description:销售平台商品编辑保存的实体类
    * @Author: hanchao
    * @Date: 2017/11/17 0017
    */
public class SalesProductManageForm {
    public String brand;
    public Double sort;
    public Integer sales;//销量
    public Integer id;//商品id
    public String  name;
    public String  href;
    public String  vedioUrl;//视频
    public Integer views;//浏览量
    public String  pno;//货号不用插入到商品库
    public Double price;//建议零售价
    public Double  suplierDay;//单件供货周期
    public String  material;//材料说明
    public Integer colligate;//综合排序
    public String  remarks;
    public String  description;//商品图文详情
    public Integer categoryId;
    public Integer platProductId;//平台商品id
    public Integer status;//是否上架

    public List<SalesProductManagePriceListForm> salesProductPriceList=new ArrayList<>(); //商品明细集合

    public List<SalesProductManagePictureListForm> salesProductPictureList=new ArrayList<>();//商品图片集合

    public List<SalesProductManageLabelListForm> salesProductLabelList=new ArrayList<>();//商品标签集合


}
