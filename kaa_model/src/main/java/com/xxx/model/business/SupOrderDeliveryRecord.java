package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

/**
 * 工单快递信息
 */
@Entity
@Table(name = "sup_order_delivery_record")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_order_delivery_record", allocationSize=1)
public class SupOrderDeliveryRecord extends GenericEntity {

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
	 * 快递公司Id
	 */
	@Column(name = "delivery_company")
	private Integer deliveryCompany;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "delivery_company", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private PlaProductBase plaProductBase;

	/**
	 * 快递公司名称
	 */
	@Column(name = "delivery_companyName")
	private String deliveryCompanyName;

	/**
	 * 快递单号
	 */
	@Column(name = "delivery_no")
	private String deliveryNo;

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

	public Integer getDeliveryCompany() {
		return deliveryCompany;
	}

	public void setDeliveryCompany(Integer deliveryCompany) {
		this.deliveryCompany = deliveryCompany;
	}

	public String getDeliveryCompanyName() {
		return deliveryCompanyName;
	}

	public void setDeliveryCompanyName(String deliveryCompanyName) {
		this.deliveryCompanyName = deliveryCompanyName;
	}

	public String getDeliveryNo() {
		return deliveryNo;
	}

	public void setDeliveryNo(String deliveryNo) {
		this.deliveryNo = deliveryNo;
	}

	public PlaProductBase getPlaProductBase() {
		return plaProductBase;
	}

	public void setPlaProductBase(PlaProductBase plaProductBase) {
		this.plaProductBase = plaProductBase;
	}
}
