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
 * 门店前端的电脑下的订单
 */
@Entity
@Table(name = "sto_salesorder")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sto_salesorder", allocationSize=1)
public class StoSalesOrder extends GenericEntity {


	/**
	 * 销售订单号
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
	private StoSalesOrderDelivery stoSalesOrderDelivery;

	/**
	 * 状态  0：待发货    1：已发货
	 */
	@Column(name = "status")
	private Integer status = 0;

	/**
	 * 对应后面采购订单编号
	 */
	@Column(name = "orderno_suplier")
	private String ordernoSuplier;

	@JSONField
	@JsonIgnore
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id",referencedColumnName="salesorder_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	private StoSuplierOrder stoSuplierOrder;

	/**
	 * 采购状态  0：未采购    1：采购中    2：已完成
	 */
	@Column(name = "orderstatus_suplier")
	private Integer orderstatusSuplier = 0;

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
	 * 门店销售订单明细集合
	 */
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "stoSalesOrder")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<StoSalesOrderDetail> stoSalesOrderDetailList = new ArrayList<>();

	public List<StoSalesOrderDetail> getStoSalesOrderDetailList() {
		return stoSalesOrderDetailList;
	}

	public void setStoSalesOrderDetailList(List<StoSalesOrderDetail> stoSalesOrderDetailList) {
		this.stoSalesOrderDetailList = stoSalesOrderDetailList;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getOrdernoSuplier() {
		return ordernoSuplier;
	}

	public void setOrdernoSuplier(String ordernoSuplier) {
		this.ordernoSuplier = ordernoSuplier;
	}

	public Integer getOrderstatusSuplier() {
		return orderstatusSuplier;
	}

	public void setOrderstatusSuplier(Integer orderstatusSuplier) {
		this.orderstatusSuplier = orderstatusSuplier;
	}

	public StoStoreInfo getStoStoreInfo() {
		return stoStoreInfo;
	}

	public void setStoStoreInfo(StoStoreInfo stoStoreInfo) {
		this.stoStoreInfo = stoStoreInfo;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public StoSalesOrderDelivery getStoSalesOrderDelivery() {
		return stoSalesOrderDelivery;
	}

	public void setStoSalesOrderDelivery(StoSalesOrderDelivery stoSalesOrderDelivery) {
		this.stoSalesOrderDelivery = stoSalesOrderDelivery;
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

	public StoSuplierOrder getStoSuplierOrder() {
		return stoSuplierOrder;
	}

	public void setStoSuplierOrder(StoSuplierOrder stoSuplierOrder) {
		this.stoSuplierOrder = stoSuplierOrder;
	}
}
