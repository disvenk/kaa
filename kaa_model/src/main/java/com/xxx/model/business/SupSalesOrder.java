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
 * 供应商订单
 */
@Entity
@Table(name = "sup_sales_order")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_sales_order", allocationSize=1)
public class SupSalesOrder extends GenericEntity {

	/**
	 * 订单编号
	 */
	@Column(name = "order_no")
	private String orderNo;

	/**
	 * 内部编号
	 */
	@Column(name = "inside_order_no")
	private String insideOrderNo;

	/**
	 * 供应商id
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
	 * 订单明细
	 */
	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id",referencedColumnName="order_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SupSalesOrderDetail supSalesOrderDetail;

	/**
	 * 订单收货信息
	 */
	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id",referencedColumnName="order_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SupSalesOrderDelivery supSalesOrderDelivery;

	/**
	 * 生产状态
	 * 		供应商订单   2：待生产  3：生产中   4：已发货  7：已完成
	 */
	@Column(name = "produced_status")
	private Integer producedStatus = 2;

	/**
	 * 重要说明
	 */
	@Column(name = "description")
	private String description;

	/**
	 * 订单总价
	 */
	@Column(name = "total")
	private Double total;

	/**
	 * 备注
	 */
	@Column(name = "remarks")
	private String remarks;

	/**
	 * 结款状态//0：全部  1：已结算 2:未结算
	 */
	@Column(name = "pay_status")
	private Integer payStatus;

	/**
	 * 备注记录
	 */
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "supSalesOrder")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<SupSalesOrderRemarkLog> supSalesOrderRemarkLogList = new ArrayList<>();

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getInsideOrderNo() {
		return insideOrderNo;
	}

	public void setInsideOrderNo(String insideOrderNo) {
		this.insideOrderNo = insideOrderNo;
	}

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

	public SupSalesOrderDetail getSupSalesOrderDetail() {
		return supSalesOrderDetail;
	}

	public void setSupSalesOrderDetail(SupSalesOrderDetail supSalesOrderDetail) {
		this.supSalesOrderDetail = supSalesOrderDetail;
	}

	public SupSalesOrderDelivery getSupSalesOrderDelivery() {
		return supSalesOrderDelivery;
	}

	public void setSupSalesOrderDelivery(SupSalesOrderDelivery supSalesOrderDelivery) {
		this.supSalesOrderDelivery = supSalesOrderDelivery;
	}

	public Integer getProducedStatus() {
		return producedStatus;
	}

	public void setProducedStatus(Integer producedStatus) {
		this.producedStatus = producedStatus;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}

	public List<SupSalesOrderRemarkLog> getSupSalesOrderRemarkLogList() {
		return supSalesOrderRemarkLogList;
	}

	public void setSupSalesOrderRemarkLogList(List<SupSalesOrderRemarkLog> supSalesOrderRemarkLogList) {
		this.supSalesOrderRemarkLogList = supSalesOrderRemarkLogList;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}
}
