package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

/**
 * 门店销售订单的收件信息
 */
@Entity
@Table(name = "sto_salesorder_delivery")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sto_salesorder_delivery", allocationSize=1)
public class StoSalesOrderDelivery extends GenericEntity {

	/**
	 * 订单ID
	 */
	@Column(name = "order_id")
	private Integer orderId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private StoSalesOrder stoSalesOrder;

	/**
	 * 收件人
	 */
	@Column(name = "receiver")
	private String receiver;

	/**
	 * 收件电话
	 */
	@Column(name = "mobile")
	private String mobile;

	/**
	 * 省code
	 */
	@Column(name = "province")
	private String province;

	/**
	 * 省名称
	 */
	@Column(name = "province_name")
	private String provinceName;

	/**
	 * 市code
	 */
	@Column(name = "city")
	private String city;

	/**
	 * 市名称
	 */
	@Column(name = "city_name")
	private String cityName;

	/**
	 * 区code
	 */
	@Column(name = "zone")
	private String zone;

	/**
	 * 区名称
	 */
	@Column(name = "zone_name")
	private String zoneName;

	/**
	 * 详细地址
	 */
	@Column(name = "address")
	private String address;

	/**
	 * 期望发货日期
	 */
	@Column(name = "expectsend_date")
	private Date expectsendDate;

	/**
	 * 快递公司Id
	 */
	@Column(name = "delivery_company")
	private Integer deliveryCompany;

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

	/**
	 * 实际发货日期
	 */
	@Column(name = "delivery_date")
	private Date deliveryDate;

	public String getDeliveryCompanyName() {
		return deliveryCompanyName;
	}

	public void setDeliveryCompanyName(String deliveryCompanyName) {
		this.deliveryCompanyName = deliveryCompanyName;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public StoSalesOrder getStoSalesOrder() {
		return stoSalesOrder;
	}

	public void setStoSalesOrder(StoSalesOrder stoSalesOrder) {
		this.stoSalesOrder = stoSalesOrder;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getExpectsendDate() {
		return expectsendDate;
	}

	public void setExpectsendDate(Date expectsendDate) {
		this.expectsendDate = expectsendDate;
	}

	public Integer getDeliveryCompany() {
		return deliveryCompany;
	}

	public void setDeliveryCompany(Integer deliveryCompany) {
		this.deliveryCompany = deliveryCompany;
	}

	public String getDeliveryNo() {
		return deliveryNo;
	}

	public void setDeliveryNo(String deliveryNo) {
		this.deliveryNo = deliveryNo;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
}
