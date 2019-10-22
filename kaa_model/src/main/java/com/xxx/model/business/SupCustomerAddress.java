package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

/**
 * 客户地址信息
 */
@Entity
@Table(name = "sup_customer_address")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_customer_address", allocationSize=1)
public class SupCustomerAddress extends GenericEntity {

	/**
	 * 客户ID
	 */
	@Column(name = "customer_id")
	private Integer customerId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SupCustomer supCustomer;

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
	 * 详细地址
	 */
	@Column(name = "address")
	private String address;

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

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public SupCustomer getSupCustomer() {
		return supCustomer;
	}

	public void setSupCustomer(SupCustomer supCustomer) {
		this.supCustomer = supCustomer;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
}
