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
 * 盒子使用记录
 */
@Entity
@Table(name = "box_use_log")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="box_use_log", allocationSize=1)
public class BoxUseLog extends GenericEntity {

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
	 * 状态  0：未发出    1：已发出   2：已退回   3：已完成
	 */
	@Column(name = "status")
	private Integer status = 0;

	/**
	 * 使用记录号
	 */
	@Column(name = "order_no")
	private String orderNo;

	/**
	 * 快递公司Id —— 发出
	 */
	@Column(name = "issue_delivery_company")
	private Integer issueDeliveryCompany;

	/**
	 * 快递公司名称 —— 发出
	 */
	@Column(name = "issue_delivery_companyName")
	private String issueDeliveryCompanyName;

	/**
	 * 快递单号 —— 发出
	 */
	@Column(name = "issue_delivery_no")
	private String issueDeliveryNo;

	/**
	 * 快递公司Id —— 退回
	 */
	@Column(name = "return_delivery_company")
	private Integer returnDeliveryCompany;

	/**
	 * 快递公司名称 —— 退回
	 */
	@Column(name = "return_delivery_companyName")
	private String returnDeliveryCompanyName;

	/**
	 * 快递单号 —— 退回
	 */
	@Column(name = "return_delivery_no")
	private String returnDeliveryNo;

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
	 * 盒子使用记录 关联商品
	 */
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "boxUseLog")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<BoxUseLogProduct> boxUseLogProductList = new ArrayList<>();

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

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
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

	public Integer getIssueDeliveryCompany() {
		return issueDeliveryCompany;
	}

	public void setIssueDeliveryCompany(Integer issueDeliveryCompany) {
		this.issueDeliveryCompany = issueDeliveryCompany;
	}

	public String getIssueDeliveryCompanyName() {
		return issueDeliveryCompanyName;
	}

	public void setIssueDeliveryCompanyName(String issueDeliveryCompanyName) {
		this.issueDeliveryCompanyName = issueDeliveryCompanyName;
	}

	public String getIssueDeliveryNo() {
		return issueDeliveryNo;
	}

	public void setIssueDeliveryNo(String issueDeliveryNo) {
		this.issueDeliveryNo = issueDeliveryNo;
	}

	public Integer getReturnDeliveryCompany() {
		return returnDeliveryCompany;
	}

	public void setReturnDeliveryCompany(Integer returnDeliveryCompany) {
		this.returnDeliveryCompany = returnDeliveryCompany;
	}

	public String getReturnDeliveryCompanyName() {
		return returnDeliveryCompanyName;
	}

	public void setReturnDeliveryCompanyName(String returnDeliveryCompanyName) {
		this.returnDeliveryCompanyName = returnDeliveryCompanyName;
	}

	public String getReturnDeliveryNo() {
		return returnDeliveryNo;
	}

	public void setReturnDeliveryNo(String returnDeliveryNo) {
		this.returnDeliveryNo = returnDeliveryNo;
	}

	public List<BoxUseLogProduct> getBoxUseLogProductList() {
		return boxUseLogProductList;
	}

	public void setBoxUseLogProductList(List<BoxUseLogProduct> boxUseLogProductList) {
		this.boxUseLogProductList = boxUseLogProductList;
	}
}
