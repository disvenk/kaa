package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 工单收货记录
 */
@Entity
@Table(name = "sup_order_receipt_log")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_order_receipt_log", allocationSize=1)
public class SupOrderReceiptLog extends GenericEntity {


	/**
	 * 工单编号
	 */
	@Column(name = "order_no")
	private String orderNo;

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
     * 操作人
     */
    @Column(name = "name")
    private String name;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
