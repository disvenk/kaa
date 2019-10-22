package com.xxx.store.form;


import java.io.Serializable;

public class OrderDetailForm implements Serializable{

    public Integer shopcartId;
    public Integer pid;
    public Integer num;

    public String color;
    public String size;

    public Double shoulder;    //肩宽
    public Double bust;        //胸围
    public Double waist;       //腰围
    public Double hipline;     //臀围
    public Double height;      //身高
    public Double weight;      //体重
    public Double throatheight; //喉到地
    public String other;       //其它

}
