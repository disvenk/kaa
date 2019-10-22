package com.xxx.admin.form;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:商品编辑保存的实体类
 * @Author: hanchao
 * @Date: 2017/10/26 0026
 */
public class ProductForm {
    public Double sort;
    public Integer id;
    public String  name;
    public String  href;
    public String  brand;
    public String  vedioUrl;//视频
//    public String  pno;
    public Integer pid;//商品id
    public Double  prices;//建议零售价
//    public Integer productSupplierId;//
    public Double  suplierDay;//单件供货周期
    public String  technics;//工艺说明
    public String  material;//材料说明
    public Boolean isDesigner;//是否设计师
    public Integer colligate;//综合排序
    public Integer sales;//销量
    public Integer views;//浏览量
    public String  updateDate;
    public String  remarks;
    public String  description;//商品图文详情
    public Integer categoryId;



    public List<ProductSupplierListForm> productSupplierList = new ArrayList<>();

    public List<ProductPictureListForm> productPictureList = new ArrayList<>();//图片集合

    public List<ProductLabelListForm> productLabelList = new ArrayList<>();//标签集合

    public List<ProductSchannelListForm> productSchannelList = new ArrayList<>();//销售渠道集合
}
