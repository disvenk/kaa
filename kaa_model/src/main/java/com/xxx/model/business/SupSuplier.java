package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 供应商基本资料
 */
@Entity
@Table(name = "sup_suplier")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_suplier", allocationSize=1)
public class SupSuplier extends GenericEntity {

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
	 * 供应商编号
	 */
	@Column(name = "code")
	private String code;

	/**
	 * 供应商名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 * 联系人
	 */
	@Column(name = "contact")
	private String contact;

	/**
	 * 联系人电话
	 */
	@Column(name = "contact_phone")
	private String contactPhone;

//	/**
//	 * 公司名称
//	 */
//	@Column(name = "company_name")
//	private String companyName;

	/**
	 * 工厂地址
	 */
	@Column(name = "address")
	private String address;

	/**
	 * 开厂年限
	 */
	@Column(name = "open_years")
	private Double openYears;

	/**
	 * 工厂面积
	 */
	@Column(name = "size")
	private String size;

	/**
	 * 主营业务
	 */
	@Column(name = "scope")
	private String scope;

	/**
	 * 公司介绍
	 */
	@Column(name = "description")
	private String description;

	/**
	 * 设计师
	 */
	@Column(name = "designer")
	private String designer;

	/**
	 * 版型师
	 */
	@Column(name = "editer")
	private String editer;

	/**
	 * 车工
	 */
	@Column(name = "smith")
	private String smith;

	/**
	 * 裁剪
	 */
	@Column(name = "sewer")
	private String sewer;

	/**
	 * 模架  0：无   1：外模  2：内模
	 */
	@Column(name = "model_set")
	private String modelSet;

	/**
	 * 资质信息
	 */
	@Column(name = "qualifications")
	private String qualifications;

	/**
	 * 状态 0：待审核   1：审核成功  2:审核失败
	 */
	@Column(name = "approvestatus")
	private Integer approveStatus;

	/**
	 * 说明
	 */
	@Column(name = "explains")
	private String explains;

	public String getExplains() {
		return explains;
	}

	public void setExplains(String explains) {
		this.explains = explains;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Double getOpenYears() {
		return openYears;
	}

	public void setOpenYears(Double openYears) {
		this.openYears = openYears;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDesigner() {
		return designer;
	}

	public void setDesigner(String designer) {
		this.designer = designer;
	}

	public String getEditer() {
		return editer;
	}

	public void setEditer(String editer) {
		this.editer = editer;
	}

	public String getSmith() {
		return smith;
	}

	public void setSmith(String smith) {
		this.smith = smith;
	}

	public String getSewer() {
		return sewer;
	}

	public void setSewer(String sewer) {
		this.sewer = sewer;
	}

	public String getModelSet() {
		return modelSet;
	}

	public void setModelSet(String modelSet) {
		this.modelSet = modelSet;
	}

	public String getQualifications() {
		return qualifications;
	}

	public void setQualifications(String qualifications) {
		this.qualifications = qualifications;
	}

	public Integer getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(Integer approveStatus) {
		this.approveStatus = approveStatus;
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

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}


}
