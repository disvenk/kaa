package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 门店采购订单支付、发票记录
 */
@Entity
@Table(name = "sto_suplierorder_pay")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sto_suplierorder_pay", allocationSize=1)
public class StoSuplierOrderPay extends GenericEntity {


	/**
	 * 门店id
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
	 * 采购订单id
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
	 * 支付状态  0：未支付,  1：已支付
	 */
	@Column(name = "paystatus")
	private Integer payStatus;

	/**
	 * 支付方式:  0：支付宝,   1：微信
	 */
	@Column(name = "paytype")
	private Integer payType;

	/**
	 * 是否需要发票:   0：不需要, 1：需要
	 */
	@Column(name = "needinvoice")
	private Integer needInvoice;

	/**
	 * 发票类型  0：普通商业发票，  1：增值税发票
	 */
	@Column(name = "invoicetype")
	private Integer invoiceType;

	/**
	 * 开票抬头公司
	 */
	@Column(name = "invoicecompany")
	private String invoiceCompany;

	/**
	 * 纳税人识别号
	 */
	@Column(name = "invoicetaxcode")
	private String invoicetaxcode;

	/**
	 * 支付成功的二维码id
	 */
	@Column(name = "qr_id")
	private Integer qrId;

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

	public Integer getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Integer getNeedInvoice() {
		return needInvoice;
	}

	public void setNeedInvoice(Integer needInvoice) {
		this.needInvoice = needInvoice;
	}

	public Integer getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(Integer invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getInvoiceCompany() {
		return invoiceCompany;
	}

	public void setInvoiceCompany(String invoiceCompany) {
		this.invoiceCompany = invoiceCompany;
	}

	public String getInvoicetaxcode() {
		return invoicetaxcode;
	}

	public void setInvoicetaxcode(String invoicetaxcode) {
		this.invoicetaxcode = invoicetaxcode;
	}

	public Integer getQrId() {
		return qrId;
	}

	public void setQrId(Integer qrId) {
		this.qrId = qrId;
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
}
