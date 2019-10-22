package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 供应商工序
 */
@Entity
@Table(name = "sup_procedure")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_procedure", allocationSize=1)
public class SupProcedure extends GenericEntity {

    /**
     * 供应商ID
     */
    @Column(name = "suplier_id")
    private Integer suplierId;

    @JSONField
    @JsonIgnore
    @NotFound(action= NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suplier_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
    private SupSuplier supSuplier;

    /**
     * 工序名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 工序工价
     */
    @Column(name = "price",columnDefinition = "decimal(5,2)")
    private BigDecimal price;

    /**
     * 排序
     */
    @Column(name = "sort",columnDefinition = "double(5,2)")
    private Double sort;

    /**
     * 备注
     */
    @Column(name = "remarks")
    private String remarks;

    public Integer getSuplierId() {
        return suplierId;
    }

    public void setSuplierId(Integer suplierId) {
        this.suplierId = suplierId;
    }

    public SupSuplier getSupSuplier() {
        return supSuplier;
    }

    public void setSupSuplier(SupSuplier supSuplier) {
        this.supSuplier = supSuplier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Double getSort() {
        return sort;
    }

    public void setSort(Double sort) {
        this.sort = sort;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
