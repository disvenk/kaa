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
 * 工单
 */
@Entity
@Table(name = "sup_order")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_order", allocationSize=1)
public class SupOrder extends GenericEntity {

	/**
	 * 工单编号
	 */
	@Column(name = "order_no")
	private String orderNo;

	/**
	 * 内部编号
	 */
	@Column(name = "inside_order_no")
	private String insideOrderNo;

	/**
	 * 采购订单ID
	 */
	@Column(name = "order_id")
	private Integer orderId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private StoSuplierOrder stoSuplierOrder;

	/**
	 * 供应商订单ID
	 */
	@Column(name = "sup_sales_order_id")
	private Integer supSalesOrderId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sup_sales_order_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SupSalesOrder supSalesOrder;

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
	 * 工单的明细    【* 目前工单与商品明细一对一
	 */
	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id",referencedColumnName="order_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SupOrderDetail supOrderDetail;

	/**
	 * 工单收货信息
	 */
	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id",referencedColumnName="order_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SupOrderDelivery supOrderDelivery;

	/**
	 * 生产状态  (括号中为对供应商订单的生产状态)
	 * 		1:待分配  2：待接单(待生产)   3：生产中   4：已发货  5：待质检  6：质检不合格  7：质检通过(已完成)  8：已入库  9：已取消  10：确认取消
	 * 		供应商订单   2：待生产  3：生产中   4：已发货  7：已完成
	 */
	@Column(name = "produced_status")
	private Integer producedStatus = 1;

	/**
	 * 订单类型  1：平台订单   2：供应商本地订单
	 */
	@Column(name = "order_type")
	private Integer orderType = 1;

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
	 * 重要说明
	 */
	@Column(name = "description")
	private String description;

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
	 * 结款状态//0：全部  1：已结算 2:未结算
	 */
	@Column(name = "pay_status")
	private String payStatus;

	/**
	 * 紧急状态   1：正常 2：紧急
	 */
	@Column(name = "urgent")
	private Integer urgent;

	/**
	 * 当前生产记录id
	 */
	@Column(name = "production_log_id")
	private Integer productionLogId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "production_log_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SupOrderProductionLog supOrderProductionLog;

	/**
	 * 工单生产记录
	 */
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "supOrder")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<SupOrderProductionLog> supOrderProductionLogList = new ArrayList<>();

	/**
	 * 工单备注记录
	 */
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "supOrder")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<SupOrderRemarkLog> supOrderRemarkLogList = new ArrayList<>();

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

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public StoSuplierOrder getStoSuplierOrder() {
		return stoSuplierOrder;
	}

	public void setStoSuplierOrder(StoSuplierOrder stoSuplierOrder) {
		this.stoSuplierOrder = stoSuplierOrder;
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

	public Integer getProducedStatus() {
		return producedStatus;
	}

	public void setProducedStatus(Integer producedStatus) {
		this.producedStatus = producedStatus;
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

	public SupOrderDetail getSupOrderDetail() {
		return supOrderDetail;
	}

	public void setSupOrderDetail(SupOrderDetail supOrderDetail) {
		this.supOrderDetail = supOrderDetail;
	}

	public String getInsideOrderNo() {
		return insideOrderNo;
	}

	public void setInsideOrderNo(String insideOrderNo) {
		this.insideOrderNo = insideOrderNo;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SupOrderDelivery getSupOrderDelivery() {
		return supOrderDelivery;
	}

	public void setSupOrderDelivery(SupOrderDelivery supOrderDelivery) {
		this.supOrderDelivery = supOrderDelivery;
	}

	public List<SupOrderProductionLog> getSupOrderProductionLogList() {
		return supOrderProductionLogList;
	}

	public void setSupOrderProductionLogList(List<SupOrderProductionLog> supOrderProductionLogList) {
		this.supOrderProductionLogList = supOrderProductionLogList;
	}

	public Integer getProductionLogId() {
		return productionLogId;
	}

	public void setProductionLogId(Integer productionLogId) {
		this.productionLogId = productionLogId;
	}

	public SupOrderProductionLog getSupOrderProductionLog() {
		return supOrderProductionLog;
	}

	public void setSupOrderProductionLog(SupOrderProductionLog supOrderProductionLog) {
		this.supOrderProductionLog = supOrderProductionLog;
	}

	public List<SupOrderRemarkLog> getSupOrderRemarkLogList() {
		return supOrderRemarkLogList;
	}

	public void setSupOrderRemarkLogList(List<SupOrderRemarkLog> supOrderRemarkLogList) {
		this.supOrderRemarkLogList = supOrderRemarkLogList;
	}

	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	public Integer getSupSalesOrderId() {
		return supSalesOrderId;
	}

	public void setSupSalesOrderId(Integer supSalesOrderId) {
		this.supSalesOrderId = supSalesOrderId;
	}

	public SupSalesOrder getSupSalesOrder() {
		return supSalesOrder;
	}

	public void setSupSalesOrder(SupSalesOrder supSalesOrder) {
		this.supSalesOrder = supSalesOrder;
	}

	public Integer getUrgent() {
		return urgent;
	}

	public void setUrgent(Integer urgent) {
		this.urgent = urgent;
	}
}
