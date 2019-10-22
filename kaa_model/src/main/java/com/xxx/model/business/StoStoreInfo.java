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
 * 门店的主体信息表
 */
@Entity
@Table(name = "sto_storeinfo")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sto_storeinfo", allocationSize=1)
public class StoStoreInfo extends GenericEntity {

	/**
	 * 用户ID
	 */
	@Column(name = "user_id")
	private Integer userId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private PubUserLogin pubUserLogin ;

	/**
	 *	门店名称
	 */
	@Column(name = "storename")
	private String storeName;

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
	 * 联系人
	 */
	@Column(name = "contact")
	private String contact;

	/**
	 * 联系人电话
	 */
	@Column(name = "contactphone")
	private String contactPhone;

	/**
	 * 门店图片  主图
	 */
	@Column(name = "storepicture")
	private String storepicture;

	/**
	 * 资质照片
	 */
	@Column(name = "credentials")
	private String credentials;

	/**
	 * 主营业务
	 */
	@Column(name = "scope")
	private String scope;

	/**
	 * 状态  0：待审核  1：审核通过   2：审核不通过   null:未提交过审核记录
	 */
	@Column(name = "storestatus")
	private Integer storeStatus;

	/**
	 * 门店图片集合
	 */
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "stoStoreInfo")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<StoStorePicture> stoStorePictureList = new ArrayList<>();

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public PubUserLogin getPubUserLogin() {
		return pubUserLogin;
	}

	public void setPubUserLogin(PubUserLogin pubUserLogin) {
		this.pubUserLogin = pubUserLogin;
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

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
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

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getStorepicture() {
		return storepicture;
	}

	public void setStorepicture(String storepicture) {
		this.storepicture = storepicture;
	}

	public String getCredentials() {
		return credentials;
	}

	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}

	public Integer getStoreStatus() {
		return storeStatus;
	}

	public void setStoreStatus(Integer storeStatus) {
		this.storeStatus = storeStatus;
	}

	public List<StoStorePicture> getStoStorePictureList() {
		return stoStorePictureList;
	}

	public void setStoStorePictureList(List<StoStorePicture> stoStorePictureList) {
		this.stoStorePictureList = stoStorePictureList;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
}
