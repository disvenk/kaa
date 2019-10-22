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
 * 门店的采购订单
 */
@Entity
@Table(name = "sto_suplierorder")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sto_suplierorder", allocationSize=1)
public class StoSuplierOrder extends GenericEntity {


	/**
	 * 订单号
	 */
	@Column(name = "orderno")
	private String orderNo;

	/**
	 * 订单时间
	 */
	@Column(name = "orderdate")
	private Date orderDate;

	/**
	 * 门店ID
	 */
	@Column(name = "store_id")
	private Integer storeId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private StoStoreInfo stoStoreInfo;

	/**
	 * 收件记录
	 */
	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id",referencedColumnName="order_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private StoSuplierOrderDelivery stoSuplierOrderDelivery;


	/**
	 * 状态  0：待支付   1:待发货   2：已完成  3：已取消  4：待收货
	 */
	@Column(name = "status")
	private Integer status = 0;

	/**
	 * 对应销售订单ID
	 */
	@Column(name = "salesorder_id")
	private Integer salesOrderId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "salesorder_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private StoSalesOrder stoSalesOrder;

	/**
	 * 订单总价
	 */
	@Column(name = "total")
	private Double total;

	/**
	 * 实际支付
	 */
	@Column(name = "actual_pay")
	private Double actualPay;

	/**
	 * 备注
	 */
	@Column(name = "remarks")
	private String remarks;

	/**
	 * 渠道类型   1：线上渠道  2：线下渠道
	 */
	@Column(name = "channel_type")
	private Integer channelType;

	/**
	 * 渠道名称
	 */
	@Column(name = "channel_name")
	private String channelName;

	/**
	 * 门店采购订单商品明细集合
	 */
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "stoSuplierOrder")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<StoSuplierOrderDetail> stoSuplierOrderDetailList = new ArrayList<>();


	public Integer getSalesOrderId() {
		return salesOrderId;
	}

	public void setSalesOrderId(Integer salesOrderId) {
		this.salesOrderId = salesOrderId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public StoStoreInfo getStoStoreInfo() {
		return stoStoreInfo;
	}

	public void setStoStoreInfo(StoStoreInfo stoStoreInfo) {
		this.stoStoreInfo = stoStoreInfo;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public StoSalesOrder getStoSalesOrder() {
		return stoSalesOrder;
	}

	public void setStoSalesOrder(StoSalesOrder stoSalesOrder) {
		this.stoSalesOrder = stoSalesOrder;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Double getActualPay() {
		return actualPay;
	}

	public void setActualPay(Double actualPay) {
		this.actualPay = actualPay;
	}

	public StoSuplierOrderDelivery getStoSuplierOrderDelivery() {
		return stoSuplierOrderDelivery;
	}

	public void setStoSuplierOrderDelivery(StoSuplierOrderDelivery stoSuplierOrderDelivery) {
		this.stoSuplierOrderDelivery = stoSuplierOrderDelivery;
	}

	public List<StoSuplierOrderDetail> getStoSuplierOrderDetailList() {
		return stoSuplierOrderDetailList;
	}

	public void setStoSuplierOrderDetailList(List<StoSuplierOrderDetail> stoSuplierOrderDetailList) {
		this.stoSuplierOrderDetailList = stoSuplierOrderDetailList;
	}

	public Integer getChannelType() {
		return channelType;
	}

	public void setChannelType(Integer channelType) {
		this.channelType = channelType;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
}
