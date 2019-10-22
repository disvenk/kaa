package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

/**
 * 工单收货信息表
 */
@Entity
@Table(name = "sup_order_delivery")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_order_delivery", allocationSize=1)
public class SupOrderDelivery extends GenericEntity {

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
	 * 客户id
	 */
	@Column(name = "customer_id")
	private Integer customerId;

	/**
	 * 客户
	 */
	@Column(name = "customer")
	private String customer;

	/**
	 * 客户手机号
	 */
	@Column(name = "customer_phone")
	private String customerPhone;

	/**
	 * 客户收件地址id
	 */
	@Column(name = "customer_address_id")
	private Integer customerAddressId;

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
	 * 市
	 */
	@Column(name = "city")
	private String city;

	/**
	 * 市名称
	 */
	@Column(name = "city_name")
	private String cityName;

	/**
	 * 区
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
	 * 交货日期
	 */
	@Column(name = "delivery_date")
	private Date deliveryDate;

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

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
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

	public SupOrder getSupOrder() {
		return supOrder;
	}

	public void setSupOrder(SupOrder supOrder) {
		this.supOrder = supOrder;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
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

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public Integer getCustomerAddressId() {
		return customerAddressId;
	}

	public void setCustomerAddressId(Integer customerAddressId) {
		this.customerAddressId = customerAddressId;
	}
}
