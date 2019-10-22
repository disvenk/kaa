package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 供应商工人
 */
@Entity
@Table(name = "sup_worker")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_worker", allocationSize=1)
public class SupWorker extends GenericEntity {

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
     * 工人编号
     */
    @Column(name = "code")
    private String code;

    /**
     * 工人名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 手机号
     */
    @Column(name = "phone")
    private String phone;

//    /**
//     * 操作权限  0:无权限   1：有权限
//     */
//    @Column(name = "worker_type")
//    private Integer workerType = 0;

    /**
     * 备注
     */
    @Column(name = "remarks")
    private String remarks;

    /**
     * 供应商工人所属工位
     */
    @JSONField(serialize = false)
    @ManyToMany(mappedBy = "supWorker")
    @org.hibernate.annotations.ForeignKey(name = "none")
    private List<SupWorkerStation> supWorkerStationList = new ArrayList<>();

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<SupWorkerStation> getSupWorkerStationList() {
        return supWorkerStationList;
    }

    public void setSupWorkerStationList(List<SupWorkerStation> supWorkerStationList) {
        this.supWorkerStationList = supWorkerStationList;
    }

}
