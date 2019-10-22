package com.xxx.admin.form;

import java.util.ArrayList;
import java.util.List;

public class StoreEditForm {

    //会员资料
    public Integer userId;
    public String icon;
    public String userCode;
    public String mobile;
    public String userName;
    public Integer sex;
    public Integer userType;
    public String remarks;

    //门店资料
    public Integer storeId;
    public String storeName;
    public String provinceId;
    public String provinceName;
    public String cityId;
    public String cityName;
    public String zoneId;
    public String  zoneName;
    public String address;
    public List<StoSalesCategoryForm> stoSalesCategoryList;//销售商品的集合
    public String storePicture;
    public String credentials;
    public String contact;
    public String contactTelephone;
    public String scope;
    public Integer approveStatus;

    public List<StoStorePictureForm> stoStorePictureList = new ArrayList<>();//图片集合

}
