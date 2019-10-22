package com.xxx.admin.form;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:供应商商品管理编辑数据保存
 * @Author: hanchao
 * @Date: 2017/10/26 0026
 */
public class SuplierManageGoodsForm {
    public Integer id;
    public String  name;
    public String  href;
    public String  brand;
    public String  pno;
    public Integer pid;//商品id
    public Double  suplierDay;//单件供货周期
    public Integer productSupplierId;
    public String  material;//材料说明
    public Integer sort;//排序
    public String  updateDate;
    public String  suplierRemark;//供应商备注
    public Integer categoryId;

    public List<SupplierGoodsListForm> productSupplierList = new ArrayList<>();

    public List<ProductPictureListForm> productPictureList = new ArrayList<>();//图片集合

}
