package com.xxx.suplier.form;


import java.io.Serializable;

public class JoinForm implements Serializable{
    public String name;
    public String phone;
    public Integer sex;
    public String personID;
    public String companyName; //公司名称
    public String address;
    public Double openYears;
    public String scope; //主营业务
    public Boolean scopeType = true; //是否微信图片
    public String smith; //车工
    public String sewer; //裁剪
    public String editer; //版型师
    public String modelSet; //模架
    public String description; //公司介绍
    public String qualifications; //资质信息

}
