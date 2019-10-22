package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 供应商订单备注记录
 */
@Entity
@Table(name = "sup_sales_order_remark_log")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_sales_order_remark_log", allocationSize=1)
public class SupSalesOrderRemarkLog extends GenericEntity {


	/**
	 * 供应商订单ID
	 */
	@Column(name = "order_id")
	private Integer orderId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SupSalesOrder supSalesOrder;

	/**
	 * 修改人名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 * 备注
	 */
	@Column(name = "remarks")
	private String remarks;

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public SupSalesOrder getSupSalesOrder() {
		return supSalesOrder;
	}

	public void setSupSalesOrder(SupSalesOrder supSalesOrder) {
		this.supSalesOrder = supSalesOrder;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
