package com.xxx.suplier.form;


import java.io.Serializable;

public class ProduceRecordListForm implements Serializable{
    public Integer pageNum;
    public String orderNo;//工单号
    public String productCode;//平台商品单号
    public String supplierProductCode;//供应商产品编号
    public String procedureType;//工序
    public Integer producedStatus;//工单状态
    public Integer timeStatus;//1:超过 24小时
}
