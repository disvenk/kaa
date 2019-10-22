package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 供应商工人所属工位
 */
@Entity
@Table(name = "sup_worker_station")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_worker_station", allocationSize=1)
public class SupWorkerStation extends GenericEntity {

    /**
     * 供应商工人ID
     */
    @Column(name = "worker_id")
    private Integer workerId;

    @JSONField
    @JsonIgnore
    @NotFound(action= NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
    private SupWorker supWorker;

    /**
     * 工序id
     */
    @Column(name = "procedure_id")
    private Integer procedureId;

    @JSONField
    @JsonIgnore
    @NotFound(action= NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procedure_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
    private SupProcedure supProcedure;

    public Integer getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

    public SupWorker getSupWorker() {
        return supWorker;
    }

    public void setSupWorker(SupWorker supWorker) {
        this.supWorker = supWorker;
    }

    public Integer getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(Integer procedureId) {
        this.procedureId = procedureId;
    }

    public SupProcedure getSupProcedure() {
        return supProcedure;
    }

    public void setSupProcedure(SupProcedure supProcedure) {
        this.supProcedure = supProcedure;
    }
}
