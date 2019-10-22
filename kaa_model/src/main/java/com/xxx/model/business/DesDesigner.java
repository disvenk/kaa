package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 设计师的资料主档
 */
@Entity
@Table(name = "des_designer")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="des_designer", allocationSize=1)
public class DesDesigner extends GenericEntity {

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
	 *	类型  1：在校学生   2：自由设计师   3：设计工作室   4：其它
	 */
	@Column(name = "type")
	private Integer type;

	/**
	 *  个人简历
	 */
	@Column(name = "resume")
	private String resume;

	/**
	 * 图文详情
	 */
	@Column(name = "description")
	private String description;

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
	 *	详细地址
	 */
	@Column(name = "address")
	private String address;

	/**
	 *	备注
	 */
	@Column(name = "remarks")
	private String remarks;

	/**
	 *	是否展示
	 */
	@Column(name = "is_show")
	private Boolean isShow;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getResume() {
		return resume;
	}

	public void setResume(String resume) {
		this.resume = resume;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Boolean getShow() {
		return isShow;
	}

	public void setShow(Boolean show) {
		isShow = show;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
