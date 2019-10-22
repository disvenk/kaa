package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

/**
 * 工单生产记录
 */
@Entity
@Table(name = "sup_order_production_log")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_order_production_log", allocationSize=1)
public class SupOrderProductionLog extends GenericEntity {

	/**
	 * 工单ID
	 */
	@Column(name = "order_id")
	private Integer orderId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SupOrder supOrder;

    /**
     * 工序类型     【弃用
     *      1:打版
     *      2:车位
     *      3:排花
     *      4:裁剪
     *      5:质检
     */
//    @Column(name = "procedure_type")
//    private String procedureType;

    /**
     * 供应商订单商品工序id
     */
    @Column(name = "detail_procedure_id")
    private Integer detailProcedureId;

    @JSONField
    @JsonIgnore
    @NotFound(action= NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detail_procedure_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
    private SupSalesOrderDetailProcedure supSalesOrderDetailProcedure;

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
     * 生产时间
     */
    @Column(name = "production_date")
    private Date productionDate;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public SupOrder getSupOrder() {
        return supOrder;
    }

    public void setSupOrder(SupOrder supOrder) {
        this.supOrder = supOrder;
    }


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

    public Date getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(Date productionDate) {
        this.productionDate = productionDate;
    }

    public Integer getDetailProcedureId() {
        return detailProcedureId;
    }

    public void setDetailProcedureId(Integer detailProcedureId) {
        this.detailProcedureId = detailProcedureId;
    }

    public SupSalesOrderDetailProcedure getSupSalesOrderDetailProcedure() {
        return supSalesOrderDetailProcedure;
    }

    public void setSupSalesOrderDetailProcedure(SupSalesOrderDetailProcedure supSalesOrderDetailProcedure) {
        this.supSalesOrderDetailProcedure = supSalesOrderDetailProcedure;
    }
}
