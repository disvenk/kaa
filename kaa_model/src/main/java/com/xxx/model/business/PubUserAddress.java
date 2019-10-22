package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 会员收件地址信息
 */
@Entity
@Table(name = "pub_user_address")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="pub_user_address", allocationSize=1)
public class PubUserAddress extends GenericEntity {


	/**
	 * 用户账号ID
	 */
	@Column(name = "user_id")
	private Integer userId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private PubUserLogin pubUserLogin;

	/**
	 * 收货人
	 */
	@Column(name = "receiver")
	private String receiver;

	/**
	 * 手机号
	 */
	@Column(name = "mobile")
	private String mobile;

	/**
	 * 电话
	 */
	@Column(name = "tel")
	private String tel;

	/**
	 * 省Code
	 */
	@Column(name = "province")
	private String province;

	/**
	 * 省名称
	 */
	@Column(name = "province_name")
	private String provinceName;

	/**
	 * 市Code
	 */
	@Column(name = "city")
	private String city;

	/**
	 * 市名称
	 */
	@Column(name = "city_name")
	private String cityName;

	/**
	 * 区Code
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
	 * 是否默认
	 */
	@Column(name = "is_default")
	private Boolean isDefault;

	public PubUserLogin getPubUserLogin() {
		return pubUserLogin;
	}

	public void setPubUserLogin(PubUserLogin pubUserLogin) {
		this.pubUserLogin = pubUserLogin;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
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

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Boolean getDefault() {
		return isDefault;
	}

	public void setDefault(Boolean aDefault) {
		isDefault = aDefault;
	}
}
